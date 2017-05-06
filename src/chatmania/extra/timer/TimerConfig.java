package chatmania.extra.timer;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class TimerConfig {

    public static final Properties file = new Properties();

    // webserver
    public static int TIME;

    public static void config() {

        try {

            file.load(new FileInputStream("conf/timer.properties"));

            // webserver
            TIME = Integer.parseInt(file.getProperty("time"));

        } catch (Exception e) {

            new LogIRCd("TimerConfig.java", "", "" + e + "");

        }

    }

}