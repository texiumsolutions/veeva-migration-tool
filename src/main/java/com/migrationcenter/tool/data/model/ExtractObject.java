package com.migrationcenter.tool.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor*/
@Document(collection = "jobdata")
public class ExtractObject implements Serializable {
//    private String name;
//    private String srcPath;
//    private String targetPath;
//    private String fileType;
	
	@Id
	private String id;
	
	private String sourceFile;
	private String transformedFile;
	private String loadedFile;
	private String mappingFile;
	private String jobId;
	private String keywords;
	private String transformedFileErrorAnalysis;
	private List<String> filePaths = new ArrayList<>();
	
	public ExtractObject() {}
	
	public ExtractObject(String sourceFile, String mappingFile,String transformedFile, String loadedFile, String jobId, String keywords, List <String> filePaths, String transformedFileErrorAnalysis) {

		super();
		this.sourceFile = sourceFile;
		this.transformedFile = transformedFile;
		this.loadedFile = loadedFile;
		this.jobId = jobId;
		this.mappingFile = mappingFile;
		this.keywords = keywords;
		this.filePaths = filePaths;
		this.transformedFileErrorAnalysis = transformedFileErrorAnalysis;
	}
	
	
	

	@Override
	public String toString() {
		return "ExtractObject [sourceFile=" + sourceFile + ", transformedFile=" + transformedFile + ", loadedFile="
				+ loadedFile + ", mappingFile=" + mappingFile + ", jobId=" + jobId + ", keywords=" + keywords + "]";
	}

	public String getMappingFile() {
		return mappingFile;
	}

	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getTransformedFile() {
		return transformedFile;
	}
	public void setTransformedFile(String transformedFile) {
		this.transformedFile = transformedFile;
	}
	public String getLoadedFile() {
		return loadedFile;
	}
	public void setLoadedFile(String loadedFile) {
		this.loadedFile = loadedFile;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public List<String> getFilePaths() {
		return filePaths;
	}
	public void setFilePaths(List<String> filePaths) {
		this.filePaths = filePaths;
	}

	public String getTransformedFileErrorAnalysis() {
		return transformedFileErrorAnalysis;
	}

	public void setTransformedFileErrorAnalysis(String transformedFileErrorAnalysis) {
		this.transformedFileErrorAnalysis = transformedFileErrorAnalysis;
	}
		
	
	
//	public ExtractObject(LoadFile sourceFile, LoadFile transformedFile, LoadFile loadedFile, String jobId, String keywords) {
//		super();
//		this.sourceFile = sourceFile;
//		this.transformedFile = transformedFile;
//		this.loadedFile = loadedFile;
//		this.jobId = jobId;
//		this.keywords = keywords;
//	}
//	@Override
//	public String toString() {
//		return "ExtractObject [sourceFile=" + sourceFile + ", transformedFile=" + transformedFile + ", loadedFile="
//				+ loadedFile + ", jobId=" + jobId + ", keywords=" + keywords + "]";
//	}
//	public LoadFile getSourceFile() {
//		return sourceFile;
//	}
//	public void setSourceFile(LoadFile sourceFile) {
//		this.sourceFile = sourceFile;
//	}
//	public LoadFile getTransformedFile() {
//		return transformedFile;
//	}
//	public void setTransformedFile(LoadFile transformedFile) {
//		this.transformedFile = transformedFile;
//	}
//	public LoadFile getLoadedFile() {
//		return loadedFile;
//	}
//	public void setLoadedFile(LoadFile loadedFile) {
//		this.loadedFile = loadedFile;
//	}
//	public String getJobId() {
//		return jobId;
//	}
//	public void setJobId(String jobId) {
//		this.jobId = jobId;
//	}
//	public String getKeywords() {
//		return keywords;
//	}
//	public void setKeywords(String keywords) {
//		this.keywords = keywords;
//	}
//	
//	

	
}
