package com.migrationcenter.tool.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.migrationcenter.tool.data.dao.ExtractionDAO;
import com.migrationcenter.tool.data.model.ExtractObject;
import com.migrationcenter.tool.data.model.LoadFile;
import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.entity.Extraction;


@Service
public class ExtractionService {
	
	@Autowired
	ExtractionDAO  extractionDAO;
	
	public ExtractObject createNewExtractedObject(ExtractObject obj) {
		// TODO Auto-generated method stub
	
		return extractionDAO.save(obj);
	}
	
	@Autowired
	private GridFsTemplate template;

	@Autowired
	private GridFsOperations operations;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	
	
	public String addFile(MultipartFile upload,String profile,String job) throws IOException {

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        metadata.put("extenstion", upload.getOriginalFilename());
        System.out.println(upload.getName());
        System.out.println(upload.getContentType());

        Object fileID = template.store(upload.getInputStream(), profile+job+"_E", upload.getContentType(), metadata);
        
        return fileID.toString();
    }
	
	public String addFile1(MultipartFile upload,String profile,String job) throws IOException {

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        metadata.put("extenstion", upload.getOriginalFilename());
        System.out.println(upload.getName());
        System.out.println(upload.getContentType());

        Object fileID = template.store(upload.getInputStream(), profile+job+"_E", upload.getContentType(), metadata);
        
        return fileID.toString();
    }
	public String addFile1(File upload,String profile,String job) throws IOException {
		
        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.length());
        metadata.put("extenstion", upload.getName());
        System.out.println(upload.getName());
        //System.out.println(upload.getContentType());

        Object fileID = template.store(FileUtils.openInputStream(upload), profile+job+"_E", null, metadata);

        return fileID.toString();
    }
	
	public GridFsResource downloadFile(String id) throws IOException {

        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );
        
        

        //System.out.println("File Name : "+gridFSFile.getFilename() +gridFSFile.getMetadata().get("_contentType").toString() );
        //ExtractObject loadFile = new ExtractObject();
//        LoadFile file = new LoadFile();
//
//        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
//            file.setFilename( gridFSFile.getFilename() );
//
//            file.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );
//
//            file.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );
//
//            file.setFile( IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) );
//        }
//        return file;

        GridFsResource resource = operations.getResource(gridFSFile);
        return resource;
        //return new GridFsResource(gridFSFile);
    }
	
	public File downloadFile2(String id) throws IOException {

        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );
        
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
        GridFsResource resource = operations.getResource(gridFSFile);
        byte[] array =   IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) ;
        System.out.println(array.length);
        File outputFile = new File("./data/"+gridFSFile.getFilename());
        try (FileOutputStream outputStream = new FileOutputStream("./data/"+resource.getOptions().getMetadata().get("extenstion"))) {
            outputStream.write(array);
            outputStream.close();
           
        }
        
        System.out.println("141 " + outputFile.length());
        System.out.println(outputFile.getName());
        
//        Path p = Paths.get("./data/test1.xlsx");
//        Path path = Files.write(p, array, null);
//        File file = path.toFile();
        return outputFile;
        }
        return null;
        //return new GridFsResource(gridFSFile);
    }
	
	public String downloadFile3(String id) throws IOException {

        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );
        
        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
        GridFsResource resource = operations.getResource(gridFSFile);
        byte[] array =   IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) ;
        System.out.println(array.length);
        String outputFile = "./data/"+resource.getOptions().getMetadata().get("extenstion");
        
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(array);
            
            outputStream.close();
            
        }
        //System.out.println("165 " + outputFile.length());
        //System.out.println(outputFile.getName());
        
//        Path p = Paths.get("./data/test1.xlsx");
//        Path path = Files.write(p, array, null);
//        File file = path.toFile();
        return outputFile;
        }
        return "";
        //return new GridFsResource(gridFSFile);
    }
	
	
	

    
//    public Profile extractSrcObject(String profileName){
//    	
//    	return extractionDAO.saveExtractedDocument("");
//    }
    
    public String extractSrcDocuement(String profileName) {
    	System.out.print("*****"+profileName );
        return "";
    }
    
    
	public ExtractObject getObjectById(String jobid) {
		// TODO Auto-generated method stub
		return extractionDAO.findExtractObjectByJobid(jobid);
		
	}
	
	
    public List<String> extractFilePathsFromExcel(File sourceFile) throws IOException {
        List<String> filePaths = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);  // Assuming data is in the first sheet
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                Cell cell = row.getCell(getFileColumnIndex(sheet));
                if (cell != null) {
                    String filePath = formatter.formatCellValue(cell);
                    filePaths.add(filePath);
                }
            }
        }
        return filePaths;
    }

    public int getFileColumnIndex(Sheet sheet) {
        // Assuming the first row contains the column names and "file" is the column header
        Row headerRow = sheet.getRow(0);
        for (Cell cell : headerRow) {
            if ("file".equalsIgnoreCase(cell.getStringCellValue())) {
                return cell.getColumnIndex();
            }
        }
        throw new RuntimeException("File column not found in the Excel sheet.");
    }

    
 
    
 // New method to update paths in ExtractObject
    public void updateFilePathsForJob(String jobId, String sourceFilePath, String transformedFilePath, String errorAnalysisFilePath) {
        Query query = new Query(Criteria.where("jobId").is(jobId));
        Update update = new Update()
                .set("sourceFile", sourceFilePath)
                .set("transformedFile", transformedFilePath)
                .set("transformedFileErrorAnalysis", errorAnalysisFilePath);

        mongoTemplate.updateFirst(query, update, ExtractObject.class);
    }
    
    
    
 // PATCH: Update Transformed File Path (Normal Transformation)
    public void patchTransformedFilePath(String jobId, String fileId) {
        Query query = new Query(Criteria.where("jobId").is(jobId));
        Update update = new Update().set("transformedFile", fileId);
        mongoTemplate.updateFirst(query, update, ExtractObject.class);
    }

    // PATCH: Update Error Analysis File Path (Error Column Transformation)
//    public void patchErrorAnalysisFilePath(String jobId, String errorFileId) {
//        Query query = new Query(Criteria.where("jobId").is(jobId));
//        Update update = new Update().set("transformedFileErrorAnalysis", errorFileId);
//        mongoTemplate.updateFirst(query, update, ExtractObject.class);
//    }

    public void patchSourceFilePath(String jobId, String fileId) {
        Query query = new Query(Criteria.where("jobId").is(jobId));
        Update update = new Update().set("sourceFile", fileId);
        mongoTemplate.updateFirst(query, update, ExtractObject.class);
    }

    public void patchErrorAnalysisFilePath(String jobId, String fileId) {
        Query query = new Query(Criteria.where("jobId").is(jobId));
        Update update = new Update().set("transformedFileErrorAnalysis", fileId);
        mongoTemplate.updateFirst(query, update, ExtractObject.class);
    }

    public void patchLoadedFilePath(String jobId, String loadedFileId) {
        Query query = new Query(Criteria.where("jobId").is(jobId));
        Update update = new Update().set("loadedFile", loadedFileId);
        mongoTemplate.updateFirst(query, update, ExtractObject.class);
    }





}