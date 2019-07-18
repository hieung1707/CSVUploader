/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import entity.Loan;
import entity.Person;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author ASUS
 */
public class DataParser {
    public static Date parseStringToDate(String dateString) {
        Date date = null;
        try {
            System.out.println(dateString);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            date = df.parse(dateString);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    
    public static String parseDateToString(Date date) {
        String dateString = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            dateString = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return dateString;
        }
    }
    
    public static String parseDateTimeToString(Date date) {
        String dateString = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            dateString = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return dateString;
        }
    }
    
   public static List<Loan> parseDataToLoans(List<List<String>> lines) {
        List<Loan> listLoans = new ArrayList<>();
        for (List<String> line : lines) {
            Person person = new Person(line.get(0), line.get(1), line.get(2));
            Date expDate = parseStringToDate(line.get(5));
            Date oblRetDate = parseStringToDate(line.get(6));
            Loan loan = new Loan(person, 
                    line.get(3), 
                    Integer.parseInt(line.get(4)), 
                    expDate, oblRetDate);
            listLoans.add(loan);
        }
        return listLoans;
   }
}
