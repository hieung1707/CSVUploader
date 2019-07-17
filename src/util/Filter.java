/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author ASUS
 */
public class Filter {

    public static File[] finder( String dirName, String extension){
        File dir = new File(dirName);
        for (File file : dir.listFiles()) {
            System.out.println(file.getName());
        }

        return dir.listFiles(new FilenameFilter() { 
                 @Override
                 public boolean accept(File dir, String filename)
                      { return filename.endsWith(extension); }
        } );

    }

}
