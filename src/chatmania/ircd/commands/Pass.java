package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;
import chatmania.ircd.services.nickserv.NickServ;

import java.util.StringTokenizer;

public class Pass implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() == 1) {

                String senha = st.nextToken();
                new NickServ().execute(tmpUser, "NICKSERV IDENTIFY " + senha);

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/pass password193<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Pass.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
