package com.migrationcenter.tool.controller;

import com.migrationcenter.tool.data.model.ExtractObject;
import com.migrationcenter.tool.data.model.Job;
import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.service.JobService;
import com.migrationcenter.tool.service.ExtractionService;
import com.migrationcenter.tool.service.ProfileJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin
@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ProfileJobService profileJobService;

    @Autowired
    private ExtractionService extractionService;

    // Atomic counter for jobId generation
    private AtomicInteger jobCounter = new AtomicInteger(1);

    // ✅ Existing APIs
    @GetMapping("/api/job")
    public ResponseEntity<Job> getJobById(@RequestParam("jobId") String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        Job job = jobService.getJobById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }

    @GetMapping("/api/jobs")
    public ResponseEntity<List<Job>> getJobsByProfileId(@RequestParam("profileId") String profileId) {
        List<Job> jobs = jobService.getJobsByProfileId(profileId);
        if (jobs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(jobs);
    }

    @PatchMapping("/api/job/updateJobState")
    public ResponseEntity<Job> updateJobState(@RequestParam("jobId") String jobId,
                                              @RequestParam("state") String state) {
        Job updatedJob = jobService.updateJobState(jobId, state);
        if (updatedJob == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping("/api/job/profile")
    public ResponseEntity<Profile> getProfileByJobId(@RequestParam("jobId") String jobId) {
        Profile profile = jobService.getProfileByJobId(jobId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    // ✅ NEW APIs

    // 1. Update Mapping File
    @PostMapping("/api/job/update-mapping")
    public ResponseEntity<?> updateMappingFile(@RequestParam("jobId") String jobId,
                                               @RequestParam("mappingFile") MultipartFile mappingFile) {
        try {
            String baseUploadPath = System.getProperty("user.dir") + "/uploads";
            File uploadDir = new File(baseUploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String path = baseUploadPath + "/mapping_" + System.currentTimeMillis() + "_" + mappingFile.getOriginalFilename();
            File convFile = new File(path);
            mappingFile.transferTo(convFile);

            // Save mapping file in GridFS
            String mappingFileId = extractionService.addFile1(convFile, "job", jobId);

            // Patch to ExtractObject instead of Job
            extractionService.patchSourceFilePath(jobId, mappingFileId);

            return ResponseEntity.ok("Mapping file updated successfully for jobId: " + jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating mapping file: " + e.getMessage());
        }
    }


    // 2. Download Mapping File by Profile Name
    @GetMapping("/api/job/mapping-file")
    public ResponseEntity<?> getMappingFileByProfileName(@RequestParam("profileName") String profileName) {
        try {
            Profile profile = profileJobService.getProfileName(profileName).get(0);
            if (profile == null) {
                return ResponseEntity.badRequest().body("Profile not found: " + profileName);
            }
            File mappingFile = extractionService.downloadFile2(profile.getMappingFileId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mappingFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(mappingFile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching mapping file: " + e.getMessage());
        }
    }

    // 3. Download Transformed File
    @GetMapping("/api/job/download-transformed")
    public ResponseEntity<?> downloadTransformedFile(@RequestParam("jobId") String jobId) {
        try {
            // Get ExtractObject for this job
//            var extractObject = extractionService.getObjectById(jobId);
        	ExtractObject extractObject = extractionService.getObjectById(jobId);

            if (extractObject == null || extractObject.getTransformedFile() == null) {
                return ResponseEntity.badRequest().body("No transformed file for jobId: " + jobId);
            }

            File transformedFile = extractionService.downloadFile2(extractObject.getTransformedFile());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + transformedFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(transformedFile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error downloading transformed file: " + e.getMessage());
        }
    }


    // 4. Download Error-Analysis File
    @GetMapping("/api/job/download-error")
    public ResponseEntity<?> downloadErrorFile(@RequestParam("jobId") String jobId) {
        try {
//            var extractObject = extractionService.getObjectById(jobId);
        	ExtractObject extractObject = extractionService.getObjectById(jobId);

            if (extractObject == null || extractObject.getTransformedFileErrorAnalysis() == null) {
                return ResponseEntity.badRequest().body("No error file for jobId: " + jobId);
            }

            File errorFile = extractionService.downloadFile2(extractObject.getTransformedFileErrorAnalysis());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + errorFile.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(errorFile));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error downloading error file: " + e.getMessage());
        }
    }


    // 5. Generate JobId
    @GetMapping("/api/job/generate-id")
    public ResponseEntity<String> generateJobId(@RequestParam("profileName") String profileName) {
        String prefix = profileName.length() >= 3
                ? profileName.substring(0, 3).toLowerCase()
                : profileName.toLowerCase();
        int count = jobCounter.getAndIncrement();
        String jobId = prefix + "_job_" + count;
        return ResponseEntity.ok(jobId);
    }
}
