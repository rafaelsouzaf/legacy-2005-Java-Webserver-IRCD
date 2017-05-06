package chatmania.ircd;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.ircd.commands.*;
import chatmania.ircd.services.chanserv.ChanServ;
import chatmania.ircd.services.news.News;
import chatmania.ircd.services.nickserv.NickServ;
import chatmania.ircd.services.operserv.OperServ;

import java.util.HashMap;

public class Commands {

    // cole�ao
    private static final HashMap<String, CommandsI> commands;

    static {

        // inicializa cole�ao
        commands = new HashMap<String, CommandsI>();

        // services
        commands.put("NICKSERV", new NickServ());
        commands.put("CHANSERV", new ChanServ());
        commands.put("NS", new NickServ()); // c�pia de NickServ
        commands.put("CS", new ChanServ()); // c�pia de ChanServ
        commands.put("NEWS", new News());
        commands.put("OPERSERV", new OperServ());

        // comandos
        commands.put("NICK", new Nick());
        commands.put("PASS", new Pass());
        commands.put("AWAY", new Away());
        commands.put("JOIN", new Join());
        commands.put("J", new Join()); // copia de Join
        commands.put("MOTD", new Motd());
        commands.put("PART", new Part());
        commands.put("UPTIME", new Uptime());
        commands.put("INFO", new Info());
        commands.put("PRIVMSG", new Privmsg());
        commands.put("ME", new Me());
        commands.put("TOPIC", new Topic());
        commands.put("KICK", new Kick());
        commands.put("IRCOPS", new Ircops());
        commands.put("WHOIS", new Whois());
        commands.put("MODE", new Mode());
        commands.put("W", new Whois()); // c�pia de Whois
        commands.put("LUSERS", new Lusers());
        commands.put("QUIT", new Quit());

    }

    public void invoca(User tmpUser, String cmd) {

        try {

            if (cmd.length() > 0) {

                // se for flood
                if (Util.isFlooding(tmpUser)) {

                    Util.releaseUserID(tmpUser, Lang.traduz(tmpUser, "webchat_068", ""));

                } else {

                    // pega primeira palavra, coloca para maiuscula e retira espa�o
                    // adiciona espa�o ao cmd
                    // se retirar esse espa�o os comandos como /lusers
                    // que possuem uma �nica palavra nao funcionam
                    String comando = cmd.substring(0, (cmd + " ").indexOf(' ')).toUpperCase().trim();

                    // se comando estiver cadastrado nas cole�oes
                    if (commands.containsKey(comando)) {

                        // execute comando
                        CommandsI com = commands.get(comando);

                        // se com for diferente de null
                        if (com != null)
                            com.execute(tmpUser, cmd);

                    } else {

                        // comando inexistente
                        new Unknow().execute(tmpUser, cmd);

                    }

                }

            }

        } catch (Exception e) {
        }

    }

}