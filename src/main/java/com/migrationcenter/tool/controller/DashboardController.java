//package com.migrationcenter.tool.controller;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.migrationcenter.tool.service.ExtractionService;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//@RestController
//public class DashboardController {
//	
//    @Autowired
//    ExtractionService eservice;
//
//    @GetMapping("/customers")
//    public List getList() {
//        return eservice.getCustomerList();
//    }
//    
//    @GetMapping("/object")
//    public String getObjects() {
//    	
//        return "Hello, World!";
//    }
//    
//    @GetMapping("/document")
//    public String getDocuments() {
//        return "Hello, World!";
//    }
//    
//    @GetMapping("/binder")
//    public String getBinders() {
//        return "Hello, World!";
//    }
//}
