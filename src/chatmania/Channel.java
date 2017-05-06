package chatmania;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private String name;
    private String topic = "";
    private String topicOwner;
    private boolean isModerado;
    private boolean isSecret;
    private User fundador;
    private ArrayList<String> banList = new ArrayList<String>(5);
    private ArrayList<String> onjoin = new ArrayList<String>(8);
    private ConcurrentHashMap<User, Integer> members = new ConcurrentHashMap<User, Integer>(50); // key � o tmpUser, value � o status (0, 1+, 2@, 3.)

    public Channel(String name, User fundador) {

        this.name = name;
        this.fundador = fundador;

        // inicializa onjoin
        for (int i = 0; i < 8; i++)
            onjoin.add("");

    }

    public User getFundador() {

        return fundador;

    }

    public String getTopicOwner() {

        return topicOwner;

    }

    public void setTopicOwner(String str) {

        topicOwner = str;

    }

    public String getTopic() {

        return topic;

    }

    public void setTopic(String topic) {

        this.topic = topic;

    }

    public String getName() {

        return name;

    }

    public Enumeration<User> getList() {

        return members.keys();

    }

    public boolean isMember(User tmpUser) {

        return members.containsKey(tmpUser);

    }

    public void setMemberStatus(User tmpUser, int status) {

        if (members.containsKey(tmpUser))
            members.put(tmpUser, status);

    }

    public int getMemberStatus(User tmpUser) {

        return members.get(tmpUser);

    }

    public String getMemberStatusStr(User tmpUser) {

        int status = members.get(tmpUser);

        if (status == 0)
            return "";
        else if (status == 1)
            return "+";
        else if (status == 2)
            return "@";
        else if (status == 3)
            return ".";

        return "";

    }

    public int getMemberSize() {

        return members.size();

    }

    public void addUser(User tmpUser, int status) {

        members.put(tmpUser, status);

    }

    public void removeUser(User tmpUser) {

        members.remove(tmpUser);

    }

    public void addOnjoin(int linha, String frase) {
        onjoin.set(linha, frase);
    }

    public void clearOnjoin() {

        for (int i = 1; i < 8; i++)
            onjoin.set(i, "");

    }

    public ArrayList<String> listOnjoin() {
        return onjoin;
    }

    public boolean isModerado() {
        return isModerado;
    }

    public void setModerado(boolean moderado) {
        isModerado = moderado;
    }

    public boolean isSecret() {
        return isSecret;
    }

    public void setSecret(boolean secret) {
        isSecret = secret;
    }

    public ArrayList<String> getBanList() {
        return banList;
    }

    public void clearBans() {
        banList.clear();
    }

    public void setBan(String ban) {
        banList.add(ban.toLowerCase());
    }

    public void removeBan(String ban) {
        banList.remove(ban.toLowerCase());
    }

    public boolean isBan(String ban) {
        return banList.contains(ban.toLowerCase());
    }

}