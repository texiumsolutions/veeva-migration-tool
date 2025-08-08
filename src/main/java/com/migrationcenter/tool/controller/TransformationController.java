package com.migrationcenter.tool.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.util.SystemOutLogger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.xb.ltgfmt.TestCase.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.migrationcenter.tool.data.model.ExtractObject;
import com.migrationcenter.tool.data.model.MainClass;
import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.data.model.TransformationFileInfo;
import com.migrationcenter.tool.data.model.TransformationFileInfoWithError;
import com.migrationcenter.tool.service.ExtractionService;
import com.migrationcenter.tool.service.ProfileJobService;
import com.migrationcenter.tool.service.TransformService;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;



@CrossOrigin
@RestController
public class TransformationController {
	
	 @Autowired
	 ProfileJobService profileJobService;
	 
	 @Autowired
	 ExtractionService eService;
	 
	 @Autowired
	 ExtractionController extractionController;
	 
	

//	@PostMapping("/mappingFile")
//    public String getFile(@RequestPart("mappingFile") MultipartFile mappingFile,@RequestPart("loaderFile") MultipartFile loaderFile) throws IOException {
//    	File convMappingFile = new File( mappingFile.getOriginalFilename() );
//        FileOutputStream fos = new FileOutputStream( convMappingFile );
//        fos.write( mappingFile.getBytes() );
//        fos.close();
//        System.out.println(convMappingFile.getName());
//        
//        File convLoaderFile = new File( loaderFile.getOriginalFilename() );
//        fos = new FileOutputStream(convLoaderFile);
//        fos.write( loaderFile.getBytes() );
//        fos.close();
//        
//        System.out.println(convLoaderFile.getName());
//        
//        TransformService.readMappingFile(convMappingFile);
//        TransformService.readObjectReferenceSheet(convMappingFile);
//        TransformService.readPicklistSheet(convMappingFile);
//        TransformService.readExcelFileAndTransform(convLoaderFile);
//        
//    	LocalDate today = LocalDate.now();
//    	Date date=new Date();
//    	String writePath = "./";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");			
//		String fileWritePath = writePath + "DocumentTransformed_Output_"+formatter.format(today)+"_"+date.getTime()+".xlsx";
//		TransformService.writeAllDataToExcel(fileWritePath);
//		System.out.println("Output File is created at "+ fileWritePath);
//		System.out.println("Absolute Path: " + new File(fileWritePath).getAbsolutePath());
//
//		//System.out.println(""+TransformService.readExcelFileAndTransform(convMappingFile));
//        return "SUCCESS";
//    }
	 
