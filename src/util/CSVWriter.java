/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class CSVWriter {
    private FileWriter csvWriter;
    private List<List<String>> lines;
    private boolean hasHeader = false;
    
    public CSVWriter(String filePath) throws IOException {
        csvWriter = new FileWriter(filePath);
        lines = new ArrayList<>();
    }
    
    public void addLine(List<String> row) throws IOException {
        lines.add(row);
    }
    
    public void setHeader(List<String> headers) throws IOException {
        if (hasHeader)
            lines.remove(0);
        else
            hasHeader = true;
        lines.add(0, headers);
    }
    
    public void setHeader(String[] headers) throws IOException {
        if (hasHeader)
            lines.remove(0);
        else
            hasHeader = true;
        List<String> listHeaders = new ArrayList<>();
        for (String col : headers)
            listHeaders.add(col);
        lines.add(0, listHeaders);
    }
    
    public void flushToFile() throws IOException {
        for (List<String> line : lines) {
            csvWriter.write(String.join(",", line));
            csvWriter.write("\n");
        }
        csvWriter.flush();
    }
    
    public void close() throws IOException {
        csvWriter.close();
    }
}
