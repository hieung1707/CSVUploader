/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.List;

/**
 *
 * @author ASUS
 */
public interface ExcelFileReader {
    public List<String> getHeaders();
    public List<List<String>> getRows();
}
