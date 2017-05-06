package chatmania.extra;

import chatmania.Channel;
import chatmania.Server;
import chatmania.User;
import chatmania.extra.log.LogIRCd;
import chatmania.ircd.Config;
import chatmania.ircd.Message;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.*;

public class Util {

    public static boolean isNumeric(String c) {

        try {

            Integer.parseInt(c);
            return true;

        } catch (Exception e) {
        }

        return false;

    }

    public static boolean isCloned(String ip) {

        int clones = 0;

        // se IP do usuario tiver na lista de cloneExcecao
        // libera conexao
        if (Config.EXCEPTION.contains(ip))
            return false;

        // verifica quais usuarios tem o mesmo IP
        // e soma o numero dos IPs iguais a este
        for (Enumeration<User> enu = Server.getUserElements(); enu.hasMoreElements(); ) {

            User temp = enu.nextElement();
            if (temp.getIP().equals(ip))
                ++clones;

        }

        // se o numero de clones for maior
        // que o limite permitido
        return clones >= Config.CLONELIMIT;

    }

    public static boolean isFlooding(User tmpUser) {

        if ((System.currentTimeMillis() - tmpUser.getLastMsg()) <= 3000) {

            tmpUser.incFloodCount(1);

            if (tmpUser.floodCount() > Config.FLOODLIMIT) {

                return true;

            } else {

                tmpUser.setLastMsg(System.currentTimeMillis());

            }

        } else {

            tmpUser.resetFloodCount();
            tmpUser.setLastMsg(System.currentTimeMillis());

        }

        return false;

    }

    public static boolean isValidEmail(String email) {

        // se email conter @ e .
        // entao eh valido
        return email.contains("@") && email.contains(".");

    }

    public static boolean isValidCanal(String canal) {

        try {

            // verifico se nick tem caracter ilegal
            String caracteresLegais = Config.CHANNELCHARS;

            // transforma strings em caixa baixa
            caracteresLegais = caracteresLegais.toLowerCase();
            canal = canal.toLowerCase();

            for (int i = 0; i < canal.length(); i++) {

                if (caracteresLegais.indexOf(canal.charAt(i)) == -1)
                    return false;

            }

            // se canal tiver mais que caracteres permitidos
            if (canal.length() > Config.MAXCHANNELLEN)
                return false;

        } catch (Exception e) {

            new LogIRCd("Util.java", "isValidCanal", e + "");

        }

        return true;

    }

    public static boolean isValidNick(String apelido) {

        try {

            // verifico se nick tem caracter ilegal
            String caracteresLegais = Config.NICKCHARS;

            // transforma strings em caixa baixa
            caracteresLegais = caracteresLegais.toLowerCase();
            apelido = apelido.toLowerCase();

            for (int i = 0; i < apelido.length(); i++) {

                if (caracteresLegais.indexOf(apelido.charAt(i)) == -1)
                    return false;

            }

            // se nick tiver mais que caracteres permitidos
            if (apelido.length() > Config.MAXNICKLEN)
                return false;

        } catch (Exception e) {

            new LogIRCd("Util.java", "isValidNick", e + "");

        }

        return true;

    }

    public static void releaseUserID(User tmpUser, String msg) {

        try {

            // envia mensagem de quit para todos os canais onde est�
            for (Object tmp : tmpUser.getListaDeSalas()) {

                Channel tmpChan = (Channel) tmp;
                if (tmpChan != null) {

                    // remove usuario desse canal
                    tmpChan.removeUser(tmpUser);

                    // envia mensagem para o canal relatando a saida do usuario
                    Message.tellUsersInChan(tmpChan, "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_006", tmpUser.getNick() + "%%" + msg) + "</font>", true);

                    // se sala tiver 0 usuarios, entao fecha a sala
                    // retira canal da cole�ao de canais
                    if (tmpChan.getMemberSize() == 0)
                        Server.unsetChannel(tmpChan.getName());

                }

            }

            // comunica a si pr�prio
            Message.tellUser(tmpUser, "Atual", "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_007", msg) + "</font>", 0);
            Message.tellUserAlternativeWeb(tmpUser, "<script>alert('" + Lang.traduz(tmpUser, "webchat_007", msg) + "');</script>");

            // retira user da cole�ao
            Server.unsetUser(tmpUser.getNick());

            // limpa usu�rio
            tmpUser.clear();

