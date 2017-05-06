package chatmania;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    // variavel uptime
    private static final long uptime = System.currentTimeMillis();

    // cole�ao de canal
    private static final ConcurrentHashMap<String, Channel> channel = new ConcurrentHashMap<String, Channel>(50);

    // cole�ao de user
    private static final ConcurrentHashMap<String, User> user = new ConcurrentHashMap<String, User>(100);

    // recupera tmpUser de determinado nick
    public static User getUser(String nick) {

        return user.get(nick.toLowerCase());

    }

    // recupera Enumeration do user
    public static Enumeration<User> getUserElements() {

        return user.elements();

    }

    // recupera tmpCanal de determinado canal
    public static Channel getChannel(String sala) {

        return channel.get(sala.toLowerCase());

    }

    // recupera Enumeration do canal
    public static Enumeration<Channel> getChannelElements() {

        return channel.elements();

    }

    // recupera tamanho da cole�ao user
    public static int getUserSize() {

        return user.size();

    }

    // recupera tamanho da colecao canal
    public static int getChannelSize() {

        return channel.size();

    }

    // adiciona novo user a cole�ao
    public static void setUser(User tmpUser, String apelido) {

        // adiciona a cole�ao
        user.put(apelido.toLowerCase(), tmpUser);

    }

    // remove user da cole�ao
    public static void unsetUser(String apelido) {

        user.remove(apelido.toLowerCase());

    }

    // adiciona novo canal a cole�ao
    public static void setChannel(Channel tmpChan, String sala) {

        channel.put(sala.toLowerCase(), tmpChan);

    }

    // remove sala da cole�ao
    public static void unsetChannel(String sala) {

        channel.remove(sala.toLowerCase());

    }

    // recupeta uptime, tempo online
    public static long getUptime() {

        return uptime;

    }

}
