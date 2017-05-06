package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Part implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() == 1) {

                Channel tmpChan = Server.getChannel(st.nextToken().toLowerCase());

                if (tmpChan != null) {

                    if (tmpChan.isMember(tmpUser)) {

                        // retira canal da lista de canais do usuario
                        tmpUser.delNewChannel(tmpChan);

                        // remove usuario do canal
                        tmpChan.removeUser(tmpUser);

                        // envia mensagem para o canal relatando a saida do usuario
                        Message.tellUsersInChan(tmpChan, "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_001", tmpUser.getNick()) + "</font>", true);

                        // se sala tiver 0 usuarios, entao fecha a sala
                        // retirando canal da cole�ao
                        if (tmpChan.getMemberSize() == 0)
                            Server.unsetChannel(tmpChan.getName());

                    }

                }

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/part #channel<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Part.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
