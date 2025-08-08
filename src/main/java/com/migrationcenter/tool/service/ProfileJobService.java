package com.migrationcenter.tool.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.migrationcenter.tool.data.dao.ProfileDAO;

import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.entity.Extraction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//@FunctionalInterface
//public interface ProfileJobService {
//	
//	 @Component
//	 @RequiredArgsConstructor
//	 @Slf4j
//	 class Default implements ProfileJobService {
//	
//	 }
//}

@Service
public class ProfileJobService {


	@Autowired
	ProfileDAO iprofilesDao;

//    public Profile createNewProfle() {
//        return iprofilesDao.save(new Profile("test2","D:/MigratorTool/OriginalFile.xlsx","D:\\MigratorTool\\field mapping Sheet.xlsx","D:\\MigratorTool\\","yes","document"));
//    }

	public Profile createNewProfle(Profile profile) {
		// TODO Auto-generated method stub

		return iprofilesDao.save(profile);
	}

	public List<Profile> getAllProfile() {
		// TODO Auto-generated method stub

		return iprofilesDao.findAll();
	}

	public List<Profile> getProfileName(String name) {
		// TODO Auto-generated method stub
		System.out.println("Retrieving profiles by name: " + name);
		return iprofilesDao.getProfileByName(name);
	}

	public Profile updateProfile(Profile profile) {
		// Update an existing profile in the database
		return iprofilesDao.save(profile);
	}

//	public static void getProfileItemByName(String name) {
//        System.out.println("Getting item by name: " + name);
//        Profile item = profileItemRepo.findItemByName(name);
//        System.out.println(getItemDetails(item));
//    }


	public void deleteProfile(String id) {
		iprofilesDao.deleteById(id);
	}

	public Profile getProfileById(String profileId) {
		// Implement the logic to retrieve the profile by ID
		return iprofilesDao.findById(profileId).orElse(null);
	}

	public Profile addJobIdToProfile(String profileId, String jobId) {
		// Retrieve the profile by ID
		Profile profile = getProfileById(profileId);
		if (profile != null) {
			// Get the existing job IDs and initialize a list to store them
			List<String> jobIdsList;

			// If jobids is a String[]:
			if (profile.getJobids() != null) {
				// Convert the array to a list
				jobIdsList = new ArrayList<>((profile.getJobids()));
				jobIdsList.add(jobId);
			} else {

				// If jobids is null or empty, initialize a new list
				jobIdsList = new ArrayList<>();
			}

			// Add the new job ID to the list


			// Convert the list back to a String array and set it in the profile
//			profile.setJobids(jobIdsList.toArray(new String[]));

			// Save the updated profile to the database
			return updateProfile(profile);
		}
		return null;
	}

}