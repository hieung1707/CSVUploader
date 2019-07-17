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
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.DataParser;

/**
 *
 * @author ASUS
 */
public class XLSReader implements ExcelFileReader {

    private String filePath;
    private List<String> listHeaders;
    private List<List<String>> listRows;

    public XLSReader(String path) {
        this.filePath = path;
        listHeaders = new ArrayList<>();
        listRows = new ArrayList<>();
        readFile(filePath);
    }

    private void readFile(String filePath) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                List<String> listData = getCellData(cellIterator);
                if (count == 0)
                    listHeaders = listData;
                else
                    listRows.add(listData);
                count++;
            }
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCellData(Iterator<Cell> iterator) {
        List<String> listCellData = new ArrayList<>();
        while (iterator.hasNext()) {
            Cell currentCell = iterator.next();
            if (currentCell.getCellType() == CellType.STRING) {
                listCellData.add(currentCell.getStringCellValue().trim());
            } else if (currentCell.getCellType() == CellType.NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                    listCellData.add(DataParser.parseDateToString(currentCell.getDateCellValue()).trim());
                } else {
                    listCellData.add(String.valueOf((int)currentCell.getNumericCellValue()).trim());
                }
            } else if (currentCell.getCellType() == CellType.BLANK) {
                listCellData.add("");
            }
        }
        return listCellData;
    }
    
    public List<String> getHeaders() {
        return listHeaders;
    }
    
    public List<List<String>> getRows() {
        return listRows;
    }
}
