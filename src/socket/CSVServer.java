/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socket;

import java.net.ServerSocket;
import constant.Constant;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 *
 * @author ASUS
 */
public class CSVServer {
    private ServerSocket server;
    private String path = constant.Constant.CALL_FILE_RECEIVED_DIR;
    private int count = 1;
    
    public CSVServer() throws IOException {
        server = new ServerSocket(Constant.SERVER_PORT);
        receiveFiles();
    }
    
    public void start() throws IOException {
        Socket socket = null;
        while (true) {
            socket = server.accept();
            socket.setSoTimeout(5000);
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream("dest" + String.valueOf(count++) + ".csv");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.out.println("Connect to client " 
                    + socket.getInetAddress().getHostName() 
                    + " " 
                    + String.valueOf(socket.getPort()));
            byte[] bytearray = new byte[1024];
            int count = 0;
            while ((count = bis.read(bytearray, 0, bytearray.length)) != -1) {
                baos.write(bytearray);
            }
            bos.write(baos.toByteArray());
            bos.flush();
            bos.close();
            socket.close();
        }
    }
    
    public void receiveFiles() throws IOException {
        Socket socket = null;
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
        while (true) {
            socket = server.accept();
            socket.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            
            System.out.println("Connect to client " 
                    + socket.getInetAddress().getHostName() 
                    + " " 
                    + String.valueOf(socket.getPort()));
            int filesNum = dis.readInt();
            for (int i = 0; i < filesNum; i++) {
                
                String fileName = dis.readUTF();
                FileOutputStream fos = new FileOutputStream(path + "\\" + fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                long length = dis.readLong();
                for (int j = 0; j < length; j++)
                    baos.write(dis.readByte());
                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
                fos.close();
                baos.close();
            }
            count++;
//            byte[] bytearray = new byte[1024];
//            int count = 0;
//            while ((count = bis.read(bytearray, 0, bytearray.length)) != -1) {
//                baos.write(bytearray);
//            }
//            bos.write(baos.toByteArray());
//            bos.flush();
//            bos.close();
//            socket.close();
        }
    }
    
    public static void main(String[] args) {
        try {
            new CSVServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
