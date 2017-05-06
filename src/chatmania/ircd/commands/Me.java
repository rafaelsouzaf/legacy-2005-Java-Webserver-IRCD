package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Me implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() >= 2) {

                String vitima = st.nextToken();
                String mensagem = cmd.substring(vitima.length() + 4);

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

                                // verifica se usuario nao esta com zombie
                                if (!tmpUser.isZombie())
                                    Message.tellUsersInChan(chanID, "<font color=#800080> * " + tmpUser.getNick() + " " + mensagem + "</font>", false);

                            } else {

                                // avisa que mensagem nao pode ser enviada
                                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_034", "") + "</font>", 0);

                            }

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

                        // verifica se usuario nao esta com zombie
                        if (!tmpUser.isZombie()) {

                            // envia mensagem para meu pvt
                            Message.tellUser(tmpUser, tmpUID.getNick(), "<font color=#800080> * " + tmpUser.getNick() + " " + mensagem + "</font>", 0);

                            // envia mensagem para pvt da vitima
                            Message.tellUser(tmpUID, tmpUser.getNick(), "<font color=#800080> * " + tmpUser.getNick() + " " + mensagem + "</font>", 0);

                        }

                        // modo de seguran�a se estiver ativado
                        if (!tmpUser.getLogadoArray().isEmpty()) {

                            for (Object tmpU : tmpUser.getLogadoArray()) {

                                User tmpU2 = (User) tmpU;

                                // se user for admin
                                if (tmpU2.isAdmin())
                                    Message.tellUser(tmpU2, "&Log&", "<font color=blue>&lt;" + tmpUser.getNick() + "&gt;</font> &lt;" + vitima + "&gt; " + mensagem, 0);

                            }

                        }

                    } else {

                        // avisa que usuario nao existe
                        Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                    }

                }

            } else {

                // avisa que sintaxe est� errada
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/me #America bla bla bla<br>");
                exemplo.append("/me Maria bla bla bla<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Me.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
