package chatmania.ircd.services.nickserv;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.mail.Mail;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.Commands;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import chatmania.ircd.services.operserv.OperServ;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public class NickServ extends Commands implements CommandsI {

    private final static String svs = "NickServ";

    public static void verNickTime(User tmpUser) {

        // verifica se � registrado ou nao
        if (SQLQuery.isRegistered(tmpUser.getNick()))
            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_035", tmpUser.getNick()));
        else
            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_037", tmpUser.getNick()));

    }

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");

            if (st.countTokens() > 1) {

                String com1 = st.nextToken();
                com1 = st.nextToken().toUpperCase().trim();

                String usuario = tmpUser.getNick();
                //int status = tmpUser.getSysop();
                boolean identificado = tmpUser.isIdentify();

                if (com1.equals("HELP")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_001", "" + NickServConfig.EXPIRE));

                    } else if (st.countTokens() == 1) {

                        String parte1 = st.nextToken().toUpperCase();

                        if (parte1.equals("REGISTER")) {

                            SQLQuery.expire();
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_002", "" + NickServConfig.EXPIRE));

                        } else if (parte1.equals("IDENTIFY")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_003", "" + NickServConfig.EXPIRE));

                        } else if (parte1.equals("REGAIN")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_004", ""));

                        } else if (parte1.equals("SETPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_005", ""));

                        } else if (parte1.equals("EMAIL")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_006", ""));

                        } else if (parte1.equals("SENDPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_007", ""));

                        } else if (parte1.equals("DROP")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_008", ""));

                        } else if (parte1.equals("INFO")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_009", ""));

                        } else if (parte1.equals("LANG")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_010", ""));

                        } else if (parte1.equals("AJOIN")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_011", ""));

                        } else if (parte1.equals("LOGOFF")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_015", ""));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_059", ""));

                        }

                    }

                } else if (com1.equals("INFO")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_012", "" + SQLQuery.countNicks()));

                    } else if (st.countTokens() == 1) {

                        infoFor(tmpUser, st.nextToken());

                    }

                } else if (com1.equals("LOGOFF")) {

                    if (st.countTokens() == 0) {

                        // se usuario estiver identificado
                        if (identificado) {

                            // retira identifica�ao do nick
                            tmpUser.setIdentify(false);

                            // avisa usuario que seu apelido nao est� mais identificado
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_070", ""));

                        } else {

                            // avisa que precisa estar com apelido identificado
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_019", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv logoff<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("IDENTIFY")) {

                    if (st.countTokens() == 1) {

                        if (!identificado) {

                            String senha = st.nextToken();

                            // se apelido e senha baterem
                            if (SQLQuery.isNickCorrect(usuario, senha)) {

                                // atualiza tempo de identificacao
                                SQLQuery.setLast(usuario);

                                // avisa usuario que agora esta identificado e registrado
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_060", ""));

                                // identifica
                                tmpUser.setIdentify(true);

                                // verifica se user tem status de ircop
                                OperServ.getIRCopOrAdmin(tmpUser);

                                // for�a entrada nos canais
                                ajoinNow(tmpUser);

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_016", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_017", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv identify passWord123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("EMAIL")) {

                    if (st.countTokens() == 1) {

                        String email = st.nextToken();

                        if (identificado) {

                            SQLQuery.setEmail(usuario, email);
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_020", email));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_019", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv email email@email.com<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SENDPASS")) {

                    if (st.countTokens() == 1) {

                        String vitima = st.nextToken();

                        if (sendPass(tmpUser, vitima)) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_041", vitima));
                            Message.noticeOpers(Lang.traduz(tmpUser, "nickserv_042", tmpUser.getNick() + "%%" + vitima));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_043", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv sendpass Maria<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("DROP")) {

                    if (st.countTokens() == 1) {

                        String senha = st.nextToken();

                        // se usuario estiver identificado
                        if (identificado) {

                            // verifica se apelido e senha batem
                            if (SQLQuery.isNickCorrect(usuario, senha)) {

                                // dropa nick
                                SQLQuery.removeNick(usuario);

                                // avisa que apelido foi dropado
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_021", usuario));

                                // muda nick do usuario para um generico
                                super.invoca(tmpUser, "NICK Chat" + (int) (Math.random() * 9999) + "");

                                // chama o metodo passaCanalAdiante, que ir� verificar se usuario eh owner
                                // de algum canal, se for passar� o canal para o sucessor (se existir algum)
                                passaCanalAdiante(tmpUser, usuario);

                            } else {

                                // avisa usuario que dados estao incorretos
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_016", ""));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_019", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv drop passWord123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SETPASS")) {

                    if (st.countTokens() == 1) {

                        String senha = st.nextToken();

                        if (identificado) {

                            SQLQuery.setPassword(usuario, senha);
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_018", senha));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_019", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv setpass newPassword123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("REGAIN")) {

                    if (st.countTokens() == 2) {

                        String nick = st.nextToken();
                        String senha = st.nextToken();

                        // se usuario e senha batem
                        if (SQLQuery.isNickCorrect(nick, senha)) {

                            // se nick nao estiver forbidado
                            if (!SQLQuery.isForbid(nick)) {

                                // pega instancia do usuario improprio
                                User droga = Server.getUser(nick);

                                // booleano que ira ajudar a ver se eh possivel realizar mudan�a de apelido
                                boolean bloqueia = false;

                                // se usuario existir
                                if (droga != null) {

                                    // se usuario nao for sysop, kila, senao bloqueia
                                    // mudan�a de apelido porque nao � possivel que o comando
                                    // nickserv regain retire apelido de um ircop
                                    if (droga.isIRCop() || droga.isAdmin())
                                        Util.releaseUserID(droga, Lang.traduz(tmpUser, "nickserv_028", ""));
                                    else
                                        bloqueia = true;

                                }

                                if (!bloqueia) {

                                    // atualiza tempo de identificacao
                                    SQLQuery.setLast(nick);

                                    // muda nick do usuario que acabou de recuperar o nick
                                    Util.changeNick(tmpUser, nick);

                                    // identifica
                                    tmpUser.setIdentify(true);

                                    // avisa usuario que ele ja esta identificado
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_029", nick));

                                    // verifica se user tem status de ircop
                                    OperServ.getIRCopOrAdmin(tmpUser);

                                    // for�a entrada nos canais
                                    ajoinNow(tmpUser);

                                } else {

                                    // avisa que regain nao pode ser usado porque apelido � ircop
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_068", nick));

                                }

                            } else {

                                // avisa que apelido � forbidado
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "webchat_049", nick));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_016", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv regain John Password123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("REGISTER")) {

                    if (st.countTokens() == 1) {

                        String email = st.nextToken();

                        // se email tiver uma validade minima
                        if (Util.isValidEmail(email)) {

                            // se email ainda nao existir no banco de dados
                            if (!SQLQuery.isExistEmail(email)) {

                                // se nick ainda nao estiver registrado, registra
                                if (!SQLQuery.isRegistered(usuario)) {

                                    // registra nick
                                    String senha = SQLQuery.registerNick(usuario, email);

                                    // avisa usuario que apelido foi registrado
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_032", usuario + "%%" + senha + "%%" + email));

                                    // se email for enviado com sucesso
                                    if (sendPassRegister(tmpUser, usuario)) {

                                        // avisa que senha foi enviada
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_033", email));

                                    } else {

                                        // avisa usuario que senha nao pode ser enviada
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_043", ""));

                                    }

                                } else {

                                    // apelido ja esta registrado
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_031", usuario));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_057", email));

                            }

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_034", email));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/nickserv register email@email.com<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("AJOIN")) {

                    if (identificado) {

                        // verifica se countTOkens � igual a 1, se for
                        // entao � porque esta chamando os comandos LIST ou NOW
                        if (st.countTokens() == 1) {

                            String opcao = st.nextToken();
                            if (opcao.equalsIgnoreCase("LIST")) {

                                // pega canais do ajoin
                                ArrayList ajoin = SQLQuery.getAjoin(usuario);

                                // se lista nao estiver vazia
                                if (!ajoin.isEmpty()) {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_025", ""));

                                    for (Object separa : ajoin)
                                        Message.noticeSvs(tmpUser, svs, "-> " + separa);

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_026", ""));

                                }

                            } else if (opcao.equalsIgnoreCase("NOW")) {

                                // for�a entrada nos canais
                                ajoinNow(tmpUser);

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/nickserv ajoin list<br>");
                                exemplo.append("/nickserv ajoin now<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                            // se for igual a 2 � porque chama as opcoes
                            // add ou del
                        } else if (st.countTokens() == 2) {

                            String opcao = st.nextToken();
                            String sala = st.nextToken();

                            if (opcao.equalsIgnoreCase("ADD")) {

                                // se canal come�ar com #, continue
                                // senao avisa que s� pode canais que come�em com #
                                if (sala.charAt(0) == '#') {

                                    // se numero de canais j� cadastrados for menor
                                    // que o n�mero m�ximo de canais permitido no ajoin por usuario
                                    if (SQLQuery.isAjoinLimitOk(usuario)) {

                                        // se canal nao estiver cadastrado, cadastra
                                        // senao avisa que canal ja esta cadastrado
                                        if (!SQLQuery.isAjoin(usuario, sala)) {

                                            // cadastra sala no ajoin do usuario
                                            SQLQuery.setAjoin(usuario, sala);
                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_055", sala));

                                        } else {

                                            // avisa que canal ja esta cadastrado
                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_046", sala));

                                        }

                                    } else {

                                        // avisa que usuario ja tem numero maximo de canais permitidos no ajoin
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_069", NickServConfig.AJOINLIMIT + ""));

                                    }

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_023", ""));

                                }

                            } else if (opcao.equalsIgnoreCase("DEL")) {

                                // se canal estiver cadastrado, DEScadastra
                                // senao avisa que canal NAO esta cadastrado
                                if (SQLQuery.isAjoin(usuario, sala)) {

                                    // remove canal do ajoin no banco de dados
                                    SQLQuery.removeAjoin(usuario, sala);
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_045", sala));

                                } else {

                                    // avisa que canal NAO esta cadastrado
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_027", sala));

                                }

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/nickserv ajoin add #channel<br>");
                                exemplo.append("/nickserv ajoin del #channel<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                        }

                    } else {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_019", ""));

                    }

                } else {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_059", ""));

                }

            }

        } catch (Exception e) {

            new LogIRCd("NickServ.java", "execute(" + cmd + ")", e + "");

        }

    }

    public void ajoinNow(User tmpUser) {

        // pega canais do ajoin
        ArrayList ajoin = SQLQuery.getAjoin(tmpUser.getNick());

        // se lista nao estiver vazia
        if (!ajoin.isEmpty()) {

            for (Object separa : ajoin)
                super.invoca(tmpUser, "JOIN " + separa);

        }

    }

    public boolean sendPassRegister(User tmpUser, String vitima) {

        // se usuario estiver registrado
        if (SQLQuery.isRegistered(vitima)) {

            // pega informacoes do usuario
            Map info = SQLQuery.getInfoFor(vitima);

            // senha descriptografada
            String senha = info.get("ns_password").toString();

            // monta mensagem
            String mensagem = Lang.traduz(tmpUser, "nickserv_014", vitima + "%%" + vitima + "%%" + vitima + "%%" + senha + "%%" + vitima + "%%" + senha + "%%" + Config.NETWORK);

            // cria instancia de email
            Mail mail = new Mail(info.get("ns_email").toString(), Lang.traduz(tmpUser, "nickserv_044", ""), mensagem);

            // se email for enviado, entao retorna true
            if (mail.envia())
                return true;

        }

        return false;

    }

    public boolean sendPass(User tmpUser, String vitima) {

        // se usuario estiver registrado
        if (SQLQuery.isRegistered(vitima)) {

            // pega informacoes do usuario
            Map info = SQLQuery.getInfoFor(vitima);

            // senha descriptografada
            String senha = info.get("ns_password").toString();

            // monta mensagem
            String mensagem = Lang.traduz(tmpUser, "nickserv_013", vitima + "%%" + vitima + "%%" + senha + "%%" + Config.NETWORK);

            // cria instancia de email
            Mail mail = new Mail(info.get("ns_email").toString(), Lang.traduz(tmpUser, "nickserv_040", ""), mensagem);

            // se email for enviado, entao retorna true
            if (mail.envia())
                return true;

        }

        return false;

    }

    public void infoFor(User tmpUser, String nick) {

        try {

            // pega info do apelido no banco de info
            Map info = SQLQuery.getInfoFor(nick);

            // se existir alguma informa�ao
            if (!info.isEmpty()) {

                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_047", info.get("ns_nick").toString()));

                // se usuario for sysop, entao mostra o email
                if (tmpUser.isIRCop() || tmpUser.isAdmin())
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_048", info.get("ns_email").toString()));

                String vhost = (String) info.get("ns_vhost");
                if (vhost != null) {
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_066", vhost));
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_067", info.get("ns_vhosttime").toString()));
                }

                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_049", info.get("ns_date").toString()));

                // se usuario for ircop
                if (SQLQuery.isIRCOP(nick))
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_050", ""));
                else
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_051", ""));

                // pega lista de canais onde usuario eh founder
                ArrayList canaisFounder = SQLQuery.getFounderChannels(nick);

                // se existir algum canal
                if (!canaisFounder.isEmpty()) {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_052", ""));

                    for (Object separa : canaisFounder)
                        Message.noticeSvs(tmpUser, svs, " -> " + separa);

                }

                // pega lista de canais onde usuario eh owner
                ArrayList canaisOwner = SQLQuery.getOwnerChannels(nick);

                // se existir algum canal
                if (!canaisOwner.isEmpty()) {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_053", ""));

                    for (Object separa : canaisOwner)
                        Message.noticeSvs(tmpUser, svs, " -> " + separa);

                }

                // pega lista de canais onde usuario eh host
                ArrayList canaisHost = SQLQuery.getHostChannels(nick);

                // se existir algum canal
                if (!canaisHost.isEmpty()) {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_054", ""));

                    for (Object separa : canaisHost)
                        Message.noticeSvs(tmpUser, svs, " -> " + separa);

                }

            } else {

                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "nickserv_024", nick));

            }

        } catch (Exception e) {

            new LogIRCd("NickServ.java", "infoFor()", "" + e);

        }

    }

    public void passaCanalAdiante(User tmpUser, String apelido) {

        try {

            // pega canais onde apelido � fundador
            ArrayList canais = SQLQuery.getFounderChannels(apelido);

            // se canais for diferente de nada
            if (!canais.isEmpty()) {

                // lista canais e os trata individualmente
                for (Object separa : canais) {

                    String sala = separa.toString();
                    String sucessor = SQLQuery.getSucessor(sala);

                    // se sala tiver sucessor
                    if (!sucessor.equals("")) {

                        // define sucessor como fundador da sala
                        SQLQuery.setFounder(sala, sucessor);

                        // seta sucessor para nada
                        SQLQuery.setSucessor(sala, "");

                        // se canal estiver aberto, avisa no topico que a partir de agora o fundador
                        // � tal, por falta de frequencia do fundador anterior
                        Channel channelTemp = Server.getChannel(sala);
                        if (channelTemp != null)
                            channelTemp.setTopic(Lang.traduz(tmpUser, "chanserv_050", sala + "%%" + sucessor));

                    } else {

                        // senao deleta sala
                        SQLQuery.removeChannel(sala);

                    }

                }

            }

        } catch (Exception e) {

            new LogIRCd("NickServ.java", "passaCanalAdiante()", e + "");

        }

    }

}