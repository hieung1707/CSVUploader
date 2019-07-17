/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import entity.Loan;
import entity.Person;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class CSVReader implements ExcelFileReader {
    private BufferedReader csvReader;
    private List<List<String>> listRows;
    private List<String> listHeaders;
    
    public CSVReader(String path) throws FileNotFoundException, IOException {
        csvReader = new BufferedReader(new FileReader(path));
        getData();
    }
    
    private void getData() throws IOException {
        listRows = new ArrayList<>();
        listHeaders = new ArrayList<>();
        int lineCount = 0;
        String row = null;
        while ((row = csvReader.readLine()) != null) {
            String[] cols = row.split(",");
            List<String> listCols = new ArrayList<>();
            for (int i = 0; i < cols.length; i++) {
                String col = cols[i];
                if (col.contains("(") && !col.contains(")")) {
                    i++;
                    while (i < cols.length) {
                        col += cols[i]; 
                        if (cols[i].contains(")"))
                            break;
                        i++;
                    }
                }
                listCols.add(col);
            }
            if (lineCount == 0) {
                for (String col : listCols)
                    listHeaders.add(col);
            }
            else {
                if (listCols.size() < listHeaders.size())
                    continue;
                List<String> listProperties = new ArrayList<>();
                for (String col : listCols)
                    listProperties.add(col);
                listRows.add(listProperties);
            }
            lineCount += 1;
        }
        csvReader.close();
    }
    
    public List<String> getHeaders() {
        return listHeaders;
    }
    
    public List<List<String>> getRows() {
        return listRows;
    }
}
