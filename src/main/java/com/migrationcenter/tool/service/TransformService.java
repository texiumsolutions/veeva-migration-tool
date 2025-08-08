package com.migrationcenter.tool.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;                      
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;                         
import java.util.Map.Entry;                  
import java.util.stream.Collectors;           

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.springframework.stereotype.Service;

import com.migrationcenter.tool.data.model.MainClass;
import com.migrationcenter.tool.data.model.MappingBean;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.migrationcenter.tool.service.*;     // Already inside the same package
import com.migrationcenter.tool.entity.Extraction; // Only needed if actually used


@Service
public class TransformService {
    private static List list = new ArrayList<>();
    static {
        Extraction employee = new Extraction();
        employee.setId(1L);	
        employee.setName("Test");
        employee.setAge(31L);
        employee.setLocation("Location");
    }
    public List getCustomerList() {
        return list;
    }
    
    
    // ADDING THE NEW ERROR ANALYSIS FOR FAILED TRANSFORMATION 
    private static int errorCount = 0;
    
    public static void resetErrorCount() {
    	errorCount =0;
    }
    
    public static int getErrorCount() {
    	return errorCount;
    }
    
    
    // NEW ONE WITH UPDATES
    public static void readExcelFileAndTransform(File file) {
        try (FileInputStream fileStream = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fileStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            DataFormatter df = new DataFormatter();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // First row: header mapping
                if (row.getRowNum() == 0) {
                    for (Cell cell : row) {
                        String cellValue = df.formatCellValue(cell).trim();
                        MainClass.requiredColumnIndices.put(cellValue, cell.getColumnIndex());
                    }
                    continue;
                }

                HashMap<String, String> dataMap = new HashMap<>();
                boolean isLatest = false;

                for (Map.Entry<String, Integer> entry : MainClass.requiredColumnIndices.entrySet()) {
                    String sourceField = entry.getKey();
                    Integer columnIndex = entry.getValue();
                    Cell cell = row.getCell(columnIndex);
                    String cellValue = df.formatCellValue(cell != null ? cell : row.createCell(columnIndex)).trim();

                    if (MainClass.mapping.containsKey(sourceField)) {
                        MappingBean mapBean = MainClass.mapping.get(sourceField);
                        String target = mapBean.getTarget();
                        String type = mapBean.getType();

                        switch (type.toLowerCase()) {
                            case "direct":
                                dataMap.put(target, cellValue);
                                break;

                            case "default":
                                dataMap.put(target, mapBean.getValue());
                                break;

                            case "reference":
                                String refKey = mapBean.getValue(); // e.g., field1+field2#refMap
                                if (refKey.contains("#")) {
                                    String[] parts = refKey.split("#");
                                    String[] keys = parts[0].split("\\+");
                                    String refMapName = parts[1];

                                    String lookupKey = Arrays.stream(keys)
                                            .map(k -> {
                                                Integer idx = MainClass.requiredColumnIndices.getOrDefault(k.trim(), -1);
                                                if (idx == -1) return "";
                                                Cell refCell = row.getCell(idx);
                                                return df.formatCellValue(refCell != null ? refCell : row.createCell(idx)).trim();
                                            })
                                            .collect(Collectors.joining("+"));

                                    if (MainClass.objectReference.containsKey(refMapName)) {
                                        String mappedVal = MainClass.objectReference.get(refMapName).get(lookupKey);
                                        dataMap.put(target, mappedVal != null ? mappedVal : lookupKey + " -### Value not found");
                                        errorCount++;
                                    } else {
                                        dataMap.put(target, lookupKey + " -### Ref map not found");
                                        
                                        errorCount++;
                                    }
                                } else {
                                    if (MainClass.objectReference.containsKey(refKey)) {
                                        String mappedVal = MainClass.objectReference.get(refKey).get(cellValue);
                                        dataMap.put(target, mappedVal != null ? mappedVal : cellValue + " -### Value not found");
                                        errorCount++;
                                    } else {
                                        dataMap.put(target, cellValue + " -### Ref map not found");
                                        errorCount++;
                                    }
                                }
                                break;

                            case "picklist":
                                Map<String, String> pickMap = MainClass.pickList.get(mapBean.getValue());
                                if (pickMap != null && pickMap.containsKey(cellValue)) {
                                    dataMap.put(target, pickMap.get(cellValue));
                                } else {
                                    dataMap.put(target, cellValue + " ### Value Not found");
                                    errorCount++;
                                }
                                break;

                            default:
                                dataMap.put(target, cellValue + " ### Unknown Mapping Type");
                                errorCount++;
                                break;
                        }

                    } else {
                        // Field not in mapping
                        dataMap.put(sourceField, cellValue);
                    }

                    // Handle isLatest if present
                    if (sourceField.equalsIgnoreCase("isLatest") && "true".equalsIgnoreCase(cellValue)) {
                        isLatest = true;
                    }
                }

                // Add to appropriate lists
                if (isLatest) {
                    MainClass.latestMapList.add(dataMap);
                } else {
                    MainClass.mapList.add(dataMap);
                }

                MainClass.wholeMapList.add(dataMap);
                MainClass.idStatus.put(dataMap.get("external_id__v"), "");

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("⚠️ Transformation failed: " + e.getMessage());
        }
    }


    
    
//    public static void readExcelFileAndTransform(File file) {
//
//		try {
//			FileInputStream fileStream = new FileInputStream(file);
//			Workbook workbook = new XSSFWorkbook(fileStream);
//			Sheet sheet = workbook.getSheetAt(0);
//			Iterator<Row> rowIterator = sheet.iterator();
//			int lastColumn = 0;
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//				// For each row, iterate through all the columns
//
//				if (row.getRowNum() == 0) {
//					Iterator<Cell> cellIterator = row.cellIterator();
//
//					while (cellIterator.hasNext()) {
//						Cell cell = cellIterator.next();
//						String cellValue = cell.getStringCellValue();
//						MainClass.requiredColumnIndices.put(cellValue, cell.getColumnIndex());
//						lastColumn++;
//					}
//
//				} else {
//					HashMap<String, String> dataMap = new HashMap<String, String>();
//					boolean flag = false;
//					for (Entry<String, Integer> entry : MainClass.requiredColumnIndices.entrySet()) {
//						if (MainClass.mapping.containsKey(entry.getKey().toString())) {
//							if (MainClass.mapping.get(entry.getKey()).getType().equalsIgnoreCase("Direct")) {
//								Cell cell = row.getCell(MainClass.requiredColumnIndices.get(entry.getKey()));
//								DataFormatter df = new DataFormatter();
//								// System.out.println(entry.getKey() + " : " + df.formatCellValue(cell));
//								dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//										df.formatCellValue(cell));
//							}
//							if (MainClass.mapping.get(entry.getKey()).getType().equalsIgnoreCase("Default")) {
//								Cell cell = row.getCell(MainClass.requiredColumnIndices.get(entry.getKey()));
//								DataFormatter df = new DataFormatter();
//								dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//										MainClass.mapping.get(entry.getKey()).getValue());
//							}
//							if (MainClass.mapping.get(entry.getKey()).getType().equalsIgnoreCase("Reference")) {
//								Cell cell = row.getCell(MainClass.requiredColumnIndices.get(entry.getKey()));
//								DataFormatter df = new DataFormatter();
//								if (MainClass.mapping.get(entry.getKey()).getValue().contains("+")) {
//									String lookupObject = "";
//									for(String innerObject: MainClass.mapping.get(entry.getKey()).getValue().split("#")[0].split("\\+")) {
//										lookupObject = lookupObject + "+" + row.getCell(MainClass.requiredColumnIndices.get(innerObject));
//									}
//									lookupObject = lookupObject.replaceFirst("\\+", "");
//									if (MainClass.objectReference
//											.containsKey(MainClass.mapping.get(entry.getKey()).getValue().split("#")[1])) {
//										if (MainClass.objectReference
//												.get(MainClass.mapping.get(entry.getKey()).getValue().split("#")[1])
//												.containsKey(lookupObject)) {
//											dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//													MainClass.objectReference
//															.get(MainClass.mapping.get(entry.getKey()).getValue().split("#")[1])
//															.get(lookupObject));
//										}
//										else {
//											dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//													df.formatCellValue(cell) + "-### Value not found");
//										}
//									} else {
//										dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//												df.formatCellValue(cell) + "-### Value not found");
//									}
//								} else {
//									if (MainClass.objectReference
//											.containsKey(MainClass.mapping.get(entry.getKey()).getValue())) {
//										if (MainClass.objectReference
//												.get(MainClass.mapping.get(entry.getKey()).getValue())
//												.containsKey(df.formatCellValue(cell))) {
//											dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//													MainClass.objectReference
//															.get(MainClass.mapping.get(entry.getKey()).getValue())
//															.get(df.formatCellValue(cell)));
//										}
//									} else {
//										dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//												df.formatCellValue(cell) + "-### Value not found");
//									}
//								}
//							}
//							if (MainClass.mapping.get(entry.getKey()).getType().equalsIgnoreCase("Picklist")) {
//								Cell cell = row.getCell(MainClass.requiredColumnIndices.get(entry.getKey()));
//								DataFormatter df = new DataFormatter();
//								if(MainClass.pickList.get(MainClass.mapping.get(entry.getKey()).getValue())
//												.containsKey(df.formatCellValue(cell))) {
//								dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//										MainClass.pickList.get(MainClass.mapping.get(entry.getKey()).getValue())
//												.get(df.formatCellValue(cell)));
//								}
//								else {
//									dataMap.put(MainClass.mapping.get(entry.getKey()).getTarget(),
//											df.formatCellValue(cell) + "### Value Not found");
//								}
//							}
//						} else {
//							Cell cell = row.getCell(MainClass.requiredColumnIndices.get(entry.getKey()));
//							DataFormatter df = new DataFormatter();
//							// System.out.println(entry.getKey() + " : " + df.formatCellValue(cell));
//							dataMap.put(entry.getKey(), df.formatCellValue(cell));
//							if (entry.getKey().equalsIgnoreCase("isLatest")) {
//								if (row.getCell(MainClass.requiredColumnIndices.get(entry.getKey())).toString()
//										.equalsIgnoreCase("true")) {
//
//									MainClass.latestMapList.add(dataMap);
//									flag = true;
//								}
//							}
//						}
//					}
//					if (!flag) {
//
//						MainClass.mapList.add(dataMap);
//					}
//
//					MainClass.wholeMapList.add(dataMap);
//					MainClass.idStatus.put(dataMap.get("external_id__v"), "");
//				}
//			}
//			workbook.close();
//			fileStream.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		// return mapList;
//	}


