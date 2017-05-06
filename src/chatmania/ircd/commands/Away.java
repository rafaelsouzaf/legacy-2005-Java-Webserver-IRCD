package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Away implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            // se usuario estiver away, entao
            // seta como NAO away
            if (tmpUser.isAway()) {

                tmpUser.setBack();
                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_048", "") + "</font>", 0);

            } else if (!tmpUser.isAway() && (st.countTokens() == 0)) {

                tmpUser.setAway("Pescando");
                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_046", "Pescando") + "</font>", 0);

            } else if (!tmpUser.isAway()) {

                String msg = cmd.substring(5);
                tmpUser.setAway(msg);
                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_046", msg) + "</font>", 0);

            }

        } catch (Exception e) {

            new LogIRCd("Away.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
