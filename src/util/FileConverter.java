/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import util.DataParser;

/**
 *
 * @author ASUS
 */
public class FileConverter {
    private static final String CALL_FILE_EXTENSION = ".call";
    
    private String path;
    private StringBuilder builder;
    
    public FileConverter() {
        this.path = constant.Constant.CALL_FILES_DIR + "\\";
    }
    
    public void convertToCallFile(List<List<String>> listRows) throws IOException {
        for (List<String> row : listRows) {
            String customerNum = row.get(1);
            String customerName = row.get(2);
            String gender = row.get(3).equals("Male") ? "anh" : "chi";
            String contract = row.get(4);
            String amount = row.get(5);
            String dueDate = row.get(6);
            String beforeDueDate = getBeforeDueDate(dueDate);
            String note = row.get(8);
            String extension = getExtension(note);
            System.out.println(extension);
            builder = new StringBuilder();
            appendChannel(customerNum);
            appendCallerId();
            appendContext();
            appendExtension(extension);
            appendPriority();
            appendVariable("customernum", customerNum);
            appendVariable("gender", gender);
            appendVariable("customername", customerName);
            appendVariable("contract", contract);
            appendVariable("money", amount);
            appendVariable("due_date", dueDate);
            appendVariable("before_due_date", beforeDueDate);
            String content = builder.toString();
            
            FileWriter writer = new FileWriter(path 
                    + customerNum 
                    + "-" 
                    + extension.toLowerCase() 
                    + CALL_FILE_EXTENSION);
            writer.write(content);
            writer.close();
        }
    }
    
    private String getBeforeDueDate(String dueDateString) {
        Date dueDate = DataParser.parseStringToDate(dueDateString);
        long beforeDueDateInMils = dueDate.getTime() - 24 * 60 * 60 * 1000;
        Date beforeDueDate = new Date(beforeDueDateInMils);
        return DataParser.parseDateToString(beforeDueDate);
    }
    
    private void appendChannel(String customerNum) {
        builder.append("Channel: SIP/trunk/" + customerNum);
    }
    
    private void appendCallerId() {
        builder.append("Callerid: " + constant.Constant.CALLERID);
    }
    
    private void appendContext() {
        builder.append("Context: " + constant.Constant.CONTEXT);
    }
    
    private void appendPriority() {
        builder.append("Priority: " + String.valueOf(constant.Constant.PRIORITY));
    }
    
    private String getExtension(String note) {
        String extension = "";
        switch (note) {
            case "Pre collection":
                extension = "preCol";
                break;
            case "Welcome Call":
                extension = "welcome";
                break;
        }
        return extension;
    }
    
    private void appendExtension(String extension) {
        builder.append("Extension: " + extension);
    }
    
    private void appendVariable(String var, String val) {
        builder.append("Set: ").append(var).append("=").append(val);
    }
}
