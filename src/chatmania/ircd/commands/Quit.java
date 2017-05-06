package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;

public class Quit implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            Util.releaseUserID(tmpUser, cmd.substring(5));

        } catch (Exception e) {

            new LogIRCd("Quit.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
