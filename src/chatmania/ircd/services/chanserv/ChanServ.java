package chatmania.ircd.services.chanserv;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.log.LogOpers;
import chatmania.extra.mail.Mail;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.Commands;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import chatmania.ircd.commands.Mode;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class ChanServ extends Commands implements CommandsI {

    private static final String svs = "ChanServ";

    public static void nJoin(User tmpUser, Channel tmpChan) {

        // envia mensagem de onjoin se existir alguma
        enviaOnjoin(tmpUser, tmpChan);

        if (tmpUser.isIdentify()) {

            try {

                // se for fundador ou owner
                if (SQLQuery.isFounder(tmpChan.getName(), tmpUser.getNick()) || SQLQuery.isOwner(tmpChan.getName(), tmpUser.getNick())) {

                    // atualiza o campo ultima da tabela
                    SQLQuery.setChannelLast(tmpChan.getName());

                    // d� status de owner
                    tmpChan.setMemberStatus(tmpUser, 3);

                    // promove a owner
                    new Mode().execute(tmpUser, "MODE SET OWNER " + tmpChan.getName() + " " + tmpUser.getNick() + "");

                    // se for host
                } else if (SQLQuery.isHost(tmpChan.getName(), tmpUser.getNick())) {

                    // d� status de host
                    tmpChan.setMemberStatus(tmpUser, 2);

                    new Mode().execute(tmpUser, "MODE SET HOST " + tmpChan.getName() + " " + tmpUser.getNick() + "");

                }

            } catch (Exception e) {

                new LogIRCd("ChanServ.java", "nJoin()", "" + e);

            }

        }

    }

    // verifica quando foi o ultimo acesso de algum owner
    // e desregistra canal se faz mais de tantos dias
    public static void expire() {

        SQLQuery.channelExpire();

    }

    private static void enviaOnjoin(User tmpUser, Channel tmpChan) {

        for (Object separa : tmpChan.listOnjoin()) {

            String frase = (String) separa;
            if (!frase.equals(""))
                Message.tellUser(tmpUser, tmpChan.getName(), frase, 0);

        }

    }

    private static boolean registraCanal(String canal, User tmpUser, String senha) {

        try {

            // registra no banco de dados
            SQLQuery.registerChannel(canal, tmpUser.getNick(), senha);

            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_038", canal + "%%" + tmpUser.getNick() + "%%" + senha));

            return true;

        } catch (Exception e) {

            new LogIRCd("ChanServ.java", "registraCanal()", "" + e + "");
            Message.noticeSvs(tmpUser, svs, "" + e + "");

        }

        return false;

    }

    private static int getStatusOP(String canal, String nick) {

        if ((SQLQuery.isFounder(canal, nick)) || (SQLQuery.isOwner(canal, nick)))
            return 3;
        else if (SQLQuery.isHost(canal, nick))
            return 2;
        else
            return 0;

    }

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");

            if (st.countTokens() > 1) {

                String com1 = st.nextToken();
                com1 = st.nextToken().toUpperCase().trim();

                String usuario = tmpUser.getNick();
                boolean identificado = tmpUser.isIdentify();

                if (com1.equals("HELP")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_001", "" + ChanServConfig.EXPIRE));

                    } else if (st.countTokens() == 1) {

                        String parte1 = st.nextToken().toUpperCase();

                        if (parte1.equals("REGISTER")) {

                            expire();
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_002", "" + ChanServConfig.EXPIRE));

                        } else if (parte1.equals("ONJOIN")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_003", ""));

                        } else if (parte1.equals("DROP")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_004", ""));

                        } else if (parte1.equals("LIST")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_005", ""));

                        } else if (parte1.equals("ADD")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_006", ""));

                        } else if (parte1.equals("DEL")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_007", ""));

                        } else if (parte1.equals("SENDPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_008", ""));

                        } else if (parte1.equals("SUCESSOR")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_012", ""));

                        } else if (parte1.equals("INFO")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_010", ""));

                        } else if (parte1.equals("SETPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_011", ""));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_031", ""));

                        }

                    }

                } else if (com1.equals("INFO")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_009", "" + SQLQuery.countChannels()));

                    } else if (st.countTokens() == 1) {

                        infoFor(st.nextToken(), tmpUser);

                    }

                } else if (com1.equals("LIST")) {

                    if (st.countTokens() == 1) {

                        String sala = st.nextToken();

                        // se sala estiver registrada, lista ops
                        if (SQLQuery.isChannelRegistered(sala)) {

                            listaOPs(sala, tmpUser);

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_025", sala));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv list #channel<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SENDPASS")) {

                    if (st.countTokens() == 1) {

                        String canal = st.nextToken();
                        if (SQLQuery.isChannelRegistered(canal)) {

                            if ((identificado) && (SQLQuery.isFounder(canal, usuario))) {

                                sendPass(tmpUser, canal);

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_041", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_025", canal));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv sendpass #channel<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SUCESSOR")) {

                    if (st.countTokens() == 1) {

                        String canal = st.nextToken();

                        if (SQLQuery.isChannelRegistered(canal)) {

                            // se estiver identificado e for o fundador
                            if ((identificado) && (SQLQuery.isFounder(canal, usuario))) {

                                // update sucessor
                                SQLQuery.setSucessor(canal, "");
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_055", canal));

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_041", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_025", canal));

                        }

                    } else if (st.countTokens() == 2) {

                        String canal = st.nextToken();
                        String sucessor = st.nextToken();

                        if (SQLQuery.isChannelRegistered(canal)) {

                            // se estiver identificado e for o fundador
                            if ((identificado) && (SQLQuery.isFounder(canal, usuario))) {

                                // se o apelido do sucessor for diferente do fundador
                                if (!usuario.equalsIgnoreCase(sucessor)) {

                                    // se o sucessor estiver registrado
                                    if (SQLQuery.isRegistered(sucessor)) {

                                        // update sucessor
                                        SQLQuery.setSucessor(canal, sucessor);
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_051", sucessor + "%%" + canal));

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_015", sucessor));

                                    }

                                } else {

                                    // avisa que apelido de sucessor e fundador nao podem ser iguais
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_053", ""));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_041", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_025", canal));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv sucessor #channel John<br>");
                        exemplo.append("/chanserv sucessor #channel<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SETPASS")) {

                    if (st.countTokens() == 2) {

                        if (identificado) {

                            String sala = st.nextToken();
                            String novaSenha = st.nextToken();

                            if (SQLQuery.isFounder(sala, usuario)) {

                                // atualiza senha no banco de dados
                                SQLQuery.setChannelPassword(sala, novaSenha);

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_052", sala + "%%" + novaSenha));
                                new LogOpers(usuario, sala, cmd);

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_041", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv setpass #channel newPassword123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("ADD")) {

                    if (st.countTokens() == 3) {

                        String canal = st.nextToken();
                        String tipo = st.nextToken();
                        String vitima = st.nextToken();

                        if (identificado) {

                            if (canal.charAt(0) == '#') {

                                if (tipo.equalsIgnoreCase("HOST")) {

                                    // se usuario for fundador ou owner da sala
                                    if ((SQLQuery.isFounder(canal, usuario)) || (SQLQuery.isOwner(canal, usuario))) {

                                        // se vitima for apelido registrado
                                        if (SQLQuery.isRegistered(vitima)) {

                                            // se canal ainda nao tiver alcan�ado o numero maximo de hosts
                                            // permitidos
                                            if (SQLQuery.isHostLimitOk(canal)) {

                                                // se vitima nao for fundador, nem owner nem host ainda
                                                if ((!SQLQuery.isFounder(canal, vitima)) && (!SQLQuery.isHost(canal, vitima)) && (!SQLQuery.isOwner(canal, vitima))) {

                                                    SQLQuery.setHost(canal, vitima);
                                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_013", vitima + "%%" + canal));

                                                } else {

                                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_014", vitima + "%%" + canal));

                                                }

                                            } else {

                                                // avisa que sala j� tem o numero maximo de hosts permitido
                                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_056", canal + "%%" + ChanServConfig.LIMITHOST));

                                            }

                                        } else {

                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_015", vitima));

                                        }

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                                    }

                                } else if (tipo.equalsIgnoreCase("OWNER")) {

                                    // se usuario for founder ou owner
                                    if ((SQLQuery.isFounder(canal, usuario)) || (SQLQuery.isOwner(canal, usuario))) {

                                        // se vitima tiver com apelido registrado
                                        if (SQLQuery.isRegistered(vitima)) {

                                            // se canal ainda nao tiver alcan�ado o numero maximo de owners
                                            // permitidos
                                            if (SQLQuery.isOwnerLimitOk(canal)) {

                                                // se vitima ainda nao for founder, host ou owner
                                                if ((!SQLQuery.isFounder(canal, vitima)) && (!SQLQuery.isHost(canal, vitima)) && (!SQLQuery.isOwner(canal, vitima))) {

                                                    SQLQuery.setOwner(canal, vitima);
                                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_017", vitima + "%%" + canal));

                                                } else {

                                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_014", vitima + "%%" + canal));

                                                }

                                            } else {

                                                // avisa que sala j� tem o numero maximo de owners permitido
                                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_057", canal + "%%" + ChanServConfig.LIMITOWNER));

                                            }

                                        } else {

                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_015", vitima));

                                        }

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                                    }

                                }

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv add #channel HOST john<br>");
                        exemplo.append("/chanserv add #channel OWNER john<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("DEL")) {

                    if (st.countTokens() == 2) {

                        String canal = st.nextToken();
                        String vitima = st.nextToken();

                        if (identificado) {

                            if ((SQLQuery.isFounder(canal, usuario)) || (SQLQuery.isOwner(canal, usuario))) {

                                if ((SQLQuery.isHost(canal, vitima)) || (SQLQuery.isOwner(canal, vitima))) {

                                    deletaOP(canal, vitima);
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_019", vitima));

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_020", vitima + "%%" + canal));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv del #channel john<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("DROP")) {

                    if (st.countTokens() == 2) {

                        String canal = st.nextToken();
                        String senha = st.nextToken();

                        if (identificado) {

                            if (SQLQuery.isFounder(canal, usuario)) {

                                if (SQLQuery.isChannelCorrect(canal, senha))
                                    desregistraCanal(canal, tmpUser);
                                else
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_021", ""));

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv drop #channel myPassword123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("REGISTER")) {

                    if (st.countTokens() == 2) {

                        String canal = st.nextToken();
                        String senha = st.nextToken();

                        if (identificado) {

                            // nick possui mais de 5 canais registrados?
                            if ((ehFundadorDeMaisDeLimite(tmpUser)) || (tmpUser.isIRCop() || tmpUser.isAdmin())) {

                                //verifica se canal existe
                                Channel tmpChannel = Server.getChannel(canal);
                                if (tmpChannel != null) {

                                    // verifica se usuario est� no canal
                                    if (tmpChannel.isMember(tmpUser)) {

                                        // se sala nao for registrada
                                        if (!SQLQuery.isChannelRegistered(canal)) {

                                            // se usuario foi o fundador da sala
                                            if (tmpChannel.getFundador() == tmpUser) {

                                                // execute metodo registraCanal
                                                if (registraCanal(canal, tmpUser, senha)) {

                                                    // define topico divulgando o webchat
                                                    super.invoca(tmpUser, Lang.traduz(tmpUser, "chanserv_023", canal + "%%" + Config.URL + "%%" + canal.substring(1)));

                                                }

                                            } else {

                                                // erro, apenas o primeiro usuario a entrar na sala pode registra-la
                                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_026", ""));

                                            }

                                        } else {

                                            // canal j� est� registrado
                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_032", canal));

                                        }

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_024", ""));

                                    }

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_024", ""));

                                }

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv register #channel password123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("ONJOIN")) {

                    if (st.countTokens() == 2) {

                        String comando = st.nextToken();
                        String canal = st.nextToken();

                        // se comando for para limpar onjoin
                        if (comando.equalsIgnoreCase("CLEAR")) {

                            if (identificado) {

                                // se canal existir
                                Channel tmpChan = Server.getChannel(canal);
                                if (tmpChan != null) {

                                    // se for fundador ou owner registrado
                                    if ((SQLQuery.isFounder(canal, usuario)) || (SQLQuery.isOwner(canal, usuario))) {

                                        // limpa onjoin
                                        tmpChan.clearOnjoin();

                                        // avisa usuario
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_048", tmpChan.getName()));

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                                    }

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_049", canal));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/chanserv onjoin [#channel] [line] [message]<br>");
                            exemplo.append("/chanserv onjoin #channel 1 message blah blah blah<br>");
                            exemplo.append("/chanserv onjoin #channel 2 message blah blah blah<br>");
                            exemplo.append("/chanserv onjoin clear #channel<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else if (st.countTokens() >= 3) {

                        String canal = st.nextToken();
                        String numero = st.nextToken();

                        // se estiver identificado
                        if (identificado) {

                            // se canal existir
                            Channel tmpChan = Server.getChannel(canal);
                            if (tmpChan != null) {

                                // se usuario for fundador ou owner registrado ou temporario
                                if ((SQLQuery.isFounder(canal, usuario)) || (SQLQuery.isOwner(canal, usuario))) {

                                    // converte numero para int
                                    try {

                                        // converte para numero
                                        int linha = Integer.parseInt(numero);

                                        // se o numero da linha for entre 1 e 7
                                        if ((linha > 0) && (linha < 8)) {

                                            // pega frase
                                            String frase = cmd.substring(cmd.indexOf(canal) + canal.length() + 3);

                                            // atualiza a linha do onjoin que o usuario deseja
                                            tmpChan.addOnjoin(linha, frase);

                                            // avisa usuario que onjoin foi atualizado
                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_033", canal + "%%" + linha));

                                        } else {

                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_034", ""));

                                        }

                                    } catch (NumberFormatException e) {

                                        // comando inexistente, exemplo correto �
                                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                        exemplo.append("/chanserv onjoin [#channel] [line] [message]<br>");
                                        exemplo.append("/chanserv onjoin #channel 1 message blah blah blah<br>");
                                        exemplo.append("/chanserv onjoin #channel 2 message blah blah blah<br>");
                                        exemplo.append("/chanserv onjoin clear #channel<br>");
                                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                                    }

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_016", ""));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_049", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_018", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/chanserv onjoin [#channel] [line] [message]<br>");
                        exemplo.append("/chanserv onjoin #channel 1 message blah blah blah<br>");
                        exemplo.append("/chanserv onjoin #channel 2 message blah blah blah<br>");
                        exemplo.append("/chanserv onjoin clear #channel<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_031", ""));

                }

            }

        } catch (Exception e) {

            new LogIRCd("ChanServ.java", "execute() => " + cmd, "" + e);

        }

    }

    private boolean ehFundadorDeMaisDeLimite(User tmpUser) {

        int numCanais = SQLQuery.getIntChannelToMore(tmpUser.getNick());
        String salas = SQLQuery.getStringChannelToMore(tmpUser.getNick());

        if (numCanais < ChanServConfig.LIMIT) {

            return true;

        } else {

            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_027", "" + ChanServConfig.LIMIT));
            Message.noticeSvs(tmpUser, svs, "=>" + salas);
            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_028", ""));
            return false;

        }

    }

    private void deletaOP(String canal, String nick) {

        // verifica se owner eh cadastrado
        if (SQLQuery.isOwner(canal, nick)) {

            // remove owner
            SQLQuery.removeOwner(canal, nick);

        }

        // verifica se host eh cadastrado
        if (SQLQuery.isHost(canal, nick)) {

            // remove owner
            SQLQuery.removeHost(canal, nick);

        }

    }

    private void listaOPs(String canal, User tmpUser) {

        // pega fundador
        String founder = SQLQuery.getFounder(canal);

        // pega owners
        ArrayList owners = SQLQuery.getOwners(canal);

        // pega hosts
        ArrayList hosts = SQLQuery.getHosts(canal);

        // buffer de saida
        StringBuffer saida = new StringBuffer();

        // imprime o founder
        saida.append(Lang.traduz(tmpUser, "chanserv_029", canal) + "<br>");
        saida.append("-> " + founder + "<br>");

        // e os owners
        if (!owners.isEmpty()) {

            for (Object separa : owners)
                saida.append("-> " + separa.toString() + "<br>");

        }

        // imprime os hosts
        if (!hosts.isEmpty()) {

            saida.append(Lang.traduz(tmpUser, "chanserv_030", canal) + "<br>");

            for (Object separa : hosts)
                saida.append("-> " + separa.toString() + "<br>");

        }

        Message.noticeSvs(tmpUser, svs, saida.toString());

    }

    private void infoFor(String chan, User tmpUser) {

        // pega informa�oes do canal
        Map info = SQLQuery.getChannelInfoFor(chan);

        // se houver alguma informa�ao
        if (!info.isEmpty()) {

            // buffer de saida
            StringBuffer saida = new StringBuffer();

            String sucessor = (String) info.get("cs_sucessor");
            if (sucessor == null)
                sucessor = "";

            saida.append(Lang.traduz(tmpUser, "chanserv_035", (String) info.get("cs_channel")) + "<br>");
            saida.append(Lang.traduz(tmpUser, "chanserv_036", (String) info.get("cs_founder")) + "<br>");
            saida.append(Lang.traduz(tmpUser, "chanserv_054", sucessor) + "<br>");
            saida.append(Lang.traduz(tmpUser, "chanserv_037", (String) info.get("cs_date")) + "<br>");
            Message.noticeSvs(tmpUser, svs, saida.toString());
            listaOPs(chan, tmpUser);

        } else {

            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_025", chan));

        }

    }

    private void desregistraCanal(String canal, User tmpUser) {

        // deleta do banco de dados
        SQLQuery.removeChannel(canal);

        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_040", canal));
        super.invoca(tmpUser, "MODE " + canal + " -q " + tmpUser.getNick() + "");

    }

    private boolean sendPass(User tmpUser, String canal) {

        try {

            // pega dados da sala
            Map dados = SQLQuery.getChannelInfoFor(canal);

            // se dados existirem
            if (!dados.isEmpty()) {

                String founder = dados.get("cs_founder").toString();
                String password = dados.get("cs_password").toString();

                // pega email do fundador do canal
                String email = SQLQuery.getEmail(founder);

                // monta mensagem
                String mensagem = Lang.traduz(tmpUser, "chanserv_047", founder + "%%" + canal + "%%" + canal + "%%" + password + "%%" + Config.NETWORK);

                // envia email
                Mail mail = new Mail(email, Lang.traduz(tmpUser, "chanserv_042", ""), mensagem);
                if (mail.envia()) {

                    // avisa que email foi enviado com sucesso
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_043", canal + "%%" + email));
                    Message.noticeOpers(Lang.traduz(tmpUser, "chanserv_044", tmpUser.getNick() + "%%" + canal));

                    return true;

                } else {

                    // avisa que email nao pode ser enviado
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "chanserv_045", ""));

                }

            }

        } catch (Exception e) {

            new LogIRCd("ChanServ.java", "sendPass()", "" + e + "");

        }

        return false;

    }

}