    public static void readMappingFile(File file) {

		try {
			FileInputStream fileStream = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheet("mapping");
			Iterator<Row> rowIterator = sheet.iterator();
			int lastColumn = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (row.getRowNum() != 0) {
					HashMap<String, String> dataMap = new HashMap<String, String>();
					boolean flag = false;
					MappingBean obj = new MappingBean();
					if (row.getCell(1).getStringCellValue().equalsIgnoreCase("Default")) {
						obj.setSource(row.getCell(0).getStringCellValue());
						obj.setType("Default");
						obj.setValue(row.getCell(3).getStringCellValue());
						obj.setTarget(row.getCell(2).getStringCellValue());
						MainClass.targetColumn.add(obj.getTarget());
						MainClass.mapping.put(row.getCell(0).getStringCellValue(), obj);

					} else if (row.getCell(1).getStringCellValue().equalsIgnoreCase("Direct")) {
						obj.setSource(row.getCell(0).getStringCellValue());
						obj.setType("Direct");
						obj.setTarget(row.getCell(2).getStringCellValue());
						MainClass.targetColumn.add(obj.getTarget());
						MainClass.mapping.put(row.getCell(0).getStringCellValue(), obj);
					} else if (row.getCell(1).getStringCellValue().equalsIgnoreCase("Reference")) {

						obj.setSource(row.getCell(0).getStringCellValue());
						obj.setType("Reference");
						obj.setValue(row.getCell(3).getStringCellValue());
						obj.setTarget(row.getCell(2).getStringCellValue());
						MainClass.targetColumn.add(obj.getTarget());
						MainClass.mapping.put(row.getCell(0).getStringCellValue(), obj);
					} else if (row.getCell(1).getStringCellValue().equalsIgnoreCase("Picklist")) {
						obj.setSource(row.getCell(0).getStringCellValue());
						obj.setType("Picklist");
						obj.setValue(row.getCell(3).getStringCellValue());
						obj.setTarget(row.getCell(2).getStringCellValue());
						MainClass.targetColumn.add(obj.getTarget());
						MainClass.mapping.put(row.getCell(0).getStringCellValue(), obj);
					}


				}
			}
			workbook.close();
			fileStream.close();
//			for (Entry<String, MappingBean> entry : MainClass.mapping.entrySet()) {
//				System.out.println(entry.getValue().toString());
//			}
//			 System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
    
    
    public static void readObjectReferenceSheet(File file) {

		try {
			FileInputStream fileStream = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fileStream);
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (workbook.getSheetName(i).equalsIgnoreCase("mapping")
						|| workbook.getSheetName(i).equalsIgnoreCase("picklist")) {
					continue;
				}
				HashMap<String, String> map = new HashMap<>();

				Sheet sheet = workbook.getSheetAt(i);
				Iterator<Row> rowIterator = sheet.iterator();
				int lastColumn = 0;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					if (row.getRowNum() != 0) {

						map.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
					}
				}
				MainClass.objectReference.put(workbook.getSheetName(i), map);
			}
			workbook.close();
			fileStream.close();
//			for (Entry<String, HashMap<String, String>> entry : MainClass.objectReference.entrySet()) {
//				System.out.println(entry.getKey());
//				HashMap<String, String> map = entry.getValue();
//				for (Entry<String, String> ent : map.entrySet()) {
//					System.out.println(ent.getKey() + " " + ent.getValue());
//				}
//
//			}
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void readPicklistSheet(File file) {

		try {
			FileInputStream fileStream = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fileStream);
			Sheet sheet = workbook.getSheet("picklist");
			Iterator<Row> rowIterator = sheet.iterator();
			int lastColumn = 0;
			HashMap<String, String> map = new HashMap<>();
			String pickListName = "";
			int lastRow = sheet.getLastRowNum();
			for (int i = 0; i <= lastRow; i++) {

				Row row = sheet.getRow(i);
				if (row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK) {
					MainClass.pickList.put(pickListName, map);
					pickListName = row.getCell(0).getStringCellValue();
					map = new HashMap<>();
				}
				map.put(row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue());

			}
			MainClass.pickList.put(pickListName, map);
			workbook.close();
			fileStream.close();
//			for (Entry<String, HashMap<String, String>> entry : MainClass.pickList.entrySet()) {
//				System.out.println(entry.getKey());
//				HashMap<String, String> map1 = entry.getValue();
//				for (Entry<String, String> ent : map1.entrySet()) {
//					System.out.println(ent.getKey() + " " + ent.getValue());
//				}
//
//			}
			// System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    // ORIGINAL WRITEALLDATATOEXCEL FUNCTION
    
    public static File writeAllDataToExcel(String fileWritePath) {

		try {
			FileOutputStream excel = new FileOutputStream(fileWritePath);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("output");
			int rowNum = 0;

			// Create Header Row
			Row header = sheet.createRow(rowNum);

			int colNum=0;
			for(String column : MainClass.targetColumn) {
				Cell cell = header.createCell(colNum);
				cell.setCellValue(column);
				colNum++;
			}
			Cell cell = header.createCell(colNum);
			cell.setCellValue("Veeva Doc ID");
			colNum++;
			cell = header.createCell(colNum);
			cell.setCellValue("Load Status");
			rowNum++;

			for (HashMap p : MainClass.wholeMapList) {
				colNum = 0;
				Row row = sheet.createRow(rowNum);
				for(String column : MainClass.targetColumn) {
					//System.out.print(column);
					//System.out.println(p.get(column).toString());
					cell = row.createCell(colNum);
					cell.setCellValue(p.get(column).toString());
					colNum++;
				}
				//colNum++;
				if(!MainClass.transformOnly.equalsIgnoreCase("yes")) {
					cell = row.createCell(colNum);
					String veevaId = MainClass.idStatus.get(p.get("external_id__v")).toString();
					if(!veevaId.contains("SUCCESS")) {
						cell.setCellValue(MainClass.idStatus.get(p.get("external_id__v")).split("#")[0]);
					}else {
						cell.setCellValue("FAILED");
					}				
					colNum++;
					cell = row.createCell(colNum);
					if(veevaId.contains("FAILED")) {
						cell.setCellValue(MainClass.idStatus.get(p.get("external_id__v")).split("#")[1]);
					}else {
						cell.setCellValue("SUCCESS");
					}
					colNum++;	
				}
				
				rowNum++;
			}
			
			// Add error count summary at the bottom
			// Create the summary row 2 rows below the last data row
			Row summaryRow = sheet.createRow(rowNum + 2);

			// Create the first cell in the row for the label
			Cell summaryLabelCell = summaryRow.createCell(0);
			summaryLabelCell.setCellValue("❌ Mapping Error Count:");

			// Create the second cell for the actual count
			Cell summaryValueCell = summaryRow.createCell(1);
			summaryValueCell.setCellValue(errorCount);


			
			workbook.write(excel);
			workbook.close();
			excel.close();
			return new File(fileWritePath);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    
  
    
    
    
    public static int countErrorCellsInExcel(File excelFile) {
        int errorCount = 0;
        try (FileInputStream fis = new FileInputStream(excelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter df = new DataFormatter();

            for (Row row : sheet) {
                for (Cell cell : row) {
                    String value = df.formatCellValue(cell).toLowerCase();
                    if (value.contains("### value not found") ||
                        value.contains("### ref map not found") ||
                        value.contains("### unknown mapping type") ||
                        value.contains("-### ref map not found") ||
                        value.contains("-### value not found")) {
                        errorCount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorCount;
    }


//    public static void appendSummaryToExcel(File file, int errorCount) {
//        try (FileInputStream fis = new FileInputStream(file);
//             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
//
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            int lastRowNum = sheet.getLastRowNum();
//            Row summaryRow = sheet.createRow(lastRowNum + 2); // leave one row gap
//            Cell cell = summaryRow.createCell(0);
//            cell.setCellValue("❌ Mapping Error Count:\t" + errorCount);
//
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                workbook.write(fos);
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error appending summary: " + e.getMessage());
//        }
//    }

    // WORkING printing the erorr analysis at the last  row
    public static void appendSummaryToExcel(File file, int errorCount) {
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            
            for (int i = sheet.getLastRowNum(); i >= 0; i--) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(0);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String value = cell.getStringCellValue();
                        if (value != null && value.startsWith("❌ Mapping Error Count:")) {
                            sheet.removeRow(row);
                        }
                    }
                }
            }

            //  Append new summary
            int newLastRow = sheet.getLastRowNum() + 2;
            Row summaryRow = sheet.createRow(newLastRow);
            Cell cell = summaryRow.createCell(0);
            cell.setCellValue("❌ Mapping Error Count:\t" + errorCount);

   
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            System.out.println("Error appending summary: " + e.getMessage());
        }
    }

    
    
    public static void writeAllDataToExcelWithErrorSummary(String fileWritePath) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("output");
            int rowNum = 0;

            // Create Header Row
            Row header = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String column : MainClass.targetColumn) {
                header.createCell(colNum++).setCellValue(column);
            }
            // Add Errors Per Row header
            header.createCell(colNum).setCellValue("Errors Per Row");

            // Write Data Rows
            for (HashMap<String, String> dataRow : MainClass.transformedData) {
                Row row = sheet.createRow(rowNum++);
                colNum = 0;
                List<String> errorsInRow = new ArrayList<>();

                for (String column : MainClass.targetColumn) {
                    String cellValue = dataRow.getOrDefault(column, "");
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(cellValue);

                    // Check for errors like ###valueNotFound or ###refNotFound
                    if (cellValue.startsWith("###")) {
                        errorsInRow.add(cellValue);
                    }
                }

                // Write Errors Per Row summary
                String errorSummary = errorsInRow.isEmpty() ? "" :
                        errorsInRow.size() + " errors - " + String.join(", ", errorsInRow);
                row.createCell(colNum).setCellValue(errorSummary);
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(fileWritePath)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            System.out.println("Error writing Excel with Errors Per Row: " + e.getMessage());
        }
    }

    
    public static void appendErrorsPerRowColumn(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            // Get Header Row and determine last column
            XSSFRow headerRow = sheet.getRow(0);
            int lastColNum = headerRow.getLastCellNum();

            // Add "Errors Per Row" header if not present
            Cell errorHeaderCell = headerRow.createCell(lastColNum);
            errorHeaderCell.setCellValue("Errors Per Row");

            // Iterate through Data Rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;

                List<String> errorsInRow = new ArrayList<>();

                // Scan cells except the new Error column
                for (int j = 0; j < lastColNum; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String value = cell.getStringCellValue();
                        if (value != null && value.contains("###")) {
                            errorsInRow.add(value.trim());
                        }
                    }
                }

                // Write summary in Errors Per Row column
                Cell errorCell = row.createCell(lastColNum);
                if (!errorsInRow.isEmpty()) {
                    String errorSummary = errorsInRow.size() + " errors - " + String.join(", ", errorsInRow);
                    errorCell.setCellValue(errorSummary);
                } else {
                    errorCell.setCellValue("");  // No errors
                }
            }

            // Save changes
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            System.out.println("Error appending Errors Per Row: " + e.getMessage());
        }
    }

    

    

}