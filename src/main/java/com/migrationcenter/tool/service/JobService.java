package com.migrationcenter.tool.service;


import com.migrationcenter.tool.data.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.migrationcenter.tool.data.dao.JobDAO;
import com.migrationcenter.tool.data.model.Job;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private ProfileJobService profileJobService;

    // Method to get Profile by Job ID
    public Profile getProfileByJobId(String jobId) {
        // First, retrieve the Job object using jobId
        Job job = jobDAO.findById(jobId).orElse(null);

        // If the job exists, use its profileId to get the Profile
        if (job != null) {
            String profileId = job.getProfileId();
            return profileJobService.getProfileById(profileId); // Use the existing Profile retrieval method
        }
        return null;  // Return null if no Job is found
    }

    // Method to get all Jobs by Profile ID
    public List<Job> getJobsByProfileId(String profileId) {
        return jobDAO.findJobsByProfileId(profileId);
    }

    // Method to update Job state
    public Job updateJobState(String jobId, String state) {
        Job job = getJobById(jobId);
        if (job != null) {
            job.setState(state);
            return jobDAO.save(job);
        }
        return null;
    }
    // Save a job to the database
    public Job saveJob(Job job) {
        return jobDAO.save(job);
    }

    // Retrieve a job by ID
    public Job getJobById(String jobId) {
        return jobDAO.findById(jobId).orElse(null);
    }
    // You can add more methods here for job state updates, etc.
}
