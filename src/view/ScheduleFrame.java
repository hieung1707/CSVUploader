/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import constant.Constant;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import socket.CSVClient;
import util.CSVReader;
import util.DataParser;
import util.ExcelFileReader;
import util.XLSReader;
import util.FileConverter;

/**
 *
 * @author ASUS
 */
public class ScheduleFrame extends javax.swing.JFrame implements ActionListener {

    private boolean isOnline;
    private ButtonGroup startRadioGroup;
    private ButtonGroup loopRadioGroup;
    private Timer timer;
    private String path;

    /**
     * Creates new form ScheduleFrame
     */
    public ScheduleFrame(String path) {
        this.path = path;
        initComponents();
        setTitle("File scheduler");
        setResizable(false);
        setupButtonGroups();
        setLocationRelativeTo(null);
    }
    
    private void convertToCallFile(String path) {
        try {
            
            ExcelFileReader excelReader;
            if (path.endsWith(".csv"))
                excelReader = new CSVReader(path);
            else
                excelReader = new XLSReader(path);
            List<String> listHeaders = excelReader.getHeaders();
            List<List<String>> listRows = excelReader.getRows();
            FileConverter converter = new FileConverter();
            converter.convertToCallFile(listRows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtonGroups() {
        startRadioGroup = new ButtonGroup();
        startRadioGroup.add(radioStartNow);
        startRadioGroup.add(radioStartDelay);

        loopRadioGroup = new ButtonGroup();
        loopRadioGroup.add(radioLoopOnce);
        loopRadioGroup.add(radioLoopInterval);
    }

    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getName();
            }
        }

        return null;
    }

