package com.migrationcenter.tool.controller;

import com.migrationcenter.tool.data.model.Job;
import com.migrationcenter.tool.service.JobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.data.model.ExtractObject;
import com.migrationcenter.tool.data.model.LoadFile;
import com.migrationcenter.tool.service.ExtractionService;
import com.migrationcenter.tool.service.ProfileJobService;
import com.mongodb.client.gridfs.model.GridFSFile;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.FileSystemResource;

@CrossOrigin
@RestController
public class ExtractionController {
	
    @Autowired
    ExtractionService eservice;

    @Autowired
    ProfileJobService profileJobService;

    @Autowired
    JobService jobService;
    
//    @Autowired
//    ExtractionService fileService;

//    @GetMapping("/extractSrcObject")
//    public Profile extractSrcObject(String profileName) {
//    	System.out.println(profileName);
//    	return eservice.extractSrcObject(profileName);
//
//    }
    
    @Autowired
    HttpServletResponse response;
 
    @GetMapping("/extractSrcDocument")
    public String extractSrcDocument(String profileName) {
    	return eservice.extractSrcDocuement(profileName);

    }

    @PostMapping("/pushdata")
    public ExtractObject pushFileToDB(
            @RequestParam("profileId") String profileId,
            @RequestPart("sourcefile") MultipartFile sourceFile,
            @RequestPart("mappingfile") MultipartFile mappingFile) throws IOException {

        // Fetch the profile using profileId
        Profile profile = profileJobService.getProfileById(profileId);
        if (profile == null) {
            throw new RuntimeException("Profile not found with id: " + profileId);
        }

        String jobId = UUID.randomUUID().toString();
        profile.addJobId(jobId); // Use addJobId() to add jobId to profile
        profileJobService.updateProfile(profile); // Save the updated profile with the new jobId
        Job newJob = new Job(profile.getName(), profileId, LocalDateTime.now(), "extracting");
        newJob.setId(jobId);
        jobService.saveJob(newJob);

        // Create a new ExtractObject
        ExtractObject obj = new ExtractObject();
        obj.setJobId(jobId); // Set the jobId for the extract object
        obj.setKeywords("Test");



        // Process the source file
        File conFile = new File(sourceFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(conFile)) {
            fos.write(sourceFile.getBytes());
        }
        System.out.println("SOURCE NAME " + sourceFile.getName());
//        System.out.println(sourceFile.g);
//        List<String> filePaths = eservice.extractFilePathsFromExcel(conFile);

        ResponseEntity<?> response = uploadTest(conFile, jobId, profile.getName());
        System.out.println(response);

        // Process the mapping file
        File mapFile = new File(mappingFile.getName());
        try (FileOutputStream mapfos = new FileOutputStream(mapFile)) {
            mapfos.write(mappingFile.getBytes());
        }
//        System.out.println(mapFile.getName());

        ResponseEntity<?> mapResponse = uploadTest(mapFile, jobId, profile.getName());
//        System.out.println(mapResponse);

        // Set the files in the ExtractObject
        obj.setSourceFile(response.getBody().toString());
        obj.setLoadedFile("");
        obj.setMappingFile(mapResponse.getBody().toString());
        obj.setTransformedFile("");
//        obj.setFilePaths(filePaths);
        System.out.println(obj);

        // Save the ExtractObject in the database
        return eservice.createNewExtractedObject(obj);
    }


//    @PostMapping("/pushdata")
//    public ExtractObject pushFileToDB(@RequestParam("profile") String profileName, @RequestParam("jobid") String jobid,@RequestPart("sourcefile") MultipartFile sourceFile,@RequestPart("mappingfile") MultipartFile mappingFile) throws IOException {
//    	ExtractObject obj = new ExtractObject();
//    	//ExtractionController exObj = new ExtractionController();
//    	obj.setJobId(profileName+"_"+jobid);
//    	obj.setKeywords("Test");
//    	File conFile = new File( sourceFile.getOriginalFilename() );
//        FileOutputStream fos = new FileOutputStream( conFile );
//        fos.write( sourceFile.getBytes() );
//        fos.close();
//        System.out.println(conFile.getName());
//    	ResponseEntity<?> response = uploadTest(conFile,jobid,profileName);
//    	System.out.println(response);
//
//    	File mapFile = new File( mappingFile.getOriginalFilename() );
//        FileOutputStream mapfos = new FileOutputStream( mapFile );
//        mapfos.write( mappingFile.getBytes() );
//        mapfos.close();
//        System.out.println(mappingFile.getName());
//    	ResponseEntity<?> mapResponse = uploadTest(mapFile,jobid,profileName);
//    	System.out.println(mapResponse);
//
//    	obj.setSourceFile(response.getBody().toString());
//    	obj.setLoadedFile("");
//    	obj.setMappingFile(mapResponse.getBody().toString());
//    	obj.setTransformedFile("");
//    	System.out.println(obj);
//    	return eservice.createNewExtractedObject(obj);
//    	//return "";
//    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file,@RequestParam("jobid") String jobid,@RequestParam("profile") String profile) throws IOException {
        return new ResponseEntity<>(eservice.addFile(file,profile,jobid), HttpStatus.OK);
    	//return eservice.addFile(file,profile,jobid);
    }
    
    public ResponseEntity<?> uploadTest(File file, String jobid, String profile) throws IOException {
        return new ResponseEntity<>(eservice.addFile1(file,profile,jobid), HttpStatus.OK);
    	//return eservice.addFile(file,profile,jobid);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) throws IOException {
        GridFsResource obj = eservice.downloadFile(id);
        System.out.println("ln 110 ExtractedObject" + obj);
//        FileOutputStream fo = new FileOutputStream(new File("./tmp"));
//        byte[] b = obj.getInputStream().readAllBytes();
//        
//        fo.write(b);
//        fo.close();
//        return fo;
        return ResponseEntity
                .ok()
                //.contentType(MediaType.parseMediaType(obj.getContentType()))
                .contentLength(obj.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + obj.getFilename()+ "."+obj.getOptions().getMetadata().get("extenstion")+"\"")
                .body(obj);

    }
}
