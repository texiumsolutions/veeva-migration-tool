package com.migrationcenter.tool.controller;

import com.migrationcenter.tool.data.model.Job;
import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/api/job")
    public ResponseEntity<Job> getJobById(@RequestParam("jobId") String jobId) {
        if (jobId == null || jobId.isEmpty()) {
            return ResponseEntity.badRequest().body(null);  // Invalid request
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            return ResponseEntity.notFound().build();  // Job not found
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
    public ResponseEntity<Job> updateJobState(@RequestParam("jobId") String jobId, @RequestParam("state") String state) {
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


}

