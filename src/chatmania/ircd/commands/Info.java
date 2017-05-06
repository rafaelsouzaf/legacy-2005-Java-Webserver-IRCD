package chatmania.ircd.commands;

import chatmania.User;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

public class Info implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringBuffer message = new StringBuffer();
            message.append(" *  <br>");
            message.append(" *  ChatMania v0.5<br>");
            message.append(" *  by Rafael S. Fijalkowski<br>");
            message.append(" *  <br>");
            message.append(" *  A id�ia � simplificar. Ent�o tudo no software � simplificado<br>");
            message.append(" *  comparado aos IRCds tradicionais. Simplifiquei os arquivos de<br>");
            message.append(" *  configura��o, simplifiquei os comandos e principalmente os modos<br>");
            message.append(" *  de usu�rios e salas. Simplifiquei o modo de utilizar e de<br>");
            message.append(" *  administrar, deixando tudo bem mais simples e amig�vel.<br>");
            message.append(" * ");
            Message.tellUser(tmpUser, "Atual", message.toString(), 0);

            message = null;

        } catch (Exception e) {

            new LogIRCd("Info.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}