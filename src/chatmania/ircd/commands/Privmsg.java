package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Privmsg implements CommandsI {

    private static String ircNick(String nick) {

        return "<font color=blue>&lt;</font>" + nick + "<font color=blue>&gt;</font> ";

    }

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() >= 2) {

                String vitima = st.nextToken();
                String mensagem = cmd.substring(vitima.length() + 9);

                // atualiza tempo da ultima mensagem enviada util para
                // fazer o calculo de quanto tempo est� inativo no whois
                tmpUser.setTempoInativo(System.currentTimeMillis());

                // se vitima for uma sala
                if (vitima.charAt(0) == '#') {

                    // verifica se sala existe
                    Channel chanID = Server.getChannel(vitima.toLowerCase());
                    if (chanID != null) {

                        // verifica se usuario eh membro
                        if (chanID.isMember(tmpUser)) {

                            // se sala nao for moderada
                            // ou se sala for moderada e usuario for OP
                            if ((!chanID.isModerado()) || (chanID.isModerado() && (chanID.getMemberStatus(tmpUser) > 0))) {

                                // se usuario estiver com zombie, s� ele recebe a mensagem
                                if (tmpUser.isZombie())
                                    Message.tellUser(tmpUser, chanID.getName(), ircNick(tmpUser.getNick()) + Util.mircHtml(mensagem), 0);
                                else
                                    Message.tellUsersInChan(chanID, ircNick(tmpUser.getNick()) + Util.mircHtml(mensagem), false);

                            } else {

                                // avisa que mensagem nao pode ser enviada
                                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_034", "") + "</font>", 0);

                            }

                        } else {

                            // avisa que usuario precisa ser membro
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_034", "") + "</font>", 0);

                        }

                    } else {

                        // avisa que sala nao existe
                        Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", vitima) + "</font>", 0);

                    }

                    // senao eh um pvt
                } else {

                    // pega instancia de usuario
                    User tmpUID = Server.getUser(vitima);

                    // se usuario existir
                    if (tmpUID != null) {

                        // se usuario estiver com zombie, s� ele recebe a mensagem
                        if (tmpUser.isZombie()) {

                            // envia mensagem para meu pvt
                            Message.tellUser(tmpUser, tmpUID.getNick(), ircNick(tmpUser.getNick()) + Util.mircHtml(mensagem), 0);

                        } else {

                            // envia mensagem para meu pvt
                            Message.tellUser(tmpUser, tmpUID.getNick(), ircNick(tmpUser.getNick()) + Util.mircHtml(mensagem), 0);

                            // envia mensagem para pvt da vitima
                            Message.tellUser(tmpUID, tmpUser.getNick(), ircNick(tmpUser.getNick()) + Util.mircHtml(mensagem), 0);

                        }

                        // modo de seguran�a se estiver ativado
                        if (!tmpUser.getLogadoArray().isEmpty()) {

                            for (Object tmpU : tmpUser.getLogadoArray()) {

                                User tmpU2 = (User) tmpU;

                                // se user for admin
                                if (tmpU2.isAdmin())
                                    Message.tellUser(tmpU2, "&Log&", "<font color=blue>&lt;" + tmpUser.getNick() + "&gt;</font> &lt;" + vitima + "&gt; " + Util.mircHtml(mensagem), 0);

                            }

                        }

                    } else {

                        // avisa que usuario nao existe
                        Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                    }

                }

            }

        } catch (Exception e) {

            new LogIRCd("Privmsg.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
