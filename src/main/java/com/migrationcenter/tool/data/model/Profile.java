//package com.migrationcenter.tool.data.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//import java.util.ArrayList;
//
///*@Data
//@AllArgsConstructor
//@Builder
//@NoArgsConstructor*/
//
///*
// * 
//_id 664ceda11888ce74be363045
//name "TestUser"
//loaderfilepath "D:\MigratorTool\OriginalFile.xlsx"
//lastjobid "123"
//mappingfilepath "D:\MigratorTool\field mapping Sheet.xlsx"
//outputpath "D:\MigratorTool\"
//transformation "yes"
//migrationtype "document"
// */
//@Document(collection = "profile")
//public class Profile implements Serializable {
//
//	@Id
//	public String id;
//
//	private String name;
//	private String loaderfilepath;
//	private String mappingfilepath;
//	private String transformation;
//	private String migrationtype;
//	private String vaultDNS;
//	private String mappingFileId;
//	private List<String> jobids = new ArrayList<>();  // Initialize as empty list
//
//
////	@Override
////	public String toString() {
////		return "Profile [id=" + id + ", name=" + name + ", loaderfilepath=" + loaderfilepath + ", mappingfilepath="
////				+ mappingfilepath + ", transformation=" + transformation + ", migrationtype=" + migrationtype
////				+ ", vaultDNS=" + vaultDNS  + ", jobids=" + jobids + "]";
////	}
//
//	// Default constructor
//	public Profile() {
//		super();
//	}
//
//	// Constructor with all fields except jobids (which is initialized as empty)
////	public Profile(String name, String loaderfilepath, String mappingfilepath, String transformation,
////				   String migrationtype, String vaultDNS, List<String> jobids, String mappingFileId) {
////
////		this.name = name;
////		this.loaderfilepath = loaderfilepath;
////		this.mappingfilepath = mappingfilepath;
////		this.transformation = transformation;
////		this.migrationtype = migrationtype;
////		this.vaultDNS = vaultDNS;
////		this.jobids = jobids;  // Job IDs will be added later
////		this.mappingFileId = mappingFileId;
////	}
//	public Profile(String name, String loaderfilepath, String mappingfilepath,
//            String transformation, String migrationtype, String vaultDNS,
//            List<String> jobids) {
//	 this.name = name;
//	 this.loaderfilepath = loaderfilepath;
//	 this.mappingfilepath = mappingfilepath;
//	 this.transformation = transformation;
//	 this.migrationtype = migrationtype;
//	 this.vaultDNS = vaultDNS;
//	 this.jobids = jobids;
//	}
//
//
//	// Getters and setters
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getLoaderfilepath() {
//		return loaderfilepath;
//	}
//	public void setLoaderfilepath(String loaderfilepath) {
//		this.loaderfilepath = loaderfilepath;
//	}
//	public String getMappingfilepath() {
//		return mappingfilepath;
//	}
//	public void setMappingfilepath(String mappingfilepath) {
//		this.mappingfilepath = mappingfilepath;
//	}
//	public String getTransformation() {
//		return transformation;
//	}
//	public void setTransformation(String transformation) {
//		this.transformation = transformation;
//	}
//	public String getMigrationtype() {
//		return migrationtype;
//	}
//	public void setMigrationtype(String migrationtype) {
//		this.migrationtype = migrationtype;
//	}
//	public String getVaultDNS() {
//		return vaultDNS;
//	}
//	public void setVaultDNS(String vaultDNS) {
//		this.vaultDNS = vaultDNS;
//	}
//	public List<String> getJobids() {
//		return jobids;
//	}
//	public void setJobids(List<String> jobids) {
//		this.jobids = jobids;
//	}
//	public void addJobId(String jobId) {
//		this.jobids.add(jobId);  // Add a new job ID to the list
//	}
//	public String getMappingFileId() {
//        return mappingFileId;
//    }
//
//    public void setMappingFileId(String mappingFileId) {
//        this.mappingFileId = mappingFileId;
//    }
//}


package com.migrationcenter.tool.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "profile")
public class Profile implements Serializable {

    @Id
    private String id;   // make private for consistency

    // 🔹 Old fields (still kept for backward compatibility if used elsewhere)
    private String loaderfilepath;
    private String mappingfilepath;
    private String transformation;
    private String migrationtype;
    private String vaultDNS;
    private String mappingFileId;

    // 🔹 New fields for profile creation
    private String name;
    private String type;        // filetype, database, mongodb
    private String location;
    private String description;
    private String sourceFile;

    private List<String> jobids = new ArrayList<>();  // Initialize as empty list

    // Default constructor
    public Profile() {}

    // 🔹 New constructor (used in your controller)
    public Profile(String name, String type, String location, String description, String sourceFile, List<String> jobids) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
        this.sourceFile = sourceFile;
        this.jobids = jobids;
    }

    // Getters & Setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }

    public List<String> getJobids() { return jobids; }
    public void setJobids(List<String> jobids) { this.jobids = jobids; }
    public void addJobId(String jobId) { this.jobids.add(jobId); }

    // (Old fields – still available if needed elsewhere in your code)
    public String getLoaderfilepath() { return loaderfilepath; }
    public void setLoaderfilepath(String loaderfilepath) { this.loaderfilepath = loaderfilepath; }

    public String getMappingfilepath() { return mappingfilepath; }
    public void setMappingfilepath(String mappingfilepath) { this.mappingfilepath = mappingfilepath; }

    public String getTransformation() { return transformation; }
    public void setTransformation(String transformation) { this.transformation = transformation; }

    public String getMigrationtype() { return migrationtype; }
    public void setMigrationtype(String migrationtype) { this.migrationtype = migrationtype; }

    public String getVaultDNS() { return vaultDNS; }
    public void setVaultDNS(String vaultDNS) { this.vaultDNS = vaultDNS; }

    public String getMappingFileId() { return mappingFileId; }
    public void setMappingFileId(String mappingFileId) { this.mappingFileId = mappingFileId; }
}
