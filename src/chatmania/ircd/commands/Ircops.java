package chatmania.ircd.commands;

import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.ArrayList;
import java.util.Enumeration;

public class Ircops implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            // bloco que lista ircops online
            boolean sera = false;

            // string de saida
            StringBuffer saida = new StringBuffer();

            Message.tellUser(tmpUser, "Atual", Lang.traduz(tmpUser, "webchat_062", ""), 0);
            for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                User temp = (User) enu.nextElement();
                if (temp.isIRCop()) {

                    saida.append("-> " + temp.getNick() + " <font color=black>(" + temp.getHost() + ")</font><br>");
                    sera = true;

                }

            }
            if (sera)
                Message.tellUser(tmpUser, "Atual", saida.toString(), 0);
            else
                Message.tellUser(tmpUser, "Atual", Lang.traduz(tmpUser, "webchat_063", ""), 0);

            // pega a lista de ircops
            ArrayList ircops = SQLQuery.getIRCops();

            // se houver ircop, entao lista
            if (!ircops.isEmpty()) {

                Message.tellUser(tmpUser, "Atual", Lang.traduz(tmpUser, "webchat_064", ""), 0);
                for (Object separa : ircops)
                    Message.tellUser(tmpUser, "Atual", "-> " + separa, 0);

            }

        } catch (Exception e) {

            new LogIRCd("Ircops.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
