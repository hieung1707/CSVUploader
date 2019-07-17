/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.DataParser;

/**
 *
 * @author ASUS
 */
public class CSVUploader {

    private static final String FILE_NAME = "test_data.xlsx";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        XLSReader reader = new XLSReader(FILE_NAME);
        List<String> listHeaders = reader.getHeaders();
        List<List<String>> listRows = reader.getRows();
        CSVWriter writer = new CSVWriter("test_data.csv");
        writer.setHeader(listHeaders);
        for (List<String> row : listRows) {
            writer.addLine(row);
        }
        writer.flushToFile();
//        FileConverter converter = new FileConverter();
//        try {
//            converter.convertToCallFile(listRows);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//
//            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
//            Workbook workbook = new XSSFWorkbook(excelFile);
//            Sheet datatypeSheet = workbook.getSheetAt(0);
//            Iterator<Row> iterator = datatypeSheet.iterator();
//            int count = 0;
//            while (iterator.hasNext()) {
//                Row currentRow = iterator.next();
//                if (count > 0) {
//                    Iterator<Cell> cellIterator = currentRow.iterator();
//
//                    while (cellIterator.hasNext()) {
//
//                        Cell currentCell = cellIterator.next();
////                        getCellTypeEnum shown as deprecated for version 3.15
////                        getCellTypeEnum ill be renamed to getCellType starting from version 4.0
//                        if (currentCell.getCellType() == CellType.STRING) {
//                            System.out.print(currentCell.getStringCellValue() + "--");
//                        } else if (currentCell.getCellType() == CellType.NUMERIC) {
//                            if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
//                                System.out.print(DataParser.parseDateToString(currentCell.getDateCellValue())+ "--");
//                            } else
//                                System.out.print(currentCell.getNumericCellValue() + "--");
//                        } else if (currentCell.getCellType() == CellType.BLANK) {
//                            System.out.print(" --");
//                        }
//                    }
//                    System.out.println();
//                }
//                count++;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
