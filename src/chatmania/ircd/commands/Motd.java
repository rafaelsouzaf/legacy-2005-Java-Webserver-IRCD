package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

public class Motd implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            Message.tellUser(tmpUser, "Status", "<font color=#FF0000>" + Lang.traduz(tmpUser, "motd", "") + "</font>", 0);

        } catch (Exception e) {

            new LogIRCd("Motd.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
