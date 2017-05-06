package chatmania.extra.timer;

import chatmania.Channel;
import chatmania.Server;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQL;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.Config;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

public class Timer implements Runnable {

    Thread thisThread;

    public Timer() {

        thisThread = new Thread(this);
        thisThread.start();

    }

    public void run() {

        try {

            while (true) {

                // inicia contagem de acordo com que o xml.properties
                // especifica, convertendo para minutos
                Thread.sleep(TimerConfig.TIME * 60000);

                System.out.println("### Atualizando XML e SQL " + new Date());

                // atualiza arquivo channel.xml e tabela sql server
                updateChannels();

                // atualiza tabela sql server
                SQLQuery.updateAll();

            }

        } catch (InterruptedException e) {

            new LogIRCd("Timer.java", "run()", "" + e + "");

        }

    }

    private void updateChannels() {

        try {

            // deleta toda a lista de salas do SQL
            SQLQuery.removeChannels();

            // cria contrutor de saida
            PrintWriter saida = new PrintWriter(new FileWriter("wwwroot/public/xml/channels.xml"), true);

            // grava novos dados
            saida.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
            saida.println("<rss version=\"1.0\">");
            saida.println("<channels>");
            saida.println("<network>" + Config.NETWORK + "</network>");
            saida.println("<link>" + Config.URL + "</link>");

            // inteiro que ira definir um numero limite de salas para impressao
            int i = 0;

            // lista todos os canais
            for (Enumeration enu = Server.getChannelElements(); enu.hasMoreElements(); ) {

                if (i > 20)
                    break;

                Channel temp = (Channel) enu.nextElement();

                // se sala nao estiver em modo secreto
                if (!temp.isSecret() && (temp.getMemberSize() < Config.MAXUSERCHANNEL)) {

                    // adiciona ao XML
                    saida.println("<item>");
                    saida.println("<title>" + temp.getName() + "</title>");
                    saida.println("<webchat>" + Config.URL + "/blah.irc?sala=" + temp.getName().substring(1) + "</webchat>");
                    saida.println("<topic>" + temp.getTopic() + "</topic>");
                    saida.println("<users>" + temp.getMemberSize() + "</users>");
                    saida.println("</item>");

                    // adiciona ao SQL
                    String sala = temp.getName().substring(1);
                    String webchat = Config.URL + "/blah.irc?sala=" + sala;
                    String topico = Util.mircHtml(SQL.antiInject(temp.getTopic()));
                    SQLQuery.setChannel(sala, temp.getMemberSize(), webchat, topico);

                }

            }

            saida.println("</channels>");
            saida.println("</rss>");

            // fecha arquivo
            saida.close();

        } catch (Exception e) {

            new LogIRCd("Timer.java", "updateChannelXML()", e + "");

        }

    }

}