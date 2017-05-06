package chatmania.ircd;

import chatmania.User;
import chatmania.extra.Lang;
import chatmania.extra.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connection {

    private static final Get get = new Get();

    public void conexao(Socket socket) throws IOException {

        // Objeto utilizado para saber que tipo de conexao �.
        // Ser� utilizado mais abaixo
        Object blah = null;

        try {

            // IP do usu�rio
            String userIp = socket.getInetAddress().getHostAddress();

            // input que l� o que o usuario digitar
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "ISO-8859-1"), 512);

            // string utilizada para pegar mensagem do usuario
            String message;

            while ((message = input.readLine()) != null) {

                // executa comando
                if (message.length() > 0) {

                    // pega primeira palavra, coloca para maiuscula e retira espa�o
                    // adiciona espa�o ao cmd
                    // se retirar esse espa�o os comandos como /lusers
                    // que possuem uma �nica palavra nao funcionam
                    String comando = message.substring(0, (message + " ").indexOf(' ')).toUpperCase().trim();

                    // se comando for GET
                    if (comando.equals("GET")) {

                        // se mensagem for requisicao ao source.html
                        if (message.contains("?modules=Blah&Part=Source")) {

                            // paralisa por segundo(s) dando tempo de carregar as outras paginas
                            Thread.sleep(1500);

                            // cria usuario
                            User tmpUser = new User(socket, userIp);

                            // define objeto como User
                            blah = tmpUser;

                            // execute comando
                            get.geraSource(tmpUser, message);

                        } else if (message.contains("go.html?msg=")) {

                            // execute comando
                            get.geraGo(socket, message);

                        } else {

                            // execute comando
                            get.geraOutros(socket, message, userIp);

                        }

                    }

                }

            }

        } catch (Exception e) {
        }

        // se conexao ainda nao estiver fechada
        if (!socket.isClosed()) {

            if (blah != null)
                Util.releaseUserID((User) blah, Lang.traduz((User) blah, "webchat_061", ""));
            else
                socket.close();

        }

    }

}