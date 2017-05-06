package chatmania.ircd.commands;

import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import chatmania.ircd.services.nickserv.NickServ;

import java.util.StringTokenizer;

public class Nick implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() >= 1) {

                String novoNick = st.nextToken();

                // se nick for ilegal entao avisa
                if (!Util.isValidNick(novoNick)) {

                    Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_050", novoNick) + "</font>", 0);

                    // se nick estiver forbidado
                } else if (SQLQuery.isForbid(novoNick)) {

                    Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_049", novoNick) + "</font>", 0);

                    // se user nao existir
                } else if (Server.getUser(novoNick) == null) {

                    // se usuario j� estiver conectado, ou seja, j� foi
                    // adicionado a cole�ao de users
                    if (tmpUser.isWebchat()) {

                        // verifica se ele esta mudando o apelido no intervalo de tempo permitido
                        if ((System.currentTimeMillis() - (Config.TIMENEWNICK * 1000)) > tmpUser.getLastNickChangeTime()) {

                            // atualiza tempo
                            tmpUser.setLastNickChangeTime(System.currentTimeMillis());

                            // muda o nick
                            Util.changeNick(tmpUser, novoNick);

                            // manda nickserv procurar por apelido novo
                            NickServ.verNickTime(tmpUser);

                        } else {

                            // avisa para aguardar alguns segundos
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_018", "") + "</font>", 0);

                        }

                        // senao � novo usuario
                    } else {

                        // define usuario como webchat
                        tmpUser.setWebchat(true);

                        // seta host do usuario
                        tmpUser.setHost();

                        // define apelido
                        Util.changeNick(tmpUser, novoNick);

                        // adiciona user a cole�ao
                        Server.setUser(tmpUser, novoNick);

                        // avisa ircops que usuario conectou
                        Message.noticeOpers(Lang.traduz(tmpUser, "webchat_014", novoNick + "%%" + tmpUser.getIP()));

                    }

                } else {

                    Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_017", novoNick) + "</font>", 0);

                }

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/nick Maria<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            e.printStackTrace();
            new LogIRCd("Nick.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
