package chatmania;

import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.mail.MailConfig;
import chatmania.extra.sql.SQL;
import chatmania.extra.sql.SQLConfig;
import chatmania.extra.timer.Timer;
import chatmania.extra.timer.TimerConfig;
import chatmania.ircd.Config;
import chatmania.ircd.Listener;
import chatmania.ircd.services.chanserv.ChanServConfig;
import chatmania.ircd.services.news.NewsConfig;
import chatmania.ircd.services.news.NewsTimerEnvia;
import chatmania.ircd.services.news.NewsTimerRefresh;
import chatmania.ircd.services.nickserv.NickServConfig;

public class ChatMania {

    public static void main(String[] args) {

        // L� arquivos de configura�ao
        loadConfig();

        new SQL(); // inicia SQL
        new NewsTimerRefresh(); // inicia Timer do News para atualiza��o
        new NewsTimerEnvia(); // inicia Timer do News para envio
        new Timer(); // inicia Timer do XML
        new Kline(); // carrega lista de Klines
        new Lang(); // carrega os arquivos de idiomas

        // abre as portas
        loadListener();

    }

    public static void loadConfig() {

        SQLConfig.config(); // pega constants do SQL
        Config.config(); // pega constants do ircd
        TimerConfig.config(); // pega constants do timer
        MailConfig.config(); // pega constants do email
        NickServConfig.config(); // pega constants do nickserv
        ChanServConfig.config(); // pega constants do chanserv
        NewsConfig.config(); // pega constants do news

    }

    private static void loadListener() {

        // inicia portas do webserver
        for (String porta : Config.PORTS.split(";"))
            new RunWeb(Integer.parseInt(porta));

        System.out.println("WebServer Ports: " + Config.PORTS);

    }

    private static class RunWeb implements Runnable {

        private int porta;

        public RunWeb(int porta) {

            this.porta = porta;
            new Thread(this).start();

        }

        public void run() {

            try {

                new Listener(porta);

            } catch (Exception e) {

                new LogIRCd("RunWeb.java", "run()", e + "");

            }

        }

    }

}