    private long calculateDelay(String startType) {
        long delay = 0;
        if (startType.equals("start_delay")) {
            int hour = Integer.parseInt((String) comboHour.getSelectedItem());
            int minute = Integer.parseInt((String) comboMinute.getSelectedItem());
            Date date = txtDate.getDate();
            String dateString = DataParser.parseDateToString(date);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of(Constant.TIMEZONE));
            String[] timeSplit = dateString.split("-");
            ZonedDateTime nextRun = now.withYear(Integer.parseInt(timeSplit[2]))
                    .withMonth(Integer.parseInt(timeSplit[1]))
                    .withDayOfMonth(Integer.parseInt(timeSplit[0]))
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0);
            ChronoUnit unit = ChronoUnit.SECONDS;
            delay = unit.between(now, nextRun);
//            ZonedDateTime nextRun = now.with
        }
        return delay;
    }
    
    public boolean isInteger(String numStr) {
       String pt = "[0-9]+";
       return Pattern.matches(pt, numStr);
    }
    
    public boolean isIP(String ip) {
        String subnet = "[0-9]{1,3}";
        String pt = subnet + "[.]" + subnet + "[.]" + subnet + "[.]" + subnet;
        return Pattern.matches(pt, ip) || ip.equals("localhost");
    }

    private void timerErrorHandler() {
        JOptionPane.showMessageDialog(getParent(), "An error has occured");
        isOnline = false;
        changeStatus();
        timer.cancel();
    }

    private void startSchedule() {
        try {
            String ip = txtIP.getText().trim();
            String portStr = txtPort.getText().trim();
            String startType = getSelectedButtonText(startRadioGroup);
            String loopType = getSelectedButtonText(loopRadioGroup);
            if (startType == null || loopType == null) {
                JOptionPane.showMessageDialog(this, "Please check the radio boxes");
                return;
            }
            if (ip.equals("") || portStr.equals("")) {
                JOptionPane.showMessageDialog(this, "Please enter server address");
                return;
            }
            if (!isIP(ip) || !isInteger(portStr)) {
                JOptionPane.showMessageDialog(this, "Invalid IP and Port");
                return;
            }
            long delay = calculateDelay(startType) * 1000; // calculate delay in miliseconds between time of interacting to time the task start
            if (delay < 0) {
                JOptionPane.showMessageDialog(this, "Start time is from the past. Please select another date");
                return;
            }
            int port = Integer.parseInt(portStr);
            long interval = 0;
            if (loopType.equals("loop_interval")) {
                if (!isInteger(txtInterval.getText())) {
                    JOptionPane.showMessageDialog(this, "Interval should be integer");
                    return;
                }
                interval = Long.parseLong(txtInterval.getText()) * 60 * 1000;
            }
            btnSchedule.setText("Stop");
            isOnline = true;
            changeStatus();
            FileSenderThread thread = new FileSenderThread(delay, interval, ip, port);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endSchedule() {
        isOnline = false;
        btnSchedule.setText("Start");
        lblNextTransmit.setText("");
        changeStatus();
        if (timer != null)
            timer.cancel();
    }

    private void changeStatus() {
        if (isOnline) {
            txtStatus.setForeground(Color.GREEN);
            txtStatus.setText("Online");
            radioStartNow.setEnabled(false);
            radioStartDelay.setEnabled(false);
            radioLoopOnce.setEnabled(false);
            radioLoopInterval.setEnabled(false);
            comboHour.setEnabled(false);
            comboMinute.setEnabled(false);
            txtDate.setEnabled(false);
            txtInterval.setEnabled(false);
            txtIP.setEnabled(false);
            txtPort.setEnabled(false);
        } else {
            txtStatus.setForeground(new Color(0, 0, 0));
            txtStatus.setText("Offline");
            radioStartNow.setEnabled(true);
            radioStartDelay.setEnabled(true);
            radioLoopOnce.setEnabled(true);
            radioLoopInterval.setEnabled(true);
            txtIP.setEnabled(false);
            txtPort.setEnabled(false);
            radioStartClick();
            radioLoopClick();
        }
    }

    private void btnScheduleClick() {
        if (!isOnline) {
            startSchedule();
        } else {
            endSchedule();
        }
    }

    private void radioStartClick() {
        if (radioStartNow.isSelected()) {
            comboHour.setEnabled(false);
            comboMinute.setEnabled(false);
            txtDate.setEnabled(false);
        } else {
            comboHour.setEnabled(true);
            comboMinute.setEnabled(true);
            txtDate.setEnabled(true);
        }
    }

    private void radioLoopClick() {
        if (radioLoopOnce.isSelected()) {
            txtInterval.setEnabled(false);
        } else {
            txtInterval.setEnabled(true);
        }
    }
    
    private void btnBackClick() {
        if (isOnline)
            endSchedule();
        CSVFrame frame = new CSVFrame(path);
        frame.setVisible(true);
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

        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        radioStartNow = new javax.swing.JRadioButton();
        txtDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        radioStartDelay = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        radioLoopInterval = new javax.swing.JRadioButton();
        radioLoopOnce = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtInterval = new javax.swing.JTextField();
        btnSchedule = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JLabel();
        comboHour = new javax.swing.JComboBox<>();
        comboMinute = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtLastTransmit = new javax.swing.JLabel();
        lblNextTransmit = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtIP = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Time:");

        radioStartNow.setText("Now");
        radioStartNow.setName("start_now");
        radioStartNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioStartNowActionPerformed(evt);
            }
        });
        radioStartNow.setActionCommand("start_now");
        radioStartNow.addActionListener(this);

        txtDate.setDateFormatString("dd-MM-yyyy");

        jLabel2.setText("Date:");

        radioStartDelay.setText("Specific time");
        radioStartDelay.setName("start_delay");
        radioStartDelay.setActionCommand("start_delay");
        radioStartDelay.addActionListener(this);

        jLabel3.setText("Start from");

        radioLoopInterval.setText("Once every interval (minute)");
        radioLoopInterval.setName("loop_interval");
        radioLoopInterval.setActionCommand("loop_interval");
        radioLoopInterval.addActionListener(this);

        radioLoopOnce.setText("Only once");
        radioLoopOnce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioLoopOnceActionPerformed(evt);
            }
        });
        radioLoopOnce.setName("loop_once");
        radioLoopOnce.setActionCommand("loop_once");
        radioLoopOnce.addActionListener(this);

        jLabel4.setText("Loop");

        jLabel5.setText("Interval");

        btnSchedule.setText("Start");
        btnSchedule.addActionListener(this);
        btnSchedule.setActionCommand("schedule");

        jLabel6.setText("Status:");

        txtStatus.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtStatus.setText("Offline");

        comboHour.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        int count = comboHour.getItemCount();
        for (int i = 0; i < count; i++) {
            comboHour.removeItemAt(0);
        }
        for (int i = 1; i <= 24; i++) {
            comboHour.addItem(String.format("%02d", i));
        }
        comboHour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboHourActionPerformed(evt);
            }
        });

        comboMinute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        for (int i = 0; i < count; i++) {
            comboMinute.removeItemAt(0);
        }
        for (int i = 0; i < 60; i++)
        comboMinute.addItem(String.format("%02d", i));

        jLabel7.setText(":");

        jLabel8.setText("Last transmit:");

        jLabel9.setText("Next transmit:");

        btnBack.setText("Back");
        btnBack.setActionCommand("back");
        btnBack.addActionListener(this);

        jLabel10.setText("Server");

        txtIP.setText("localhost");
        txtIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIPActionPerformed(evt);
            }
        });

        jLabel11.setText("IP");

        txtPort.setText("10000");

        jLabel12.setText("Port");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBack))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtStatus))
                            .addComponent(radioLoopInterval)
                            .addComponent(jLabel4)
                            .addComponent(radioLoopOnce)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(comboHour, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel7)
                                                .addGap(2, 2, 2)
                                                .addComponent(comboMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(txtLastTransmit, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblNextTransmit, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel11)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel12)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(radioStartDelay)
                            .addComponent(radioStartNow)
                            .addComponent(jLabel8))
                        .addGap(24, 24, 24))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {comboHour, comboMinute});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblNextTransmit, txtLastTransmit});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel11, jLabel12, jLabel2, jLabel5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioStartNow)
                .addGap(1, 1, 1)
                .addComponent(radioStartDelay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioLoopOnce)
                .addGap(1, 1, 1)
                .addComponent(radioLoopInterval)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addComponent(btnSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtLastTransmit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblNextTransmit))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtStatus))
                .addGap(21, 21, 21))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel8, lblNextTransmit, txtLastTransmit});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radioLoopOnceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioLoopOnceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioLoopOnceActionPerformed

    private void radioStartNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioStartNowActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioStartNowActionPerformed

    private void comboHourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboHourActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboHourActionPerformed

    private void txtIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIPActionPerformed

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
            java.util.logging.Logger.getLogger(ScheduleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScheduleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScheduleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScheduleFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ScheduleFrame("test_data.xlsx").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSchedule;
    private javax.swing.JComboBox<String> comboHour;
    private javax.swing.JComboBox<String> comboMinute;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblNextTransmit;
    private javax.swing.JRadioButton radioLoopInterval;
    private javax.swing.JRadioButton radioLoopOnce;
    private javax.swing.JRadioButton radioStartDelay;
    private javax.swing.JRadioButton radioStartNow;
    private com.toedter.calendar.JDateChooser txtDate;
    private javax.swing.JTextField txtIP;
    private javax.swing.JTextField txtInterval;
    private javax.swing.JLabel txtLastTransmit;
    private javax.swing.JTextField txtPort;
    private javax.swing.JLabel txtStatus;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();
        switch (command) {
            case "schedule":
                btnScheduleClick();
                break;
            case "start_now":
                radioStartClick();
                break;
            case "start_delay":
                radioStartClick();
                break;
            case "loop_once":
                radioLoopClick();
                break;
            case "loop_interval":
                radioLoopClick();
                break;
            case "back":
                btnBackClick();
                break;
        }
    }
    
    private class FileSenderThread extends Thread {
        
        private long delay;
        private long interval;
        private String ip;
        private int port;
        
        public FileSenderThread(long delay, long interval, String ip, int port) {
            this.delay = delay;
            this.interval = interval;
            this.ip = ip;
            this.port = port;
        }
        
        @Override
        public void run() {
            timer = new Timer();
            if (interval == 0)
                sendFileLoopOnce();
            else
                sendFileLoopInterval();
        }
        
        private void sendFileLoopOnce() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        convertToCallFile(path);
                        new CSVClient(ip, port).sendFiles();
                        txtLastTransmit.setText(DataParser.parseDateTimeToString(new Date()));
                        lblNextTransmit.setText("");
                        JOptionPane.showMessageDialog(getParent(), "File sent");
                        endSchedule();
                    } catch (HeadlessException | IOException e) {
                        e.printStackTrace();
                        timerErrorHandler();
                    }
                }
            }, delay);
        }
        
        private void sendFileLoopInterval() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        convertToCallFile(path);
                        new CSVClient(ip, port).sendFiles();
                        Date currentTime = new Date();
                        txtLastTransmit.setText((DataParser.parseDateTimeToString(currentTime)));
                        lblNextTransmit.setText(calculateNextTransmitTime(currentTime));
                    } catch (IOException e) {
                        e.printStackTrace();
                        timerErrorHandler();
                    }
                }
            }, delay, interval);
        }
        
        private String calculateNextTransmitTime(Date date) {
            long dateInMil = date.getTime();
            long nextDateInMil = dateInMil + interval;
            Date nextDate = new Date(nextDateInMil);
            return DataParser.parseDateTimeToString(nextDate);
        }
    }
}
