package com.migrationcenter.tool.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import com.migrationcenter.tool.data.model.MainClass;
import com.migrationcenter.tool.data.model.Profile;

@Service
public class InjectionService {
	// Dummy list for demo purposes
	private static List list = new ArrayList<>();
	static {
		list.add("Sample Customer");
	}

	public List getCustomerList() {
		return list;
	}

	// Load data using the profile
	public boolean load(Profile p) {
		String vaultDNS = p.getVaultDNS();
		String password = "";
		String apiVersion = "";
		String username = "";
//		String sessionId = authorization(vaultDNS, username, apiVersion, password);

		// Use sessionId for further operations if required
		return true;
	}

	// Authorization for vault
	public String authorization(String vaultDNS, String username, String apiVersion, String password) {
		String sessionId = "";
		HttpClient httpClient = HttpClientBuilder.create().build();
		String BASEURL = vaultDNS + "/api/v" + apiVersion + "/";
		String loginURL = BASEURL + "auth";

		HttpPost httpPost = new HttpPost(loginURL);
		try {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setHeader("Accept", "application/json");

			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", password));
			httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("Error Authenticating: " + statusCode);
				return null;
			}

			String getResult = EntityUtils.toString(response.getEntity());
			JSONObject jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			return jsonObject.getString("sessionId");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Get CSV formatted data
	public static String getCSVFormatData(HashMap<String, Object> map) {
		StringBuilder data = new StringBuilder();
		String row1 = "", row2 = "";
		for (Object column : MainClass.requiredColumnIndices.keySet()) {
			row1  = row1 + "," + column.toString();
			if (map.containsKey(column.toString())) {
				row2 = row2 + "," + map.getOrDefault(column.toString(), "");
			}
		}
		data.append(row1.replaceFirst(",", "")).append("\r\n").append(row2.replaceFirst(",", "")).append("\r\n");
		return data.toString().replaceAll("TRUE", "true").replaceAll("FALSE", "false");
	}

	// Loading document to the vault
	public void loadDocument(String sessionId, List<HashMap> docList, String versionUrl) throws IOException, JSONException {
		for (HashMap map : docList) {
			String data = getCSVFormatData(map);
			URL url = new URL(MainClass.url + "/api/v" + MainClass.apiVersion + versionUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/csv");
			conn.setRequestProperty("Accept", "text/csv");
			conn.setRequestProperty("Authorization", sessionId);
			conn.setRequestProperty("X-VaultAPI-MigrationMode", "true");

			try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
				wr.write(data);
			}

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					String output;
					while ((output = br.readLine()) != null) {
						System.out.println("Response: " + output);
					}
				}
			} else {
				System.out.println("Failed to upload document, Response Code: " + responseCode);
			}
			conn.disconnect();
		}
	}

	// Writing all data to Excel
	public void writeAllDataToExcel(String fileWritePath, Profile profile, String jobId) {
		try (FileOutputStream excel = new FileOutputStream(fileWritePath  + profile.getName() + "_" + jobId + "_L.xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("output");
			int rowNum = 0;

			// Create header row
			Row header = sheet.createRow(rowNum++);
			int colNum = 0;
			for (String column : MainClass.targetColumn) {
				Cell cell = header.createCell(colNum++);
				cell.setCellValue(column);
			}

			// Add data rows
			for (HashMap p : MainClass.wholeMapList) {
				Row row = sheet.createRow(rowNum++);
				colNum = 0;
				for (String column : MainClass.targetColumn) {
					Cell cell = row.createCell(colNum++);
					cell.setCellValue(p.getOrDefault(column, "").toString());
				}
			}

			workbook.write(excel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
