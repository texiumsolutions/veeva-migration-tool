package com.migrationcenter.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.migrationcenter.tool.data.model.Profile;

@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.migrationcenter.tool"})
public class MigrationToolApplication {
	
	@Autowired
    MongoRepository<Profile, ?> profileItemRepo;

    public static void main(String[] args) {
        SpringApplication.run(MigrationToolApplication.class, args);
    }
}
