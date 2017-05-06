package chatmania.ircd.commands;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.util.StringTokenizer;

public class Whois implements CommandsI {

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");
            st.nextToken();

            if (st.countTokens() == 1) {

                // pega vitima
                String vitima = st.nextToken();

                // pega instancia do usuario
                User tmpVitima = Server.getUser(vitima);

                // se usuario existir
                if (tmpVitima != null) {

                    // pega host
                    String host = tmpVitima.getHost();

                    // pega salas
                    // lista canais onde usuario est�
                    StringBuffer tmpChans = new StringBuffer();
                    for (Object tmp : tmpVitima.getListaDeSalas()) {

                        Channel tmpChan = (Channel) tmp;
                        if (tmpChan != null)
                            tmpChans.append(tmpChan.getName() + " ");

                    }

                    // verifica se usuario esta identificado
                    String identificado;
                    if (tmpVitima.isIdentify())
                        identificado = Lang.traduz(tmpUser, "webchat_020", "");
                    else
                        identificado = Lang.traduz(tmpUser, "webchat_021", "");

                    // verifica se esta inativo a tanto tempo
                    String inativo = "";
                    if (tmpVitima.getTempoInativo() > 0) {

                        // tempo inativo e tempo conectado
                        long segundos = (System.currentTimeMillis() - tmpVitima.getTempoInativo()) / 1000;

                        // se segundos for maior que 60, entao divide e transforma em minutos
                        if (segundos > 60)
                            inativo = (segundos / 60) + " " + Lang.traduz(tmpUser, "webchat_024", "");
                        else
                            inativo = segundos + " " + Lang.traduz(tmpUser, "webchat_023", "");

                    }

                    // verifica se esta away
                    String away = "";
                    if (tmpVitima.isAway())
                        away = Lang.traduz(tmpUser, "webchat_022", "") + " " + tmpVitima.getAwayMessage();

                    // pega IP se ircop
                    String ip;
                    if (tmpUser.isIRCop() || tmpUser.isAdmin())
                        ip = tmpVitima.getIP();
                    else
                        ip = "";

                    // pega status se houver
                    String status = "";
                    if (tmpVitima.isIRCop())
                        status = "IRC Operator";

                    // publica whois para o webchat do usuario
                    Message.tellUserAlternativeWeb(tmpUser, "<script>imprimeWhois('" + vitima + "', '" + host + "','" + tmpChans.toString() + "','" + identificado + "','" + inativo + "','" + away + "','" + ip + "','" + status + "');</script>");

                    // se nao for admin, avisa vitima que usuario pegou seu whois
                    if (!tmpUser.isAdmin())
                        Message.tellUser(tmpVitima, "Atual", Lang.traduz(tmpUser, "webchat_067", tmpUser.getNick()), 0);

                } else {

                    // vitima nao existe
                    Message.tellUser(tmpUser, "Atual", "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_047", vitima) + "</font>", 0);

                }

            } else {

                // comando inexistente, exemplo correto �
                StringBuffer exemplo = new StringBuffer(Lang.traduz(tmpUser, "webchat_010", ""));
                exemplo.append("/whois John<br>");
                Message.tellUser(tmpUser, "Atual", exemplo.toString(), 0);

            }

        } catch (Exception e) {

            new LogIRCd("Whois.java", tmpUser.getNick() + " => /" + cmd, "" + e + "");

        }

    }

}
