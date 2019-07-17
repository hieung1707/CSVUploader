/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import util.CSVReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import util.ExcelFileReader;
import util.XLSReader;

/**
 *
 * @author ASUS
 */
public class CSVFrame extends javax.swing.JFrame implements ActionListener {
    
    private DefaultTableModel modelCSV;
    private List<List<String>> listRows;
    private List<String> listHeaders;
    private String filePath;
    
    /**
     * Creates new form CSVFrame
     */
    public CSVFrame() {
        initComponents();
        setTitle("File chooser");
        setResizable(false);
        setLocationRelativeTo(null);
    }
    
    public CSVFrame(String path) {
        this.filePath = path;
        initComponents();
        setTitle("File chooser");
        setResizable(false);
        setLocationRelativeTo(null);
        setupTableData();
    }
    
    private void setupTableData() {
        try {
            ExcelFileReader reader;
            if (filePath.endsWith("csv"))
                reader = new CSVReader(filePath);
            else
                reader = new XLSReader(filePath);
//            CSVReader reader = new CSVReader(filePath);
            listRows = reader.getRows();
            listHeaders = reader.getHeaders();
            if (listRows.size() > 0) {
                String[] headers = listHeaders.stream().toArray(String[]::new);
                String[][] data = new String[listRows.size()][listRows.get(0).size()];
                for (int i = 0; i < listRows.size(); i++) {
                    for (int j = 0; j < listHeaders.size(); j++) {
                        data[i][j] = listRows.get(i).get(j);
                    }
                }
                modelCSV = new DefaultTableModel(data, headers);
                tblCSV.setModel(modelCSV);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean checkFileExtension(File file) {
        String fileName = file.getName().toLowerCase();
        if(file.isFile() && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv")))
            return true;
        return false;
    }
    
    private void btnChooseFileClick() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!checkFileExtension(file)) {
                JOptionPane.showMessageDialog(this, "Incompatible file extension. Only accept csv File");
                return;
            }
            String filePath = file.getAbsolutePath();
            txtName.setText(fileChooser.getSelectedFile().getName());
            this.filePath = filePath;
            setupTableData();
        }
    }
    
    private void btnSetupScheduleClick() {
        ScheduleFrame frm = new ScheduleFrame(filePath);
        frm.setVisible(true);
        this.dispose();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCSV = new javax.swing.JTable();
        btnSelectFile = new javax.swing.JButton();
        txtName = new javax.swing.JLabel();
        btnSetupSchedule = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblCSV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblCSV);

        btnSelectFile.setText("Select File");
        btnSelectFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFileActionPerformed(evt);
            }
        });
        btnSelectFile.addActionListener(this);
        btnSelectFile.setActionCommand("choose_file");

        txtName.setText("[Please select file ...]");

        btnSetupSchedule.setText("Setup Schedule");
        btnSetupSchedule.setActionCommand("schedule");
        btnSetupSchedule.addActionListener(this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSelectFile)
                        .addGap(18, 18, 18)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSetupSchedule)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectFile)
                    .addComponent(txtName)
                    .addComponent(btnSetupSchedule))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectFileActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSelectFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CSVFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CSVFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CSVFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CSVFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CSVFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSelectFile;
    private javax.swing.JButton btnSetupSchedule;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCSV;
    private javax.swing.JLabel txtName;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case "choose_file":
                btnChooseFileClick();
                break;
            case "schedule":
                btnSetupScheduleClick();
                break;
        }
    }
}