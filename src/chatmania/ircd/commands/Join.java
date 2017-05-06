package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import chatmania.ircd.services.chanserv.ChanServ;

public class Join implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            // inteiro que ira dizer quanto retirar da string, pode ser 3 caracteres /j
            // ou 5 caracteres /join (sempre contando o espa�o)
            int quanto;

            if (cmd.toUpperCase().startsWith("J "))
                quanto = 2;
            else
                quanto = 5;

            // retira nome do comando da string cmd e substitui espa�os por virgula
            String sala = cmd.substring(quanto, cmd.length()).trim().replace(" ", ",");

            if (sala.length() > 0) {

                // se os canais contiverem virgula, entao � para separar
                // e tratar individualmente
                if (sala.contains(",")) {

                    // re-executa com canais separadamente
                    for (String separa : sala.split(",")) {

                        if (!separa.equals(""))
                            execute(tmpUser, "JOIN " + separa);

                    }

                } else {

                    // se numero de canais que usuario esta � menor que o numero maximo permitido
                    // ou usuario for ircop
                    if ((tmpUser.getNumCanais() < Config.MAXCHANNELUSER) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                        // se canal nao tiver # no inicio, coloca)
                        if (sala.charAt(0) != '#')
                            sala = "#" + sala;

                        // se sala tiver mais que 1 caracter (no caso mais que zero caracter)
                        // converte o primeiro caracter do canal para maiuscula
                        // � apenas uma questao estetica
                        if (sala.length() > 0) {

                            String caracter = sala.charAt(1) + "";
                            sala = sala.replaceFirst(caracter, caracter.toUpperCase());

                        }

                        // pega instancia de canal
                        Channel tmpChan = Server.getChannel(sala);

                        // se canal existir
                        if (tmpChan != null) {

                            // se usuario nao esta no canal
                            if (!tmpChan.isMember(tmpUser)) {

                                // se usuario nao esta na lista de ban ou for sysop
                                if ((!tmpChan.isBan(tmpUser.getNick()) && !tmpChan.isBan(tmpUser.getHost())) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                    // se canal nao est� forbidado
                                    if (!SQLQuery.isForbid(sala)) {

                                        // e se o numero de usuarios do canal for maior ou igual ao numero permitido pelo servidor
                                        if (tmpChan.getMemberSize() >= Config.MAXUSERCHANNEL) {

                                            // chama modulo que imprime lista de salas do sql
                                            Message.tellUserAlternativeWeb(tmpUser, "<script>sendUrl('Salas', '?modules=Others&Part=ListRoom')</script>");

                                            // avisa usuario
                                            Message.tellUserAlternativeWeb(tmpUser, "<script>alert('" + Lang.traduz(tmpUser, "webchat_069", tmpChan.getName() + "%%" + Config.MAXUSERCHANNEL) + "');</script>");

                                        } else {

                                            // se usuario tiver nick diferente de nada
                                            if (!tmpUser.getNick().equals("")) {

                                                // adiciona um user normal
                                                tmpChan.addUser(tmpUser, 0);

                                                Message.tellUsersInChan(tmpChan, "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_002", tmpUser.getNick()) + "</font>", true);

                                                // adiciona instancia do canal na lista de canais onde usuario est� presente
                                                tmpUser.incNewChannel(tmpChan);

                                                if (!tmpChan.getTopic().equals(""))
                                                    Message.tellUser(tmpUser, tmpChan.getName(), "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_013", Util.mircHtml(tmpChan.getTopic()) + "%%" + tmpChan.getTopicOwner()) + "</font>", 0);

                                                // avisa chanserv que usuario entrou no canal
                                                ChanServ.nJoin(tmpUser, tmpChan);

                                            }

                                        }

                                    } else {

                                        Message.tellUser(tmpUser, "Atual", "<font color=red>" + Lang.traduz(tmpUser, "webchat_049", sala) + "</font>", 0);

                                    }

                                } else {

                                    Message.tellUser(tmpUser, "Atual", "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_012", tmpChan.getName()) + "</font>", 0);
                                    Message.tellUserAlternativeWeb(tmpUser, "<script>alert('" + Lang.traduz(tmpUser, "webchat_012", tmpChan.getName()) + "')</script>");

                                    // avisa os OPs que usuario banido tentou entrar
                                    Message.tellOpsInChan(tmpChan, "<font color=#FF0000>" + Lang.traduz(tmpUser, "webchat_016", tmpUser.getNick()) + "</font>");

                                }

                            }

                        } else {

                            // se canal for valido, continue
                            // senao avisa que canal � invalido
                            if (Util.isValidCanal(sala)) {

                                // se canal nao estiver na lista de forbids
                                if (!SQLQuery.isForbid(sala)) {

                                    // se usuario tiver nick diferente de nada
                                    if (!tmpUser.getNick().equals("")) {

                                        // cria instancia do canal
                                        Channel can = new Channel(sala, tmpUser);

                                        // adiciona canal a colecao
                                        Server.setChannel(can, can.getName());

                                        // se canal estiver registrado seta modo +r e adiciona usuario normal
                                        // senao adiciona usuario Owner e o seta como fundador
                                        if (SQLQuery.isChannelRegistered(sala)) {

                                            can.addUser(tmpUser, 0);

                                        } else {

                                            // adiciona como fundador
                                            can.addUser(tmpUser, 3);

                                        }

                                        // avisa que entrou na sala
                                        Message.tellUser(tmpUser, can.getName(), "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_002", tmpUser.getNick()) + "</font>", 0);

                                        // adiciona canal na lista de salas onde usuario est�
                                        tmpUser.incNewChannel(can);

                                        // envia lista de users da sala para webchat
                                        Util.pegaUsuarios(tmpUser, can);

                                        // avisa chanserv que usuario entrou no canal
                                        ChanServ.nJoin(tmpUser, can);

                                    }

                                } else {

                                    Message.tellUser(tmpUser, "Atual", "<font color=red>" + Lang.traduz(tmpUser, "webchat_049", sala) + "</font>", 0);

                                }

                            } else {

                                // avisa que caracteres sao ilegais
                                Message.tellUser(tmpUser, "Atual", "<font color=red>" + Lang.traduz(tmpUser, "webchat_066", sala) + "</font>", 0);

                            }

                        }

                    } else {

                        // limite de salas excedido, user j� est� em muitas salas
                        Message.tellUser(tmpUser, "Atual", Lang.traduz(tmpUser, "webchat_033", ""), 0);

                    }

                }

            } else {

                // avisa que sintaxe est� errada
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/join #America, Friends, Help<br>");
                exemplo.append("/join America Friends Help<br>");
                exemplo.append("/join America<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            e.printStackTrace();
            new LogIRCd("Join.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}