package com.migrationcenter.tool.data.model;

public class TransformationFileInfoWithError {
    private String sourceFile;
    private String mappingFile;
    private String outputFile;
    private int errorCount;

    public TransformationFileInfoWithError(String sourceFile, String mappingFile, String outputFile, int errorCount) {
        this.sourceFile = sourceFile;
        this.mappingFile = mappingFile;
        this.outputFile = outputFile;
        this.errorCount = errorCount;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
