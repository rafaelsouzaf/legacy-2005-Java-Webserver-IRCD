package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Mode implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() == 2) {

                // mode list #Brasil
                // mode clear #Brasil

                String tipo = st.nextToken();
                String sala = st.nextToken();

                // se for para listar bans da sala
                if (tipo.equalsIgnoreCase("LIST")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh HOST/OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                // buffer de saida
                                StringBuffer saida = new StringBuffer("");

                                // pega lista de bans
                                for (String separa : tmpChan.getBanList())
                                    saida.append("-> " + separa + "<br>");

                                // imprime lista de bans
                                Message.noticeSvs(tmpUser, Lang.traduz(tmpUser, "webchat_070", ""), saida.toString());

                            } else {

                                // avisa que nao tem status suficiente
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else if (tipo.equalsIgnoreCase("CLEAR")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh HOST/OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                // limpa ban da sala e avisa todos usuarios
                                tmpChan.clearBans();
                                Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_071", tmpUser.getNick()) + "</font>", false);

                            } else {

                                // avisa que nao tem status suficiente
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else {

                    //

                }

            } else if (st.countTokens() == 3) {

                // mode set moderated #Brasil
                // mode set secret #Brasil

                String tipoSet = st.nextToken();
                String tipo = st.nextToken();
                String sala = st.nextToken();

                // se for para adicionar moderado ao canal
                if (tipo.equalsIgnoreCase("MODERATED")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) == 3) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                if (tipoSet.equalsIgnoreCase("SET")) {

                                    // se sala ainda nao for moderada
                                    if (!tmpChan.isModerado()) {

                                        // seta moderado e avisa todos
                                        tmpChan.setModerado(true);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_008", tmpUser.getNick() + "%%" + tipo) + "</font>", false);

                                    } else {

                                        // sala ja esta com modo moderado
                                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_053", tmpChan.getName() + "%%" + tipo) + "</font>", 0);

                                    }

                                } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                    // se sala estiver moderada
                                    if (tmpChan.isModerado()) {

                                        // remove moderado e avisa todos
                                        tmpChan.setModerado(false);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_009", tmpUser.getNick() + "%%" + tipo) + "</font>", false);

                                    } else {

                                        // sala nao esta com modo moderado
                                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_054", tmpChan.getName() + "%%" + tipo) + "</font>", 0);

                                    }

                                } else {

                                    // comando inexistente, exemplo correto �
                                    StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                    exemplo.append("/mode set moderated #channel<br>");
                                    exemplo.append("/mode set secret #channeltopic #channel<br>");
                                    exemplo.append("/mode unset moderated #channel<br>");
                                    exemplo.append("/mode unset secret #channel<br>");
                                    Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

                                }

                            } else {

                                // precisa ser owner da sala
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else if (tipo.equalsIgnoreCase("SECRET")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) == 3) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                if (tipoSet.equalsIgnoreCase("SET")) {

                                    // se sala ainda nao for secreta
                                    if (!tmpChan.isSecret()) {

                                        // seta secreto e avisa todos
                                        tmpChan.setSecret(true);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_008", tmpUser.getNick() + "%%" + tipo) + "</font>", false);

                                    } else {

                                        // sala ja esta com modo secreto
                                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_053", tmpChan.getName() + "%%" + tipo) + "</font>", 0);

                                    }

                                } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                    // se sala estiver secreta
                                    if (tmpChan.isSecret()) {

                                        // remove secreto e avisa todos
                                        tmpChan.setSecret(false);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_009", tmpUser.getNick() + "%%" + tipo) + "</font>", false);

                                    } else {

                                        // sala nao esta com modo secreto
                                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_054", tmpChan.getName() + "%%" + tipo) + "</font>", 0);

                                    }

                                } else {

                                    // comando inexistente, exemplo correto �
                                    StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                    exemplo.append("/mode set moderated #channel<br>");
                                    exemplo.append("/mode set secret #channeltopic #channel<br>");
                                    exemplo.append("/mode unset moderated #channel<br>");
                                    exemplo.append("/mode unset secret #channel<br>");
                                    Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

                                }

                            } else {

                                // precisa ser owner da sala
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else {

                    // comando inexistente, exemplo correto �
                    StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                    exemplo.append("/mode set moderated #channel<br>");
                    exemplo.append("/mode set secret #channeltopic #channel<br>");
                    exemplo.append("/mode unset moderated #channel<br>");
                    exemplo.append("/mode unset secret #channel<br>");
                    Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

                }

            } else if (st.countTokens() == 4) {

                // mode set voice #brasil nick
                // mode set host #brasil nick
                // mode set owner #brasil nick
                // mode set ban #brasil nick

                String tipoSet = st.nextToken();
                String tipo = st.nextToken();
                String sala = st.nextToken();
                String vitima = st.nextToken();

                // se for para adicionar voice a um usuario
                if (tipo.equalsIgnoreCase("VOICE")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh HOST/OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                // se vitima existir
                                User tmpVitima = Server.getUser(vitima);
                                if (tmpVitima != null) {

                                    if (tipoSet.equalsIgnoreCase("SET")) {

                                        // d� voice para vitima
                                        tmpChan.setMemberStatus(tmpVitima, 1);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_051", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                        // remove voice da vitima
                                        tmpChan.setMemberStatus(tmpVitima, 0);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_052", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    }

                                } else {

                                    // vitima nao existe
                                    Message.tellUser(tmpUser, "Atual", "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                                }

                            } else {

                                // precisa ser owner ou host da sala
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else if (tipo.equalsIgnoreCase("HOST")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh HOST/OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                // se vitima existir
                                User tmpVitima = Server.getUser(vitima);
                                if (tmpVitima != null) {

                                    if (tipoSet.equalsIgnoreCase("SET")) {

                                        // d� HOST para vitima
                                        tmpChan.setMemberStatus(tmpVitima, 2);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_051", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                        // remove HOST da vitima
                                        tmpChan.setMemberStatus(tmpVitima, 0);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_052", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    }

                                } else {

                                    // vitima nao existe
                                    Message.tellUser(tmpUser, "Atual", "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                                }

                            } else {

                                // precisa ser owner ou host da sala
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else if (tipo.equalsIgnoreCase("OWNER")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh membro
                        if (tmpChan.isMember(tmpUser)) {

                            // verifica se usuario eh OWNER ou for IRCop/Admin
                            if ((tmpChan.getMemberStatus(tmpUser) == 3) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                // se vitima existir
                                User tmpVitima = Server.getUser(vitima);
                                if (tmpVitima != null) {

                                    if (tipoSet.equalsIgnoreCase("SET")) {

                                        // d� OWNER para vitima
                                        tmpChan.setMemberStatus(tmpVitima, 3);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_051", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                        // remove OWNER da vitima
                                        tmpChan.setMemberStatus(tmpVitima, 0);
                                        Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_052", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                    }

                                } else {

                                    // vitima nao existe
                                    Message.tellUser(tmpUser, "Atual", "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                                }

                            } else {

                                // precisa ser owner ou host da sala
                                Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                            }

                        } else {

                            // precisa estar presente na sala
                            Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_056", tmpChan.getName()) + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else if (tipo.equalsIgnoreCase("BAN")) {

                    // pega instancia da sala
                    Channel tmpChan = Server.getChannel(sala);
                    if (tmpChan != null) {

                        // verifica se usuario eh HOST/OWNER ou for IRCop/Admin
                        if ((tmpChan.getMemberStatus(tmpUser) >= 2) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                            if (tipoSet.equalsIgnoreCase("SET")) {

                                // se vitima ainda nao estiver no ban
                                if (!tmpChan.isBan(vitima)) {

                                    // ban
                                    tmpChan.setBan(vitima);
                                    Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_051", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                } else {

                                    // avisa que vitima ja esta banida
                                    Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_058", vitima) + "</font>", 0);

                                }

                            } else if (tipoSet.equalsIgnoreCase("UNSET")) {

                                // se vitima estiver banida
                                if (tmpChan.isBan(vitima)) {

                                    // remove ban
                                    tmpChan.removeBan(vitima);
                                    Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_052", tmpUser.getNick() + "%%" + tipo.toLowerCase() + "%%" + vitima) + "</font>", true);

                                } else {

                                    // avisa que vitima NAO ESTA na lista de ban
                                    Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_059", vitima) + "</font>", 0);

                                }

                            }

                        } else {

                            // precisa ser owner ou host da sala
                            Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_055", "") + "</font>", 0);

                        }

                    } else {

                        // sala nao existe
                        Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_029", "") + "</font>", 0);

                    }

                } else {

                    // comando inexistente, exemplo correto �
                    StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                    exemplo.append("/mode set voice #channel Maria<br>");
                    exemplo.append("/mode set host #channel Maria<br>");
                    exemplo.append("/mode set owner #channel Maria<br>");
                    exemplo.append("/mode set ban #channel 123.X.0.X<br>");
                    exemplo.append("/mode set ban #channel Maria<br>");
                    exemplo.append("/mode unset voice/host/owner #channel Maria");
                    Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

                }

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/mode set moderated #channel<br>");
                exemplo.append("/mode unset secret #channel<br>");
                exemplo.append("/mode set voice #channel Maria<br>");
                exemplo.append("/mode set host #channel Maria<br>");
                exemplo.append("/mode set owner #channel Maria<br>");
                exemplo.append("/mode unset host #channel Maria<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Mode.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
