package chatmania.ircd.services.news;

import chatmania.extra.log.LogIRCd;

import java.io.FileInputStream;
import java.util.Properties;

public class NewsConfig {

    public static final Properties file = new Properties();

    // news
    public static String NAME = "";
    public static String URL = "";
    public static int TIME = 0;
    public static int REFRESH = 0;

    public static void config() {

        try {

            file.load(new FileInputStream("conf/services/news.properties"));

            // news
            NAME = file.getProperty("name");
            URL = file.getProperty("url");
            TIME = Integer.parseInt(file.getProperty("time"));
            REFRESH = Integer.parseInt(file.getProperty("refresh"));

        } catch (Exception e) {

            new LogIRCd("NewsConfig.java", "", "" + e + "");

        }

    }

}