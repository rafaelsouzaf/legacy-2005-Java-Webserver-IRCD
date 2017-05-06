package chatmania.extra;

import chatmania.User;
import chatmania.extra.log.LogIRCd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Lang {

    // cole��es de idiomas
    private static final HashMap<String, String> portugues = new HashMap<String, String>(800);
    //private static final HashMap<String,String> english   = new HashMap<String, String>(800);
    //private static final HashMap<String,String> espanol   = new HashMap<String, String>(800);
    //private static final String idiomas = "english portugues espanol";
    private static final String idiomas = "portugues";

    public Lang() {

        leitorDeArquivos("portugues.lang", portugues);
        //leitorDeArquivos("english.lang", english);
        //leitorDeArquivos("espanol.lang", espanol);

    }

    public static void clear() {

        portugues.clear();
        //english.clear();
        //espanol.clear();

    }

    public static String traduz(User tmpUser, String id, String argumento) {

        //String lang = tmpUser.getLang();

        try {

            //if (lang.equals("portugues"))
            //msgPura = portugues.get(id);
            //else if (lang.equals("espanol"))
            //msgPura = espanol.get(id);
            //else if (lang.equals("english"))
            //msgPura = english.get(id);
            //else
            //msgPura = portugues.get(id);

            // essa verifica�ao faz com que o msgPura se retornar null,
            // por nao ter significado em determinado idioma, pega a resposta
            // no idioma padrao portugues
            //if (msgPura == null)
            //msgPura = portugues.get(id);

            // troca $ por nada se for webchat
            if (argumento.contains("$") && tmpUser.isWebchat())
                argumento = argumento.replace("$", "");

            // pega a mensagem pura da colecao idioma
            String msgPura = portugues.get(id);

            if (argumento.contains("%%")) {

                for (String separa : argumento.split("%%"))
                    msgPura = msgPura.replaceFirst("%s", separa);

                return msgPura;

            } else {

                return msgPura.replaceFirst("%s", argumento.replace("\\", ""));

            }

        } catch (Exception e) {

            //new LogIRCd("Lang.java", id + " -> " + argumento, e + "");

        }

        return " ";

    }

    public static String getIdiomas() {

        return idiomas;

    }

	/*
    public static void setaIdioma(User tmpUser) {

		String host = tmpUser.getSocket().getInetAddress().getHostName();
		host = host.substring(host.length() - 3, host.length());

		String hostPortugues = portugues.get("hosts");
		//String hostEnglish	 = english.get("hosts");
		//String hostEspanol	 = espanol.get("hosts");

		if (hostPortugues.indexOf(host) != -1) {

			tmpUser.setLang("portugues");

		//} //else if (hostEnglish.indexOf(host) != -1) {

			//tmpUser.setLang("english");

		//} //else if (hostEspanol.indexOf(host) != -1) {

			//tmpUser.setLang("espanol");

		} else {

			tmpUser.setLang(Config.LANG);

		}

	}*/

    // metodo Leitura de dados que receber� como parametro o nome
    // do arquivo a ser lido e a cole�ao que dever� receber os dados
    public void leitorDeArquivos(String arquivo, HashMap<String, String> colecao) {

        try {

            BufferedReader s1 = new BufferedReader(new FileReader("lang/" + arquivo));

            while (true) {

                String linha = s1.readLine();

                if (linha == null) {

                    break;

                } else if (linha.indexOf("hosts=") != -1) {

                    String id = linha.substring(0, linha.indexOf('='));
                    linha = linha.substring(linha.indexOf('=') + 1);
                    colecao.put(id, linha);

                } else if (linha.indexOf('{') != -1) {

                    String id = linha.substring(0, linha.indexOf('='));
                    StringBuffer bloco = new StringBuffer();
                    bloco.append(linha.substring(linha.indexOf('{') + 1));

                    boolean acabou = true;
                    while (acabou) {

                        linha = s1.readLine();
                        bloco.append(linha + " ");

                        if (linha.indexOf('}') != -1) {

                            colecao.put(id, bloco.toString().replace("}", ""));
                            acabou = false;

                        }

                    }

                } else if (linha.indexOf("//") != -1) {

                    // ignorar porque � coment�rio

                } else if (!linha.equals("")) {

                    String id = linha.substring(0, linha.indexOf('='));
                    linha = linha.substring(linha.indexOf('=') + 1);
                    colecao.put(id, linha);

                }

            }

        } catch (Exception e) {

            new LogIRCd("Lang.java", "leitorDeArquivos()", e + "");

        }

    }

}