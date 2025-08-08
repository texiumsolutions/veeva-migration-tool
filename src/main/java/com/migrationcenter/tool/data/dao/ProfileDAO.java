package com.migrationcenter.tool.data.dao;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.migrationcenter.tool.data.model.Profile;

/**
 * This interface is used to perform DB operations on 'profile' collection
 */


@SuppressWarnings("rawtypes")
public interface ProfileDAO extends MongoRepository<Profile, String> {

	@Query("{name : '?0'}")
	List<Profile> getProfileByName(String name);
	
	//@Query("{name : '?0'}")
	List<Profile> findAll();
	
	@Query("{Profile : '?0'}")
	Profile insert(Profile profile);
	
	@Query("{Profile : '?0'}")
	Profile update(Profile profile);
	
	
	
	
//	@Query(value = "{'name' : ?0}")
//	Profile getProfileItemByName(String name);
//	
	
	//List<Tutorial> findByTitleContaining(String title);

}