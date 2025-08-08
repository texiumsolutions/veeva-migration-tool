package com.migrationcenter.tool.data.dao;

import com.migrationcenter.tool.data.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDAO extends MongoRepository<Job, String> {

    // Custom query to find a Job by its jobId
    @Query("{ 'jobID' : ?0 }")
    Job findJobByJobID(String jobID);

    // You can also have other queries like finding jobs by profileId
    @Query("{ 'profileId' : ?0 }")
    List<Job> findJobsByProfileId(String profileId);
}
