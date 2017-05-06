package chatmania.ircd.services.news;

import chatmania.Server;
import chatmania.User;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQLQuery;

import java.util.Enumeration;

public class NewsTimerEnvia implements Runnable {

    Thread thisThread;

    public NewsTimerEnvia() {

        thisThread = new Thread(this);
        thisThread.start();

    }

    public void run() {

        // faz o services News enviar noticias

        while (true) {

            try {

                // inicia contagem do tempo definido pelo usu�rio
                // para que o News envie noticias aos users
                Thread.sleep(NewsConfig.TIME * 60000);

            } catch (Exception e) {
            }

            try {

                // lista todos os usuarios
                for (Enumeration<User> enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                    // pega instancia do usuario
                    User tmpUser = enu.nextElement();

                    // se usu�rio n�o estiver identificado
                    if (!tmpUser.isIdentify()) {
                        News.enviaNoticia(tmpUser);
                    } else {

                        // se usuario quiser receber a news
                        if (!SQLQuery.isNews(tmpUser.getNick()))
                            News.enviaNoticia(tmpUser);

                    }

                }

            } catch (Exception e) {

                new LogIRCd("NewsTimerEnvia.java", "", "" + e + "");

            }

        }

    }

}