package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Topic implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() >= 2) {

                String sala = st.nextToken();
                Channel tmpChan = Server.getChannel(sala);
                String topic = cmd.substring(7 + sala.length());

                // se sala existir
                if (tmpChan != null) {

                    // se usuario for OP ou Host
                    if (tmpChan.getMemberStatus(tmpUser) >= 2) {

                        tmpChan.setTopic(topic);
                        tmpChan.setTopicOwner(tmpUser.getNick());
                        Message.tellUsersInChan(tmpChan, "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_015", tmpUser.getNick() + "%%" + Util.mircHtml(topic)) + "</font>", false);

                    } else {

                        // avisa que usuario nao tem status suficiente
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                    }

                } else {

                    // avisa que sala nao existe
                    Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                }

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/topic #channel bla bla bla<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Topic.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
