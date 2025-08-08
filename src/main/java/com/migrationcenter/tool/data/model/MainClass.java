package com.migrationcenter.tool.data.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class MainClass {

static {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
        
    }

	static public String url;
	static public String username;
	static public String readPath;
	static public String apiVersion;
	static public String writePath;
	static public String password;
	static public String transformOnly="yes";
	static public int majorVersionCount = 0;
	static public int minorVersionCount = 0;
	static public String mappingFile;
	static public List<HashMap> latestMapList = Collections.synchronizedList(new ArrayList<HashMap>());
	static public List<HashMap> mapList = Collections.synchronizedList(new ArrayList<HashMap>());
	static public List<HashMap> wholeMapList = Collections.synchronizedList(new ArrayList<HashMap>());
	static public HashMap<String,String> veevaDocIdMap = new HashMap<String,String>();
	static public HashMap<String,String> idStatus = new HashMap<String,String>();
	//static public final Logger logger = Logger.getLogger(MainClass.class.getName());
	static public HashMap<String,Integer> requiredColumnIndices = new HashMap<String,Integer>();
	//static public HashMap<String,HashMap<String,String>> lookupMap = new HashMap<String,HashMap<String,String>>();
	static public String objectMapping;
	static public String lookupFilePath;
	
	
	static public HashMap<String,MappingBean> mapping= new HashMap<>();
	static public HashMap<String,HashMap<String,String>> objectReference  = new HashMap<>();
	static public HashMap<String,HashMap<String,String>> pickList  = new HashMap<>();
	static public List<String> targetColumn  = Collections.synchronizedList(new ArrayList<String>());
	
	
	static public List<HashMap<String, String>> transformedData = Collections.synchronizedList(new ArrayList<>());

	

}
