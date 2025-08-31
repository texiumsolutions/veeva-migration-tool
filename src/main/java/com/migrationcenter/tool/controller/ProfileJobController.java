//package com.migrationcenter.tool.controller;
//
//import org.springframework.web.bind.annotation.*;
//
//import com.migrationcenter.tool.data.model.Profile;
//import com.migrationcenter.tool.service.ProfileJobService;
//import org.springframework.http.MediaType;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//@CrossOrigin
//@RestController
//@RequestMapping("/api")
//public class ProfileJobController {
//	
//    @Autowired
//    ProfileJobService profileJobService;
//
//    
////    @PostMapping("/createprofile")
////    public Profile createProfile(@RequestParam("name") String name,@RequestParam("loaderfilepath") String loaderfilepath,@RequestParam("mappingfilepath") String mappingfilepath,
////                                 @RequestParam("transformation") String transformation,@RequestParam("migrationtype") String migrationtype,@RequestParam(value = "vaultdns", required = false, defaultValue = "") String vaultDNS) {
////    	//public Profile( String name, String loaderfilepath, String mappingfilepath, String transformation,String migrationtype, String[] jobids)
////    	return profileJobService.createNewProfle(new Profile(name, loaderfilepath, mappingfilepath, transformation, migrationtype, vaultDNS, new ArrayList<String>()));
////    	
////    }
//    
//    
//  @PostMapping("/createprofile")
//  public Profile createProfile(@RequestParam("name") String name,@RequestParam("loaderfilepath") String loaderfilepath,
//                               @RequestParam("transformation") String transformation,@RequestParam("migrationtype") String migrationtype,@RequestParam(required = false, defaultValue = "") ) {
//  	//public Profile( String name, String loaderfilepath, String mappingfilepath, String transformation,String migrationtype, String[] jobids)
//  	return profileJobService.createNewProfle(new Profile(name, loaderfilepath,transformation, migrationtype, vaultDNS, new ArrayList<String>()));
//  	
//  }
//    
//    
////    @PostMapping("/createprofile1")
////    public Profile createProfile1() {
////    	return profileJobService.createNewProfle();
////    	
////    }
//   
//    @GetMapping("/getallprofile")
//    public List<Profile> getAllProfile() {
//    	return profileJobService.getAllProfile();
//    }
//
//    @GetMapping("/getprofilename")
//    public Profile getProfileName(@RequestParam("name") String name) {
//    	List<Profile> p = profileJobService.getProfileName(name);
//    	System.out.println(p);
//    	return p.get(0);
//    	
//    }
//    
//    @PostMapping("/update")
//    public Profile updateProfile(@RequestParam("name") String name, @RequestParam("loaderfilepath") String loaderfilepath, @RequestParam("mappingfilepath") String mappingfilepath, @RequestParam("transformation") String transformation, @RequestParam("migrationtype") String migrationtype, @RequestParam("vaultdns") String vaultDNS) {
//        List<Profile> profiles = profileJobService.getProfileName(name);
//        if (profiles != null && !profiles.isEmpty()) {
//            Profile existingProfile = profiles.get(0);
//            existingProfile.setLoaderfilepath(loaderfilepath);
//            existingProfile.setMappingfilepath(mappingfilepath);
//            existingProfile.setTransformation(transformation);
//            existingProfile.setMigrationtype(migrationtype);
//            existingProfile.setVaultDNS(vaultDNS);
//            return profileJobService.updateProfile(existingProfile);
//        }
//        return null;
//    }
////    public Profile updateProfile(@RequestParam("name") String name,@RequestParam("loaderfilepath") String loaderfilepath,@RequestParam("mappingfilepath") String mappingfilepath,@RequestParam("transformation") String transformation,@RequestParam("migrationtype") String migrationtype,@RequestParam("vaultdns") String vaultDNS) {
////    	List<Profile> p = profileJobService.getProfileName(name);
////    	System.out.println(p.size());
////    	System.out.println(p.get(0));
////
////    	return profileJobService.createNewProfle(new Profile(p.get(0).id,name,loaderfilepath,mappingfilepath,transformation,migrationtype,vaultDNS));
////    }
//    
//    @PostMapping("/deleteprofile")
//    public void deleteProfile(@RequestParam("id") String id) {
//    	profileJobService.deleteProfile(id);
//    }
//
//
//}


package com.migrationcenter.tool.controller;

import org.springframework.web.bind.annotation.*;
import com.migrationcenter.tool.data.model.Profile;
import com.migrationcenter.tool.service.ProfileJobService;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProfileJobController {
	
    @Autowired
    ProfileJobService profileJobService;

    // ✅ New Profile Creation (with sourceFile)
    @PostMapping("/createprofile")
    public ResponseEntity<?> createProfile(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("sourceFile") String sourceFile) {

        Profile newProfile = new Profile(name, type, location, description, sourceFile, new ArrayList<>());  
        Profile savedProfile = profileJobService.createNewProfle(newProfile);

        return ResponseEntity.ok().body(
            new java.util.HashMap<String, Object>() {{
                put("message", "Profile created successfully");
                put("profileId", savedProfile.getId());
                put("jobIds", savedProfile.getJobids());
            }}
        );
    }

    @GetMapping("/getallprofile")
    public List<Profile> getAllProfile() {
        return profileJobService.getAllProfile();
    }

    @GetMapping("/getprofilename")
    public Profile getProfileName(@RequestParam("name") String name) {
        List<Profile> p = profileJobService.getProfileName(name);
        return p.isEmpty() ? null : p.get(0);
    }

    // ✅ Update Profile (includes sourceFile now)
    @PostMapping("/updateprofile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("location") String location,
            @RequestParam("description") String description,
            @RequestParam("sourceFile") String sourceFile) {

        Profile existingProfile = profileJobService.getProfileById(id);
        if (existingProfile != null) {
            existingProfile.setName(name);
            existingProfile.setType(type);
            existingProfile.setLocation(location);
            existingProfile.setDescription(description);
            existingProfile.setSourceFile(sourceFile);

            Profile updated = profileJobService.updateProfile(existingProfile);

            return ResponseEntity.ok().body(
                new java.util.HashMap<String, Object>() {{
                    put("message", "Profile updated successfully");
                    put("profileId", updated.getId());
                    put("jobIds", updated.getJobids());
                }}
            );
        }
        return ResponseEntity.badRequest().body("Profile not found");
    }

    @PostMapping("/deleteprofile")
    public void deleteProfile(@RequestParam("id") String id) {
        profileJobService.deleteProfile(id);
    }
}

