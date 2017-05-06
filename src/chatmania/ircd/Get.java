package chatmania.ircd;

import chatmania.Kline;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;
import chatmania.extra.log.LogIRCd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Get extends Commands {

    // cole�ao de tipos
    private static final HashMap<String, String> mime = new HashMap<String, String>();

    // caminho completo do diretorio raiz
    private static String rootDir;

    static {

        try {

            // pega caminho completo do diretorio raiz
            rootDir = new File("wwwroot/public").getCanonicalPath();

        } catch (Exception e) {
        }

        mime.put(".jpg", "image/jpeg");
        mime.put(".jpeg", "image/jpeg");
        //mime.put(".jpe",  "image/jpeg");
        mime.put(".gif", "image/gif");
        //mime.put(".png",  "image/png");
        //mime.put(".tiff", "image/tiff");
        //mime.put(".tif",  "image/tiff");

        mime.put(".html", "text/html");
        mime.put(".htm", "text/html");
        mime.put(".txt", "text/plain");
        //mime.put(".inf",  "text/plain");
        //mime.put(".nfo",  "text/plain");
        mime.put(".css", "text/css");
        mime.put(".xml", "text/xml");
        //mime.put(".dtd",  "text/xml");
        mime.put(".rss", "text/rss");

        //mime.put(".mpeg", "video/mpeg");
        //mime.put(".mpg",  "video/mpeg");
        //mime.put(".mpe",  "video/mpeg");
        //mime.put(".avi",  "video/x-msvideo");

        mime.put(".js", "application/x-javascript");
        //mime.put(".rtf",  "application/rtf");
        //mime.put(".doc",  "application/msword");
        //mime.put(".pdf",  "application/pdf");
        //mime.put(".ppt",  "application/powerpoint");
        //mime.put(".zip",  "application/zip");

        //mime.put(".mpga", "audio/mpeg");
        //mime.put(".mp2",  "audio/mpeg");
        //mime.put(".mp3",  "audio/mpeg");
        //mime.put(".wav",  "audio/x-wav");

    }

    public void geraOutros(Socket socket, String cmd, String ip) {

        try {

            // retira GET e HTTP 1.0 da requisi�ao
            String arquivo = cmd.substring(4, cmd.length() - 9).trim();

            // pega parametros do arquivo requisitado
            // e filtra e deixa apenas o nome do arquivo
            String parametrosDoArquivo;
            String arquivoLimpo;

            if (arquivo.contains("?")) {
                parametrosDoArquivo = arquivo.substring(arquivo.indexOf('?'), arquivo.length());
                arquivoLimpo = arquivo.substring(0, arquivo.indexOf('?'));
            } else {
                parametrosDoArquivo = "";
                arquivoLimpo = arquivo;
            }

            // gera instancia File do arquivo requisitado
            File file = new File("wwwroot/public/" + arquivoLimpo);

            // saida
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

            if (!file.getCanonicalPath().startsWith(rootDir)) {

                // envia header 403
                output.write(("HTTP/1.0 403 Forbidden\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                        "\r\n" +
                        "<h1>403 Forbidden</h1><code>" + arquivo + "</code><p><hr>" +
                        "<i>ChatMania WebServer</i>").getBytes());
                output.flush();

                // fecha conexao
                socket.close();

                // se arquivo for um diret�rio
            } else if (file.isDirectory()) {

                // se existir arquivo index
                if (new File(file + "/index.irc").exists()) {

                    // re-chama metodo execute com o arquivo index
                    geraOutros(socket, "GET " + arquivoLimpo + "/index.irc" + parametrosDoArquivo + " HTTP/1.1", ip);

                } else {

                    // cria stringbuffer para ser utilizado na resposta
                    StringBuffer conteudo = new StringBuffer();

                    // imprime header 200
                    conteudo.append("HTTP/1.0 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                            "\r\n" +
                            "<html><head><title></title></head><body><h1>Directory Listing</h1>" +
                            "<h3>" + arquivo + "/</h3>" +
                            "<table border='0' cellspacing='1'>" +
                            "<tr><td width='20%'><b>Filename</b><br></td><td width='20%'><b>Size</b></td><td width='60%'><b>Last Modified</b></td></tr>" +
                            "<tr><td width='20%'><b><a href='../'>../</b><br></td><td width='20%'></td><td width='60%'></td></tr>");

                    // forma tabela com a lista de arquivos
                    for (File seila : file.listFiles()) {

                        if (seila.isDirectory())
                            conteudo.append("<tr><td width='20%'><b><a href=\"" + arquivo + "/" + seila.getName() + "/\">" + seila.getName() + "/</a></b></td><td width='20%'></td><td width='60%'></td></tr>");
                        else
                            conteudo.append("<tr><td width='20%'><a href=\"" + arquivo + "/" + seila.getName() + "\">" + seila.getName() + "</a></td><td width='20%'>" + seila.length() + "</td><td width='60%'>" + new Date(seila.lastModified()).toString() + "</td></tr>");

                    }

                    conteudo.append("</table><hr><i>ChatMania WebServer</i></body><html>");

                    // imprime
                    output.write(conteudo.toString().getBytes());
                    output.flush();

                    // fecha conexao
                    socket.close();

                }

                // se file nao existir
            } else if (!file.exists()) {

                // imprime header 404 not found
                output.write(("HTTP/1.0 404 File Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                        "\r\n" +
                        "<h1>404 File Not Found</h1><code>" + arquivo + "</code><p><hr>" +
                        "<i>ChatMania WebServer</i>").getBytes());
                output.flush();

                // fecha conexao
                socket.close();

                // imprime arquivos
            } else {

                // se arquivo for php
                if (arquivoLimpo.endsWith(".php") || arquivoLimpo.endsWith(".irc")) {

                    // diz que arquivo foi encontrado
                    output.write("HTTP/1.0 200 OK\r\n".getBytes());

                    // inicializa inteiro
                    int i = 0;

                    // adiciona argumentos
                    Map mapa = Util.getParameter(arquivo);

                    // Monta array de strings que o php reconhece.
                    // O executavel CGI do php deve ser utilizado da seguinte forma
                    // array(php-cgi.exe, arquivo.php, param1=teste1, param2=teste2, param3=teste3)
                    // Onde parametros sao os componentes GET da url
                    String[] parametros = new String[mapa.size() + 2];

                    // adiciona path do php
                    parametros[i++] = Config.PHP;

                    // adiciona arquivo
                    parametros[i++] = file.getCanonicalPath();

                    for (Object o : mapa.entrySet()) {

                        Map.Entry entry = (Map.Entry) o;
                        parametros[i++] = entry.getKey() + "=" + entry.getValue();

                    }

                    // envia processo para o php
                    Process process = Runtime.getRuntime().exec(parametros);
                    //Process process = new ProcessBuilder(parametros).start();

                    // recebe resposta do processo
                    InputStream in = process.getInputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = in.read(buffer, 0, 4096)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    in.close();

                    // flush
                    output.flush();

                    // fecha conexao
                    socket.close();

                } else {

                    // se requisicao for referente ao channels.xml, entao imprime registro na tela
                    if (arquivoLimpo.equals("/xml/channels.xml"))
                        System.out.println("IP " + ip + " - channels.xml as " + new Date());

                    // envia tipo de arquivo em questao
                    contentType(output, file);

                    // l� arquivo
                    FileInputStream in = new FileInputStream(file);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = in.read(buffer, 0, 4096)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    in.close();

                    // flush
                    output.flush();

                    // fecha conexao
                    socket.close();

                }

            }

        } catch (Exception e) {

            new LogIRCd("Get.java", "/" + cmd, "" + e + "");

            // fecha conexao
            // � importante manter esse fechamento de conexao porque se ocorrer alguma
            // exce�ao ele fecha a conexao do usuario, porque senao ele mantem a conexao
            // aberta e isso acaba trancando o envio de mensagens do usuario do webchat
            // ja que o navegador suporta no maximo 2 requisi�oes abertas para o mesmo
            // dominio (eu acho)
            try {
                socket.close();
            } catch (Exception ex) {
            }

        }

    }

    public void geraSource(User tmpUser, String cmd) {

        try {

            // diz que arquivo foi encontrado
            tmpUser.getOutput().write("HTTP/1.0 200 OK\r\n".getBytes());
            tmpUser.getOutput().flush();

            // inicializa inteiro
            int i = 0;

            // adiciona argumentos
            Map mapa = Util.getParameter(cmd);

            // Monta array de strings que o php reconhece.
            // O executavel CGI do php deve ser utilizado da seguinte forma
            // array(php-cgi.exe, arquivo.php, param1=teste1, param2=teste2, param3=teste3)
            // Onde parametros sao os componentes GET da url
            String[] parametros = new String[mapa.size() + 2];

            // adiciona path do php
            parametros[i++] = Config.PHP;

            // adiciona arquivo
            parametros[i++] = new File("wwwroot/public/index.irc").getCanonicalPath();

            for (Object o : mapa.entrySet()) {

                Map.Entry entry = (Map.Entry) o;
                parametros[i++] = entry.getKey() + "=" + entry.getValue();

            }

            // envia processo para o php
            Process process = Runtime.getRuntime().exec(parametros);

            // recebe resposta do processo
            InputStream in = process.getInputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(buffer, 0, 4096)) != -1) {
                tmpUser.getOutput().write(buffer, 0, bytesRead);
            }
            in.close();

            // flush
            tmpUser.getOutput().flush();

            // se IP j� tiver mais que conexoes permitidas
            if (Util.isCloned(tmpUser.getIP())) {

                Util.releaseUserID(tmpUser, Lang.traduz(tmpUser, "webchat_060", tmpUser.getIP() + "%%" + Config.EMAIL));

                // se estiver na lista de kline
            } else if (!Kline.isKline(tmpUser.getIP()).equals("")) {

                Util.releaseUserID(tmpUser, Lang.traduz(tmpUser, "webchat_011", Kline.isKline(tmpUser.getIP())));

            } else {

                // A long secret � criada para que os frames tenham alguma
                // coisa em comum e possam se comunicar
                // Sem a secret o envio de mensagem nao poderia existir ja
                // que cada frame usa uma conexao diferente, entao tem que
                // existir essa variavel para ter algo em comum entre os frames
                long secret = Long.parseLong(tmpUser.getIP().replace(".", "")) + System.currentTimeMillis();

                // seta variavel secreta para intercambio de informa�oes
                // entre os frames
                tmpUser.setSecretId(secret);

                // imprime secretid para o usuario
                Message.tellUserAlternativeWeb(tmpUser, "<script>setSecretId('" + secret + "');</script>");

                // apelido
                String apelido = (String) mapa.get("apelido");

                // verifica se apelido � valido
                // senao cria um apelido generico
                if (apelido.equals("") || !Util.isValidNick(apelido) || Server.getUser(apelido) != null) {
                    apelido = "WebChat" + ((int) (Math.random() * 9999D));
                }

                super.invoca(tmpUser, "NICK " + apelido);

                // se usuaio j� possui um nick, entao ele pode continuar.
                // Essa verifica�ao � util porque se um usuario escolhesse nick forbidado por exemplo,
                // esse nick seria refeitado mas mesmo assim ele iria receber o motd e join abaixo, entao
                // ficaria um nick invisivel na sala
                if (!tmpUser.getNick().equals("")) {

                    // mostra motd
                    super.invoca(tmpUser, "MOTD");

                    // entra na sala que deseja
                    super.invoca(tmpUser, "JOIN " + mapa.get("sala"));

                } else {

                    Util.releaseUserID(tmpUser, "");

                }

            }

        } catch (Exception e) {

            new LogIRCd("Get.java (geraSource)", tmpUser.getNick() + " => /" + cmd, "" + e + "");

            // fecha conexao
            // � importante manter esse fechamento de conexao porque se ocorrer alguma
            // exce�ao ele fecha a conexao do usuario, porque senao ele mantem a conexao
            // aberta e isso acaba trancando o envio de mensagens do usuario do webchat
            // ja que o navegador suporta no maximo 2 requisi�oes abertas para o mesmo
            // dominio (eu acho)
            Util.releaseUserID(tmpUser, "");

        }

    }

    public void geraGo(Socket socket, String cmd) {

        try {

            // saida
            BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());

            output.write(("HTTP/1.0 200 OK\r\n" +
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: ChatMania WebServer\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                    "\r\n").getBytes());
            output.flush();

            // retira GET e HTTP 1.0 da requisi�ao
            String arquivo = cmd.substring(4, cmd.length() - 9).trim();

            // pega parametros da URL
            Map parametros = Util.getParameter(arquivo);

            // recupera mensagem
            String mensagem = (String) parametros.get("msg");

            // se a mensagem for diferente de nada
            if (!mensagem.equals("")) {

                // pega instancia do usuario
                User temp = Server.getUser((String) parametros.get("nick"));
                if (temp != null) {

                    // recupera variavel secreta
                    long secretvar = Long.parseLong((String) parametros.get("userID"));

                    // se a secret do request for a mesma secret do usuario
                    if (temp.getSecretId() == secretvar) {

                        // filtra mensagem
                        mensagem = URLDecoder.decode(mensagem.replace("%27", ""), "ISO-8859-1");

                        // se usuario nao for ircop nem admin, entao retira caracter <
                        if (!temp.isIRCop() && !temp.isAdmin())
                            mensagem = mensagem.replace("<", "&lt;");

                        // recupera janela e filtra caracteres
                        String janela = URLDecoder.decode(((String) parametros.get("janela")).replace("%27", ""), "ISO-8859-1");

                        // verifica se a mensagem � um /comando
                        if (mensagem.charAt(0) == '/') {

                            if (mensagem.startsWith("/clear")) {

                                // imprime mensagem chamando funcao javascript limpaTela
                                Message.tellUserAlternativeWeb(temp, "<script>limpaTela('*** Tela limpa.<br>')</script>");

                            } else if (mensagem.startsWith("/hop")) {

                                // Se janela come�ar com o caracter #. Isso evita de dar /hop
                                // em um pvt e acabar entrando em uma sala com o nome do apelido que est� no privado
                                if (janela.charAt(0) == '#') {

                                    super.invoca(temp, "PART " + janela);
                                    Message.tellUserAlternativeWeb(temp, "<script>limpaTela()</script>");
                                    super.invoca(temp, "JOIN " + janela);

                                }

                            } else if (mensagem.startsWith("/query") || mensagem.startsWith("/q ")) {

                                String vitima = mensagem.substring(mensagem.indexOf(' ')).trim();
                                User tmpVitima = Server.getUser(vitima);
                                if (tmpVitima != null)
                                    Message.tellUserAlternativeWeb(temp, "<script>privado('" + tmpVitima.getNick() + "')</script>");
                                else
                                    // avisa que usuario nao existe
                                    Message.tellUser(temp, "Atual", "<font color=#FF0000>" + Lang.traduz(temp, "webchat_047", vitima) + "</font>", 0);

                            } else if (mensagem.startsWith("/voice") || mensagem.startsWith("/v ")) {

                                super.invoca(temp, "MODE SET VOICE " + janela + " " + mensagem.substring(mensagem.indexOf(' '), mensagem.length()).trim());

                            } else if (mensagem.startsWith("/action") || mensagem.startsWith("/me ")) {

                                super.invoca(temp, "ME " + janela + " " + mensagem.substring(mensagem.indexOf(' '), mensagem.length()).trim());

                            } else if (mensagem.startsWith("/list")) {

                                // chama modulo que imprime lista de salas do sql
                                Message.tellUserAlternativeWeb(temp, "<script>sendUrl('Salas', '?modules=Others&Part=ListRoom')</script>");

                            } else {

                                super.invoca(temp, mensagem.substring(1));

                            }

                        } else {

                            super.invoca(temp, "PRIVMSG " + janela + " " + mensagem);

                        }

                    }

                }

            }

            // fecha conexao
            socket.close();

        } catch (Exception e) {

            new LogIRCd("Get.java", "go()", "" + e);

            // fecha conexao
            // � importante manter esse fechamento de conexao porque se ocorrer alguma
            // exce�ao ele fecha a conexao do usuario, porque senao ele mantem a conexao
            // aberta e isso acaba trancando o envio de mensagens do usuario do webchat
            // ja que o navegador suporta no maximo 2 requisi�oes abertas para o mesmo
            // dominio (eu acho)
            try {
                socket.close();
            } catch (Exception ex) {
            }

        }

    }

    private void contentType(BufferedOutputStream output, File file) {

        try {

            // pega o tipo de arquivo que �
            String extension = "";
            String filename = file.getName();
            int dotPos = filename.lastIndexOf(".");
            if (dotPos >= 0)
                extension = filename.substring(dotPos).toLowerCase();
            String contentType = mime.get(extension);

            // se o tipo for igual a null
            // entao � tipo default
            if (contentType == null)
                contentType = "application/octet-stream";

            output.write(("HTTP/1.0 200 OK\r\n" +
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: ChatMania WebServer\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                    "Content-Length: " + file.length() + "\r\n" +
                    "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                    "\r\n").getBytes());
            output.flush();

        } catch (Exception e) {

            new LogIRCd("Get.java", "contentType()", e + "");

        }

    }

}