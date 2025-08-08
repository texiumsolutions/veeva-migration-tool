package com.migrationcenter.tool.controller;

import java.util.List;

import com.migrationcenter.tool.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.service.InjectionService;
import com.migrationcenter.tool.service.ProfileJobService;

@CrossOrigin
@RestController
public class InjectionController {

	@Autowired
	InjectionService injectService;

	@Autowired
	JobService jobService;


	@PostMapping("/inject")
	public boolean loadData(@RequestParam("id") String jobId) {


		// Fetch profile by
		Profile profile = jobService.getProfileByJobId(jobId);// ID
		if (profile == null) {
			System.out.println("Profile not found");
			return false;
		}

		// Log and check profile details
		System.out.println(profile);

		// Inject service to load data using profile
		boolean status = injectService.load(profile);

		// Write data to excel using the profile and jobId
		injectService.writeAllDataToExcel("./data/", profile,jobId);

		return status;
	}
}
