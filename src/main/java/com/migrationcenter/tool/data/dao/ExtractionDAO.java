package com.migrationcenter.tool.data.dao;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.migrationcenter.tool.data.model.ExtractObject;
import com.migrationcenter.tool.data.model.Profile;

/**
 * This interface is used to perform DB operations on 'ExtractObject' collection
 */
@SuppressWarnings("rawtypes")
public interface ExtractionDAO extends MongoRepository<ExtractObject, String> {

	@Query(value = "{'ExtractObject' : ?0}")
	ExtractObject insert(ExtractObject obj);

	
	@Query(value = "{'ExtractObject' : ?0}")
	ExtractObject saveExtractDocument(String jobId);
	
	
	@Query("{jobId : '?0'}")
	ExtractObject findExtractObjectByJobid(String jobid);
	
	//List<Tutorial> findByTitleContaining(String title);

//	@Query(value = "{ 'jobId': ?0 }", update = "{ '$set': { 'sourceFile': ?1, 'transformedFile': ?2, 'transformedFileErrorAnalysis': ?3 } }")
//	void updateFilePaths(String jobId, String sourceFilePath, String transformedFilePath, String errorAnalysisFilePath);


}

