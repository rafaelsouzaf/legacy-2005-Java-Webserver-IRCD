package chatmania.ircd.services.news;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQLQuery;
import chatmania.ircd.CommandsI;
import chatmania.ircd.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class News implements CommandsI {

    private static final String svs = NewsConfig.NAME;
    private static Date ultima;
    private static ArrayList<String> lista = new ArrayList<String>(100);

    public static ArrayList<String> getNewsList() {

        return lista;

    }

    public static Date getUltima() {

        return ultima;

    }

    public static void pegaNoticias() {

        // limpa lista
        lista.clear();

        for (String separa : NewsConfig.URL.split(";")) {

            try {

                URL url = new URL(separa);
                URLConnection cone = url.openConnection();
                BufferedReader s1 = new BufferedReader(new InputStreamReader(cone.getInputStream()));

                String titulo = "";
                String link;
                String line;

                while ((line = s1.readLine()) != null) {

                    // verifica se linha tem o campo <title> e </title>
                    if (line.contains("<title>") && line.contains("</title>"))
                        titulo = line.trim().replace("<title>", "").replace("</title>", "");

                    // se o titulo for vazio e a linha tiver os campos
                    // <link> e </link>, entao filtra e adiciona no lista
                    if ((!titulo.equals("")) && (line.contains("<link>")) && (line.contains("</link>"))) {

                        link = line.trim().replace("<link>", "").replace("</link>", "");

                        // cria a frase juntando o titulo com o link
                        // os espa�os sao necessarios para que o link fique separado
                        // das tags html e o webchat possa ver o link sem erro
                        lista.add(titulo + " <a href=" + link + " target=_blank><font color=#0000ff>" + link + "</font></a> ");

                        // limpa strings
                        titulo = "";
                        link = "";

                    }

                }

                s1.close();

            } catch (Exception e) {
            }

        }

        ultima = new Date();

    }

    public static void enviaNoticia(User tmpUser) {

        Message.tellUser(tmpUser, "Atual", "<font color=orange>&lt;</font>" + svs + "<font color=orange>&gt;</font> <font color=#800000>" + lista.get((int) (Math.random() * lista.size())) + "" + "</font>", 0);

    }

    public void execute(User tmpUser, String cmd) {

        try {

            StringTokenizer st = new StringTokenizer(cmd, " ");

            if (st.countTokens() > 1) {

                String com1 = st.nextToken();
                com1 = st.nextToken().toUpperCase().trim();

                boolean identificado = tmpUser.isIdentify();

                if (com1.equals("HELP")) {

                    if (st.countTokens() == 0) {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_001", ""));

                    } else if (st.countTokens() == 1) {

                        String parte1 = st.nextToken().toUpperCase();

                        if (parte1.equals("ON")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_002", ""));

                        } else if (parte1.equals("OFF")) {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_003", ""));

                        } else {

                            Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_006", ""));

                        }

                    }

                } else if (com1.equals("ON")) {

                    if (identificado) {

                        // atualiza status no banco de dados
                        SQLQuery.setNews(tmpUser.getNick(), 1);
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_005", ""));

                    } else {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_004", ""));

                    }

                } else if (com1.equals("OFF")) {

                    if (identificado) {

                        // atualiza status no banco de dados
                        SQLQuery.setNews(tmpUser.getNick(), 0);
                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_005", ""));

                    } else {

                        Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_004", ""));

                    }

                } else {

                    Message.noticeSvs(tmpUser, svs, Lang.traduz(tmpUser, "news_006", ""));

                }

            }

        } catch (Exception e) {

            new LogIRCd("News.java", "execute(" + cmd + ")", e + "");

        }

    }

}