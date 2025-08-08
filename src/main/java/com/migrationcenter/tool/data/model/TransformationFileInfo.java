package com.migrationcenter.tool.data.model;


public class TransformationFileInfo {
    private String sourceFile;
    private String mappingFile;
    private String outputFile;

    public TransformationFileInfo(String sourceFile, String mappingFile, String outputFile) {
        this.sourceFile = sourceFile;
        this.mappingFile = mappingFile;
        this.outputFile = outputFile;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}

