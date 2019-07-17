/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Timer;
import socket.CSVClient;

/**
 *
 * @author ASUS
 */
public class ScheduleTest {

    public void test() {
        Timer timer = new Timer();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Etc/GMT-7"));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        int dur = 20;
        ZonedDateTime nextRun = now.withSecond(0);
        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusMinutes(1);
        }
        Duration duration = Duration.between(now, nextRun);
        long initalDelay = duration.getSeconds();
//        timer.schedule(new MyTask(), 0, initalDelay);
        timer.schedule(new MyTask(),
                0,
                1000);
    }
    
    public static void main(String[] args) {
        new ScheduleTest().test();
    }
    
    public class MyTask extends TimerTask {

        @Override
        public void run() {
            try {
                new CSVClient("input.csv").sendFiles();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    public class MyRunnableTask implements Runnable {

        @Override
        public void run() {
            System.out.println("hello");
        }

    }
}
