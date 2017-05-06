package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Kick implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() >= 2) {

                String sala = st.nextToken();
                String vitima = st.nextToken();
                String motivo = "By By";

                if (st.countTokens() >= 1)
                    motivo = cmd.substring(8 + sala.length() + vitima.length());

                Channel tmpChan = Server.getChannel(sala);
                User tmpVitima = Server.getUser(vitima);

                // se sala existir
                if (tmpChan != null) {

                    // se vitima existir
                    if (tmpVitima != null) {

                        // se usuario estiver na sala
                        if (tmpChan.isMember(tmpUser)) {

                            // se vitima estiver na sala
                            if (tmpChan.isMember(tmpVitima)) {

                                // se usuario for OP/Owner ou for Sysop
                                if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                    // remove sala da lista de salas da vitima
                                    tmpVitima.delNewChannel(tmpChan);

                                    // remove usuario do sala
                                    tmpChan.removeUser(tmpVitima);

                                    // avisa para todos da sala que usuario foi expulso
                                    Message.tellUsersInChan(tmpChan, "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_004", tmpUser.getNick() + "%%" + tmpVitima.getNick() + "%%" + motivo) + "</font>", true);

                                    // avisa usuario atraves de alert javascript
                                    Message.tellUserAlternativeWeb(tmpVitima, "<script>alert('" + Lang.traduz(tmpUser, "webchat_003", tmpChan.getName() + "%%" + tmpUser.getNick() + "%%" + motivo) + "');</script>");

                                    // se sala tiver 0 usuarios, entao fecha a sala
                                    // retira sala da cole�ao
                                    if (tmpChan.getMemberSize() == 0)
                                        Server.unsetChannel(tmpChan.getName());

                                } else {

                                    // avisa que usuario nao tem status suficiente
                                    Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                                }

                            } else {

                                // avisa que vitima precisa estar na sala
                                Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_047", tmpVitima.getNick()) + "</font>", 0);

                            }

                        } else {

                            // avisa que usuario precisa estar na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // avisa que vitima nao existe
                        Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                    }

                } else {

                    // avisa que sala nao existe
                    Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                }

            } else {

                // avisa que sintaxe est� errada
                // avisa que sintaxe est� errada
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/kick #America John bye bye<br>");
                exemplo.append("/kick #America John<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Kick.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