            // fecha socket
            tmpUser.getSocket().close();

        } catch (Exception e) {
        }

    }

    public static void changeNick(User tmpUser, String novoNick) {

        // nick e mascara que ficar� desatualizada
        String nicktemp = tmpUser.getNick();

        // modifica nick para novo nick
        tmpUser.setNick(novoNick);

        // o proprio usuario tem que receber uma mensagem dizendo que ele mudou o nick
        // se o usuario estiver em algum canal, ele receber� esse comunicado juntamente
        // com os outros usuarios do canal, mas se ele nao estiver em nenhum canal, o boolean
        // abaixo ficara false, isso garante que o nick sera comunicado caso esteja fora dos canais
        boolean estaEmAlgumCanal = false;

        // lista todos os canais
        for (Object tmp : tmpUser.getListaDeSalas()) {

            Channel tmpChan = (Channel) tmp;
            if (tmpChan != null) {

                // se usuario esta em algum canal, seta true
                // entao o proprio usuario recebera a mensagem
                // de aviso de troca de nick
                estaEmAlgumCanal = true;

                // envia mensagem para o canal relatando a saida do usuario
                Message.tellUsersInChan(tmpChan, "<font color=#000080>" + Lang.traduz(tmpUser, "webchat_005", nicktemp + "%%" + novoNick) + "</font>", true);

            }

        }

        // se usuario nao estiver em nenhum canal
        // entao ele receber� a mensagem de troca de nick agora
        if (!estaEmAlgumCanal)
            Message.tellUser(tmpUser, "Atual", "<font color=#008000>" + Lang.traduz(tmpUser, "webchat_005", nicktemp + "%%" + novoNick) + "</font>", 0);

        // substitui novo user ao Map
        Server.unsetUser(nicktemp);
        Server.setUser(tmpUser, novoNick);

        Message.tellUserAlternativeWeb(tmpUser, "<script>users('','', '" + novoNick + "');</script>");

        // se nao for admin entao retira identifica�ao
        if (!tmpUser.isAdmin())
            tmpUser.setIdentify(false);

    }

    public static String encrypt(String password) {

        // encrypta a senha 3 vezes
        String primeira = new BASE64Encoder().encode(password.getBytes());
        String segunda = new BASE64Encoder().encode(primeira.getBytes());
        return new BASE64Encoder().encode(segunda.getBytes());

    }

    public static String decrypt(String password) {

        try {

            // decrypta a senha 3 vezes
            String primeira = new String(new BASE64Decoder().decodeBuffer(password), "ISO-8859-1");
            String segunda = new String(new BASE64Decoder().decodeBuffer(primeira), "ISO-8859-1");
            return new String(new BASE64Decoder().decodeBuffer(segunda), "ISO-8859-1");

        } catch (Exception e) {

            new LogIRCd("Util.java", "decrypt", e + "");

        }

        return "";

    }

    public static String filtraAcentos(String msg) {

        // replace de acentos prejudiciais
        return msg.replace("\"", "&quot;").replace("'", "");

    }

    public static Map<String, String> getParameter(String url) {

        // blabla.php?nome=Fulano&idade=45&cidade=poa

        Map<String, String> parametros = new HashMap<String, String>();

        // se a url conter ? (ponto de interroga�ao)
        // trata-se de uma url com parametros
        if (url.contains("?")) {

            url = url.substring(url.indexOf('?') + 1);

            // separa parametros
            for (String separa : url.split("&")) {

                // split para separar os parametros
                String[] outro = separa.split("=");

                // se existirem 2 parametros, entao define os dois
                // senao define 1 deles como vazio
                if (outro.length > 1)
                    parametros.put(outro[0], outro[1]);
                else
                    parametros.put(outro[0], "");

            }

        }

        return parametros;

    }

    private static String mirc2hex(int cor) {

        switch (cor) {
            case 0:
                return "#FFFFFF";
            case 1:
                return "#000000";
            case 2:
                return "#00007F";
            case 3:
                return "#009300";
            case 4:
                return "#FF0000";
            case 5:
                return "#7F0000";
            case 6:
                return "#9C009C";
            case 7:
                return "#FC7F00";
            case 8:
                return "#FFFF00";
            case 9:
                return "#00FC00";
            case 10:
                return "#009393";
            case 11:
                return "#00FFFF";
            case 12:
                return "#0000FC";
            case 13:
                return "#FF00FF";
            case 14:
                return "#7F7F7F";
            case 15:
                return "#D2D2D2";
            default:
                return "";
        }

    }

    public static String mircHtml(String texto) {

        try {

            StringBuffer tempTexto = new StringBuffer();
            String corTexto = "";
            int nCorTexto = 0;
            int nBold = 0;
            int nUnderline = 0;
            int txtlen = texto.length();

            for (int i = 0; i < txtlen; i++) {

                char carac = texto.charAt(i);

                if (carac == '\u0002') { // bold

                    tempTexto.append("<b>");
                    nBold++;

                } else if (carac == '\u001f') { // underline

                    tempTexto.append("<u>");
                    nUnderline++;

                } else if (carac == '\u0003') { // cor

                    if (i == txtlen - 1)
                        continue;

                    for (int x = i + 1; x <= i + 5; x++) {

                        if (Character.isDigit(texto.charAt(x))) {
                            if (corTexto.length() < 2)
                                corTexto += texto.charAt(x);
                            else
                                break;
                        } else {
                            break;
                        }

                        if (x == txtlen - 1)
                            break;

                    }

                    i += corTexto.length();

                    if (!corTexto.equals("")) {
                        tempTexto.append("<font color=").append(mirc2hex(Integer.parseInt(corTexto))).append(">");
                        nCorTexto++;
                    }

                    corTexto = "";

                } else { // texto

                    tempTexto.append(texto.charAt(i));

                }

            }

            // remove tags
            while (nBold + nUnderline + nCorTexto > 0) {

                if (nBold > 0) {
                    tempTexto.append("</b>");
                    nBold--;
                }
                if (nUnderline > 0) {
                    tempTexto.append("</u>");
                    nUnderline--;
                }
                if (nCorTexto > 0) {
                    tempTexto.append("</font>");
                    nCorTexto--;
                }

            }

            return tempTexto.toString();

        } catch (Exception e) {

            return "";

        }

    }

    public static void pegaUsuarios(User tmpUser, Channel channel) {

        try {

            if ((channel != null) && (channel.getMemberSize() > 0)) {

                ArrayList<String> arraylist = new ArrayList<String>();
                for (Enumeration<User> enu2 = channel.getList(); enu2.hasMoreElements(); ) {

                    User tmpID = enu2.nextElement();
                    arraylist.add(channel.getMemberStatusStr(tmpID) + tmpID.getNick());

                }

                Collections.sort(arraylist);
                String lista = arraylist.toString().replace(", ", ":").substring(1);
                Message.tellUserAlternativeWeb(tmpUser, "<script>users('" + lista.substring(0, lista.length() - 1) + "','" + channel.getName() + "', '" + tmpUser.getNick() + "');</script>");

            }

        } catch (Exception e) {
        }

    }

}