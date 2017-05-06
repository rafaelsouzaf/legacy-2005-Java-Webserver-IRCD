package chatmania.ircd.services.news;

import chatmania.extra.log.LogIRCd;

public class NewsTimerRefresh implements Runnable {

    Thread thisThread;

    public NewsTimerRefresh() {

        thisThread = new Thread(this);
        thisThread.start();

    }

    public void run() {

        // faz o services News atualizar o XML dentro do intervalo
        // de tempo definido

        try {

            // inicia contagem de 10 segundos, assim que o ircd inicia
            Thread.sleep(30000);
            News.pegaNoticias();

            while (true) {

                // inicia contagem de acordo com que o ircd.properties
                // especifica, convertendo para minutos
                Thread.sleep(NewsConfig.REFRESH * 60000);
                News.pegaNoticias();

            }

        } catch (InterruptedException e) {

            new LogIRCd("NewsTimerRefresh.java", "", "" + e + "");

        }

    }

}