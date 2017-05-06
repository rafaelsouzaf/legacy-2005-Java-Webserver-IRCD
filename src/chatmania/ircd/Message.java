package chatmania.ircd;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Util;

import java.util.Enumeration;

public class Message {

    public static void noticeSvs(User tmpUser, String titulo, String conteudo) {

        // publica whois para o webchat do usuario
        Message.tellUserAlternativeWeb(tmpUser, "<script>imprimeNotice('" + titulo + "', '" + conteudo + "');</script>");

    }

    public static void noticeOpers(String msg) {

        for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

            User temp = (User) enu.nextElement();
            if (temp.isIRCop() || temp.isAdmin()) {
                tellUser(temp, "Atual", "*** " + msg, 0);
            }

        }

    }

    public static void noticeAll(String msg) {

        for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); )
            tellUser((User) enu.nextElement(), "Atual", msg, 0);

    }

    public static void tellUsersInChan(Channel chanID, String msg, boolean pegaUsuarios) {

        for (Enumeration enu = chanID.getList(); enu.hasMoreElements(); ) {

            User temp = (User) enu.nextElement();
            if (temp != null) {

                tellUser(temp, chanID.getName(), msg, 0);

                if (pegaUsuarios)
                    Util.pegaUsuarios(temp, chanID);

            }

        }

    }

    public static void tellOpsInChan(Channel chanID, String msg) {

        for (Enumeration enu = chanID.getList(); enu.hasMoreElements(); ) {

            User temp = (User) enu.nextElement();
            if ((temp != null) && (chanID.getMemberStatus(temp) >= 2))
                tellUser(temp, chanID.getName(), msg, 0);

        }

    }

    public static void tellUser(User tmpUser, String janela, String msg, int hierarquia) {

        try {

            tmpUser.getOutput().write(("<script>main('" + janela + "', '" + Util.filtraAcentos(msg) + "<br>', " + hierarquia + ", '1');</script> \n").getBytes());
            tmpUser.getOutput().flush();

        } catch (Exception e) {
        }

    }

    // esse metodo serve para enviar comandos javascript
    // e outros que nao podem ser enviados pelo tellUser acima
    // usado tambem para o alert() quando usuario entra na
    // sala que tem modo +x (imagem)
    public static void tellUserAlternativeWeb(User tmpUser, String msg) {

        try {

            tmpUser.getOutput().write((msg + "\n").getBytes());
            tmpUser.getOutput().flush();

        } catch (Exception e) {
        }

    }
}