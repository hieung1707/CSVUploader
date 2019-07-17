/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import java.net.Socket;
import constant.Constant;
import util.Filter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author ASUS
 */
public class CSVClient {

    private Socket clientSocket;
    private String path;
    private boolean isStreaming = false;

    public CSVClient(String path) throws IOException {
        clientSocket = new Socket(Constant.SERVER_ADDRESS, Constant.SERVER_PORT);
        this.path = path;
    }
    
    public CSVClient() throws IOException {
        clientSocket = new Socket(Constant.SERVER_ADDRESS, Constant.SERVER_PORT);
    }

    public void sendFile() throws FileNotFoundException, IOException {
        BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        byte[] mybytearray = new byte[1024];
        int count = 0;
        while ((count = bis.read(mybytearray, 0, mybytearray.length)) != -1) {
            bos.write(mybytearray, 0, mybytearray.length);
        }
        bos.close();
        clientSocket.close();
    }
    
    public void sendFiles() throws FileNotFoundException, IOException {
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        File[] files = Filter.finder(constant.Constant.CALL_FILES_DIR, ".call");
        dos.writeInt(files.length);
        for (File file : files) {
            if (file.isFile()) {
                FileInputStream fis = new FileInputStream(file);
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                int read = 0;
                while ((read = fis.read()) != -1)
                    dos.writeByte(read);
                dos.flush();
                fis.close();
                System.out.println(file.getName() + " sent");
                file.delete();
            }
        }
        
    }
    
    
    public static void main(String[] args) {
        try {
            new CSVClient("input.csv").sendFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
