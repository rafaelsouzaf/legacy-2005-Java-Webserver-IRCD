package chatmania.ircd.commands;

import chatmania.Server;
import chatmania.User;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

public class Lusers implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            // stringbuffer que ira formar a resposta
            StringBuffer message = new StringBuffer();
            message.append("<table border='1' cellpadding='2' cellspacing='1' style='border-collapse: collapse' bordercolor='#111111' width='40%'>");
            message.append("	<tr>");
            message.append("		<td width='50%' height='30'>Salas</td>");
            message.append("		<td width='50%' height='30'>" + Server.getChannelSize() + "</td>");
            message.append("	</tr>");
            message.append("	<tr>");
            message.append("		<td width='50%' height='30'>Usu�rios</td>");
            message.append("		<td width='50%' height='30'>" + Server.getUserSize() + "</td>");
            message.append("	</tr>");
            message.append("</table>");

            Message.tellUser(tmpUser, "Status", message.toString(), 0);

        } catch (Exception e) {

            new LogIRCd("Lusers.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
