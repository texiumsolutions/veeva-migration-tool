package com.migrationcenter.tool.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "jobs")
public class Job {

    @Id
    private String id; // Unique job ID
    private String profileId;  // Profile ID (associated profile)
    private String profileName;  // Profile name
    private LocalDateTime dateCreated;  // Date job was created
    private String state;  // Current state of the job (e.g., transformed, ready, etc.)

    // Constructors
    public Job() {}

    public Job(String profileName, String profileId, LocalDateTime dateCreated, String state) {
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.profileId = profileId;
        this.state = state;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