	 @PostMapping("/mappingFile")
	 public String getFile(@RequestParam("jobId") String jobId,  
	                       @RequestPart("mappingFile") MultipartFile mappingFile,
	                       @RequestPart("loaderFile") MultipartFile loaderFile) throws IOException {

	     // Existing file save logic...
	     File convMappingFile = new File(mappingFile.getOriginalFilename());
	     FileOutputStream fos = new FileOutputStream(convMappingFile);
	     fos.write(mappingFile.getBytes());
	     fos.close();

	     File convLoaderFile = new File(loaderFile.getOriginalFilename());
	     fos = new FileOutputStream(convLoaderFile);
	     fos.write(loaderFile.getBytes());
	     fos.close();

	     // Transformation Logic...
	     TransformService.readMappingFile(convMappingFile);
	     TransformService.readObjectReferenceSheet(convMappingFile);
	     TransformService.readPicklistSheet(convMappingFile);
	     TransformService.readExcelFileAndTransform(convLoaderFile);

	     String fileWritePath = "./data/output_" + System.currentTimeMillis() + ".xlsx";
	     File outputFile = TransformService.writeAllDataToExcel(fileWritePath);

	     // ✅ Upload Transformed File to GridFS
	     String transformedFileId = eService.addFile1(outputFile, "profileName", jobId);

	     // ✅ PATCH MongoDB with new transformedFileId
	     eService.patchTransformedFilePath(jobId, transformedFileId);

	     return "Transformation complete. File: " + outputFile.getName();
	 }


	
	
	
	@PostMapping("/transformworking")
	public String transformworking(@RequestParam("name") String name) {
		Profile p;
		try {
			p = profileJobService.getProfileName(name).get(0);
			System.out.println(p);
			if(p!=null) {
				
				File convMappingFile = new File(p.getMappingfilepath() );
		        System.out.println(convMappingFile.getName());
		        File convLoaderFile = new File(p.getLoaderfilepath());
		        System.out.println(convLoaderFile.getName());		        
		        
		        TransformService.readMappingFile(convMappingFile);
		        TransformService.readObjectReferenceSheet(convMappingFile);
		        TransformService.readPicklistSheet(convMappingFile);
		        TransformService.readExcelFileAndTransform(convLoaderFile);
		        
		    	LocalDate today = LocalDate.now();
		    	Date date=new Date();
		    	String writePath = "./";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");			
				String fileWritePath = writePath + "DocumentTransformed_Output_"+formatter.format(today)+"_"+date.getTime()+".xlsx";
				TransformService.writeAllDataToExcel(fileWritePath);
				System.out.println("Output File is created at "+ fileWritePath);
				return "Output File is created at "+ fileWritePath;
				
			}else {
				return "Profile with given name " + name + " not found" ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return "";
	}
	
	@GetMapping("/transform")
	public String transform(@RequestParam("jobid") String jobid) {
		ExtractObject p;
		try {
			p = eService.getObjectById(jobid);
			System.out.println(p);
			if(p!=null) {
				
//				GridFsResource gridFsResource = eService.downloadFile(p.getMappingFile());
//				System.out.println(gridFsResource.getGridFSFile().getChunkSize());
//				System.out.println(gridFsResource.getGridFSFile().getFilename());
//				//System.out.println(gridFsResource.getFile().getAbsolutePath());
//				System.out.println(gridFsResource.getFile().getCanonicalFile());;
				
//				InputStream data = eService.downloadFile(p.getMappingFile()).getContent();
//				byte[] buffer = data.readAllBytes();
//				data.read(buffer);
				
				File file = eService.downloadFile2(p.getSourceFile());
//				FileUtils.writeByteArrayToFile(convMappingFile, buffer);
				
				
//			    FileOutputStream outStream = new FileOutputStream(convMappingFile);
//			    outStream.write();
			    //IOUtils.closeQuietly(outStream);
				File sourceFile = file;
				
				//File convMappingFile = FileUtils.copyToFile("/test1.xlsx",targetFile );//new File(p.getMappingfilepath() );
		        System.out.println(sourceFile.getName());
		        //System.out.println(extractionController.download(p.getSourceFile()).getHeaders().getContentType());
		        System.out.println("146 "+ sourceFile);
		        //File convLoaderFile = extractionController.download(p.getSourceFile()).getBody().getFile();
		        file = eService.downloadFile2(p.getMappingFile());
		        //System.out.println(extractionController.download(p.getSourceFile()).getHeaders().getContentType());
		        File convLoaderFile = file;
		        System.out.println(convLoaderFile.getName());		        
		        
//		        TransformService.readMappingFile(convMappingFile);
//		        TransformService.readObjectReferenceSheet(convMappingFile);
//		        TransformService.readPicklistSheet(convMappingFile);
//		        TransformService.readExcelFileAndTransform(convLoaderFile);
		        
		    	LocalDate today = LocalDate.now();
		    	Date date=new Date();
		    	String writePath = "./";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");			
				String fileWritePath = writePath + "DocumentTransformed_Output_"+formatter.format(today)+"_"+date.getTime()+".xlsx";
				TransformService.writeAllDataToExcel(fileWritePath);
				System.out.println("Output File is created at "+ fileWritePath);
				return "Output File is created at "+ fileWritePath;
//				
			}else {
				return "Profile with given job id " + jobid + " not found" ;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return "";
	}
	
	@PostMapping("/transform1")
	public String transform1(@RequestParam("jobid") String jobid) {
		ExtractObject p;
//		try {
		p = eService.getObjectById(jobid);
		System.out.println(p);
		if(p!=null) {

//				String source = eService.downloadFile3(p.getSourceFile());
//				String mapping = eService.downloadFile3(p.getMappingFile());
			String source = "/Users/sachin/Documents/temp_data.xlsx";
			String mapping = "/Users/sachin/Documents/field-mapping-Sheet.xlsx";

			//File convMappingFile = FileUtils.copyToFile("/test1.xlsx",targetFile );//new File(p.getMappingfilepath() );
			//System.out.println(sourceFile.getName());
			//System.out.println(extractionController.download(p.getSourceFile()).getHeaders().getContentType());
			//System.out.println("146 "+ sourceFile);
			//File convLoaderFile = extractionController.download(p.getSourceFile()).getBody().getFile();
			//file = eService.downloadFile2(p.getMappingFile());
			//System.out.println(extractionController.download(p.getSourceFile()).getHeaders().getContentType());
			//File convLoaderFile = file;
			//System.out.println(convLoaderFile.getName());

			TransformService.readMappingFile(new File(mapping));
			TransformService.readObjectReferenceSheet(new File(mapping));
			TransformService.readPicklistSheet(new File(mapping));
			TransformService.readExcelFileAndTransform(new File(source));

			LocalDate today = LocalDate.now();
			Date date=new Date();
			String writePath = "./data/";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			String fileWritePath = writePath + "DocumentTransformed_Output_"+formatter.format(today)+"_"+date.getTime()+".xlsx";
			File transformedFile = TransformService.writeAllDataToExcel(fileWritePath);

//			System.out.println(eService.addFile1(transformedFile, jobid.split("_")[0], jobid));
			System.out.println("Output File is created at "+ fileWritePath);
			return "Output File is created at "+ fileWritePath;
//				
		}
//		else {
//			return "Profile with given job id " + jobid + " not found" ;
//		}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
		return "";
	}
	

	@GetMapping("/transformation-info")
	public ResponseEntity<?> getTransformationFiles(@RequestParam("jobid") String jobid) {
	    try {
	        ExtractObject job = eService.getObjectById(jobid);
	        if (job == null) {
	            return ResponseEntity.badRequest().body("No job found with jobid: " + jobid);
	        }
	
	        File sourceFile = eService.downloadFile2(job.getSourceFile());
	        File mappingFile = eService.downloadFile2(job.getMappingFile());
	
	        LocalDate today = LocalDate.now();
	        Date date = new Date();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	        String outputFile = "./DocumentTransformed_Output_" + formatter.format(today) + "_" + date.getTime() + ".xlsx";
	
	        TransformationFileInfo response = new TransformationFileInfo(
	            sourceFile.getName(),
	            mappingFile.getName(),
	            new File(outputFile).getName()
	        );
	
	        return ResponseEntity.ok(response);
	
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Something went wrong: " + e.getMessage());
	    }
	}

	
	@PostMapping("/transformation-summary")
	public ResponseEntity<?> uploadAndTransformWithSummary(
	        @RequestParam("sourceFile") MultipartFile sourceFile,
	        @RequestParam("mappingFile") MultipartFile mappingFile
	) {
	    try {
	        // ✅ Use absolute path based on project directory
	        String baseUploadPath = System.getProperty("user.dir") + "/uploads";
	        File uploadDir = new File(baseUploadPath);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdirs(); // create directory if it doesn't exist
	        }

	        // Save uploaded files
	        String timestamp = String.valueOf(System.currentTimeMillis());
	        String sourcePath = baseUploadPath + "/source_" + timestamp + "_" + sourceFile.getOriginalFilename();
	        String mappingPath = baseUploadPath + "/mapping_" + timestamp + "_" + mappingFile.getOriginalFilename();

	        File source = new File(sourcePath);
	        File mapping = new File(mappingPath);

	        sourceFile.transferTo(source);
	        mappingFile.transferTo(mapping);

	        // Generate output path
	        String outputFilePath = baseUploadPath + "/DocumentTransformed_Output_" + timestamp + ".xlsx";

	        // Run transformation
	        TransformService.resetErrorCount();
	        TransformService.readMappingFile(mapping);
	        TransformService.readObjectReferenceSheet(mapping);
	        TransformService.readPicklistSheet(mapping);
	        TransformService.readExcelFileAndTransform(source);
	        TransformService.writeAllDataToExcel(outputFilePath);

	        // Count mapping errors
	        File outputFile = new File(outputFilePath);
	        int mappingErrors = TransformService.countErrorCellsInExcel(outputFile);

	        // Append summary
	        TransformService.appendSummaryToExcel(outputFile, mappingErrors);

	        TransformationFileInfoWithError response = new TransformationFileInfoWithError(
	            source.getName(),
	            mapping.getName(),
	            outputFile.getName(),
	            mappingErrors
	        );

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Something went wrong: " + e.getMessage());
	    }
	}


//	@PostMapping("/transformation-error-report")
//	public ResponseEntity<?> uploadAndTransformWithErrorReport(
//	        @RequestParam("mappingFile") MultipartFile mappingFile,
//	        @RequestParam("loaderFile") MultipartFile loaderFile
//	) {
//	    try {
//	        String baseUploadPath = System.getProperty("user.dir") + "/uploads";
//	        File uploadDir = new File(baseUploadPath);
//	        if (!uploadDir.exists()) uploadDir.mkdirs();
//
//	        String timestamp = String.valueOf(System.currentTimeMillis());
//	        String mappingPath = baseUploadPath + "/mapping_" + timestamp + "_" + mappingFile.getOriginalFilename();
//	        String loaderPath = baseUploadPath + "/loader_" + timestamp + "_" + loaderFile.getOriginalFilename();
//
//	        File convMappingFile = new File(mappingPath);
//	        File convLoaderFile = new File(loaderPath);
//
//	        mappingFile.transferTo(convMappingFile);
//	        loaderFile.transferTo(convLoaderFile);
//
//	        // ✅ Perform Transformation Logic (keep your original flow untouched)
//	        TransformService.resetErrorCount();
//	        TransformService.readMappingFile(convMappingFile);
//	        TransformService.readObjectReferenceSheet(convMappingFile);
//	        TransformService.readPicklistSheet(convMappingFile);
//	        TransformService.readExcelFileAndTransform(convLoaderFile);
//
//	        // Generate initial output Excel (normal transformation output)
//	        String outputFilePath = baseUploadPath + "/DocumentTransformed_Output_" + timestamp + ".xlsx";
//	        TransformService.writeAllDataToExcel(outputFilePath);
//
//	        // ✅ Now open that file again and append "Errors Per Row" column
//	        File outputFile = new File(outputFilePath);
//	        TransformService.appendErrorsPerRowColumn(outputFile);
//
//	        
//	        TransformationFileInfoWithError response = new TransformationFileInfoWithError(
//	                convLoaderFile.getName(),
//	                convMappingFile.getName(),
//	                outputFile.getName(),
//	                TransformService.countErrorCellsInExcel(outputFile)
//	        );
//
//	        return ResponseEntity.ok(response);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.internalServerError().body("Something went wrong: " + e.getMessage());
//	    }
//	}
//

	@PostMapping("/transformation-error-report")
	public ResponseEntity<?> uploadAndTransformWithErrorReport(
	    @RequestParam("jobId") String jobId,
	    @RequestParam("mappingFile") MultipartFile mappingFile,
	    @RequestParam("loaderFile") MultipartFile loaderFile
	) {
	    try {
	        // File Save Logic
	        String baseUploadPath = System.getProperty("user.dir") + "/uploads";
	        File uploadDir = new File(baseUploadPath);
	        if (!uploadDir.exists()) uploadDir.mkdirs();

	        String timestamp = String.valueOf(System.currentTimeMillis());
	        String mappingPath = baseUploadPath + "/mapping_" + timestamp + "_" + mappingFile.getOriginalFilename();
	        String loaderPath = baseUploadPath + "/loader_" + timestamp + "_" + loaderFile.getOriginalFilename();

	        File convMappingFile = new File(mappingPath);
	        File convLoaderFile = new File(loaderPath);

	        mappingFile.transferTo(convMappingFile);
	        loaderFile.transferTo(convLoaderFile);

	        // ✅ Upload Loader File (SourceFile) to GridFS & Patch
	        String loaderFileId = eService.addFile1(convLoaderFile, "profileName", jobId);
	        eService.patchLoadedFilePath(jobId, loaderFileId);

	        // Transformation Logic
	        TransformService.resetErrorCount();
	        TransformService.readMappingFile(convMappingFile);
	        TransformService.readObjectReferenceSheet(convMappingFile);
	        TransformService.readPicklistSheet(convMappingFile);
	        TransformService.readExcelFileAndTransform(convLoaderFile);

	        // Write Initial Output Excel
	        String outputFilePath = baseUploadPath + "/DocumentTransformed_Output_" + timestamp + ".xlsx";
	        TransformService.writeAllDataToExcel(outputFilePath);

	        // Append Errors Per Row Column
	        File outputFile = new File(outputFilePath);
	        TransformService.appendErrorsPerRowColumn(outputFile);

	        // ✅ Upload Transformed File & Patch
	        String transformedFileId = eService.addFile1(outputFile, "profileName", jobId);
	        eService.patchTransformedFilePath(jobId, transformedFileId);

	        // ✅ Upload Error-Analysis File & Patch
	        String errorFileId = eService.addFile1(outputFile, "profileName", jobId);
	        eService.patchErrorAnalysisFilePath(jobId, errorFileId);

	        // Prepare Response
	        TransformationFileInfoWithError response = new TransformationFileInfoWithError(
	            convLoaderFile.getName(),
	            convMappingFile.getName(),
	            outputFile.getName(),
	            TransformService.countErrorCellsInExcel(outputFile)
	        );

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Something went wrong: " + e.getMessage());
	    }
	}

}
