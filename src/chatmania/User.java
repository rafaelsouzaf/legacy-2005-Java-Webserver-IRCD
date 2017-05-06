package chatmania;

import chatmania.ircd.Message;

import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class User {

    private String nick = "";
    private String host = "";
    private String awayMsg = "";
    private String ip;
    private boolean away = false;
    private boolean identify = false;
    private boolean webchat = false;
    private boolean zombie = false;
    private boolean ircop = false;
    private boolean admin = false;
    private int floodCount = 0;
    private long lastNickChangeTime = (long) 0;
    private long lastMsg = System.currentTimeMillis();
    private long secretId = (long) 0;
    private long tempoInativo = (long) System.currentTimeMillis();
    private Socket socket;
    private BufferedOutputStream output;
    private ArrayList<User> logadoArray = new ArrayList<User>(5);
    private ArrayList<Channel> listaDeSalas = new ArrayList<Channel>(5);

    public User(Socket socket, String ip) {

        try {

            this.ip = ip;
            this.socket = socket;
            this.output = new BufferedOutputStream(socket.getOutputStream());

        } catch (Exception e) {
        }

    }

    public void clear() {

        nick = null;
        host = null;
        awayMsg = null;
        ip = null;
        away = false;
        identify = false;
        webchat = false;
        zombie = false;
        floodCount = 0;
        lastNickChangeTime = (long) 0;
        lastMsg = (long) 0;
        secretId = (long) 0;
        tempoInativo = (long) 0;
        logadoArray = null;

    }

    public BufferedOutputStream getOutput() {

        return output;

    }

    public String getIP() {

        return ip;

    }

    public long getTempoInativo() {

        return tempoInativo;

    }

    public void setTempoInativo(long i) {

        tempoInativo = i;

    }

    public boolean isWebchat() {

        return webchat;

    }

    public void setWebchat(boolean var) {

        webchat = var;

    }

    public boolean isIdentify() {

        return identify;

    }

    public void setIdentify(boolean v) {

        identify = v;

        // se usuario for webchat, entao atualiza o form de login
        if (webchat)
            Message.tellUserAlternativeWeb(this, "<script>topLogin('" + v + "')</script>");

    }

    public ArrayList getListaDeSalas() {
        return listaDeSalas;
    }

    public int getNumCanais() {

        return listaDeSalas.size();

    }

    public void incNewChannel(Channel channel) {

        listaDeSalas.add(channel);

    }

    public void delNewChannel(Channel channel) {

        listaDeSalas.remove(channel);

    }

    public boolean isAway() {

        return away;

    }

    public void setAway(String am) {

        away = true;
        awayMsg = am;

    }

    public String getAwayMessage() {

        return awayMsg;

    }

    public void setBack() {

        away = false;
        awayMsg = "";

    }

    public void incFloodCount(int i) {

        floodCount += i;

    }

    public int floodCount() {

        return floodCount;

    }

    public void resetFloodCount() {

        floodCount = 0;

    }

    public long getSecretId() {

        return secretId;

    }

    public void setSecretId(long secte) {

        secretId = secte;

    }

    public long getLastMsg() {

        return lastMsg;

    }

    public void setLastMsg(long lastMsg) {

        this.lastMsg = lastMsg;

    }

    public boolean isIRCop() {

        return ircop;

    }

    public void setIRCop(boolean b) {

        ircop = b;

    }

    public long getLastNickChangeTime() {

        return lastNickChangeTime;

    }

    public void setLastNickChangeTime(long tmp) {

        lastNickChangeTime = tmp;

    }

    public String getNick() {

        return nick;

    }

    public void setNick(String nick) {

        this.nick = nick;

    }

    public void setHost() {

        String novoHost = getIP();

        // substitui terminacao por .X
        novoHost = novoHost.substring(0, novoHost.lastIndexOf('.')) + ".X";

        // substitui elemento do meio por outro X
        // 200.X2.38.X
        // 200.X58.52.X
        // ele vai localizar o primeiro ponto e depois ir� substituir o proximo
        // numero por X
        int ponto = novoHost.indexOf('.');
        StringBuffer hostFinal = new StringBuffer(novoHost);
        hostFinal = hostFinal.deleteCharAt(ponto + 1).insert(ponto + 1, "X");

        host = hostFinal.toString();

    }

    public String getHost() {

        return host;

    }

    public void setHost(String host) {

        this.host = host;

    }

    public Socket getSocket() {

        return socket;

    }

    public void unsetLogadoArray(User tmpUser) {

        logadoArray.remove(tmpUser);

    }

    public ArrayList<User> getLogadoArray() {

        return logadoArray;

    }

    public void setLogadoArray(User tmpUser) {

        logadoArray.add(tmpUser);

    }

    public boolean isLogadoArray(User tmpUser) {

        return logadoArray.contains(tmpUser);

    }

    public boolean isZombie() {

        return zombie;

    }

    public void setZombie(boolean zombie) {

        this.zombie = zombie;

    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}