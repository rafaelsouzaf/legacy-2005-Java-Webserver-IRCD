package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

public class Unknow implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_019", cmd) + "</font>", 0);

        } catch (Exception e) {

            new LogIRCd("Unknow.java", tmpUser.getNick() + " => /" + cmd, e + "");

        }

    }

}
