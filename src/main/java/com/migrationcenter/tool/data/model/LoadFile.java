package com.migrationcenter.tool.data.model;

//import java.beans.JavaBean;
import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;


public class LoadFile {

    private String filename;
    private String fileType;
    private String fileSize;
    private byte[] file;
    private String extenstion;

    public LoadFile() {
    }
    
    public LoadFile(String filename, String fileType, String fileSize, byte[] file, String extenstion) {
		super();
		this.filename = filename;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.file = file;
		this.extenstion = extenstion;
	}

	@Override
	public String toString() {
		return "LoadFile [filename=" + filename + ", fileType=" + fileType + ", fileSize=" + fileSize + ", file="
				+ Arrays.toString(file) + ", extenstion=" + extenstion + "]";
	}

	public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

	public String getExtenstion() {
		return extenstion;
	}

	public void setExtenstion(String extenstion) {
		this.extenstion = extenstion;
	}
    
    
}