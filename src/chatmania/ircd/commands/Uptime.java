package chatmania.ircd.commands;

import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

public class Uptime implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            long ut = (System.currentTimeMillis() - Server.getUptime()) / 1000; //time server has been up (sec).
            long days = (ut / 86400); //gives the total number of days
            long hrs = (ut % 86400); //give the remainder of hours
            long hours = (hrs / 3600); // gives the total number of hours
            long min = (hrs % 3600); // gives remainder of minutes and sec
            long mins = (min / 60); // gives the minutes
            long sec = (min % 60); // gives the remaining seconds
            Message.tellUser(tmpUser, "Atual", Lang.traduz(tmpUser, "webchat_065", days + "%%" + hours + "%%" + mins + "%%" + sec), 1);

        } catch (Exception e) {

            new LogIRCd("Uptime.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
