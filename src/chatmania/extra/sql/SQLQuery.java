package chatmania.extra.sql;

import chatmania.Server;
import chatmania.extra.Util;
import chatmania.extra.log.LogSQL;
import chatmania.ircd.Config;
import chatmania.ircd.services.chanserv.ChanServConfig;
import chatmania.ircd.services.nickserv.NickServConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SQLQuery extends SQL {

    public static void updateAll() {

        try {

            update("update server set"
                    + " network = '" + Config.NETWORK + "',"
                    + " admin = '" + Config.ADMIN + "',"
                    + " email = '" + Config.EMAIL + "',"
                    + " link = '" + Config.URL + "',"
                    + " uptime = '" + Server.getUptime() + "',"
                    + " users_total = '" + Server.getUserSize() + "',"
                    + " users_registered = '" + countNicks() + "',"
                    + " channels_online = '" + Server.getChannelSize() + "',"
                    + " channels_registered = '" + countChannels() + "'"
            );

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "updateAll()", e + "");

        }

    }

    public static boolean isIRCOP(String nick) {

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_nick = '" + antiInject(nick) + "' AND ns_ircop=1");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "isIRCOP()", e + "");

        }

        return false;

    }

    public static boolean isOper(String nick) {

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_nick='" + antiInject(nick) + "' AND (ns_ircop=1 OR ns_admin=1)");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "isOper()", e + "");

        }

        return false;

    }

    public static void setOper(String nick, String nivel) {

        try {

            nivel = antiInject(nivel.toUpperCase());
            if (nivel.equals("IRCOP"))
                update("update nickserv set ns_ircop=1 where ns_nick = '" + antiInject(nick) + "'");
            else if (nivel.equals("ADMIN"))
                update("update nickserv set ns_admin=1 where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "setOper()", e + "");

        }

    }

    public static void removeOper(String nick) {

        try {

            update("update nickserv set ns_ircop=0, ns_admin=0 where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "removeOper()", e + "");

        }

    }

    public static StringBuffer getAllWithNivel() {

        StringBuffer ircops = new StringBuffer("");

        try {

            ResultSet result = query("select ns_nick, ns_ircop, ns_admin from nickserv where ns_ircop=1 OR ns_admin=1 order by ns_ircop, ns_nick");
            while (result.next()) {

                if (result.getInt("ns_ircop") == 1)
                    ircops.append("(IRCop) ").append(result.getString("ns_nick")).append("<br>");
                else
                    ircops.append("(ADMIN) ").append(result.getString("ns_nick")).append("<br>");
            }

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "getAllWithNivel()", e + "");

        }

        return ircops;

    }

    public static ArrayList<String> getAllEmails() {

        ArrayList<String> emails = new ArrayList<String>();

        try {

            ResultSet result = query("select ns_email from nickserv where ns_ircop=1 OR ns_admin=1");
            while (result.next())
                emails.add(result.getString("ns_email"));

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "getAllEmails()", e + "");

        }

        return emails;

    }

    public static ArrayList<String> getIRCops() {

        ArrayList<String> ircops = new ArrayList<String>();

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_ircop=1");
            while (result.next())
                ircops.add(result.getString("ns_nick"));

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "getIRCops()", e + "");

        }

        return ircops;

    }

    public static ArrayList<String> getForbids() {

        ArrayList<String> forbids = new ArrayList<String>();

        try {

            ResultSet result = query("select forbid from forbids order by forbid");
            while (result.next())
                forbids.add(result.getString("forbid"));

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "getForbids()", "" + e + "");

        }

        return forbids;

    }

    public static void setForbid(String canal) {

        try {

            insert("INSERT INTO forbids (forbid) VALUES ('" + antiInject(canal) + "')");

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "setForbid()", "" + e + "");

        }

    }

    public static void removeForbid(String canal) {

        try {

            update("delete from forbids where forbid='" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLOperServ.java", "removeForbid()", "" + e + "");

        }

    }

    public static String getIRcopOrAdmin(String nick) {

        try {

            ResultSet result = query("select ns_ircop, ns_admin from nickserv where ns_nick = '" + antiInject(nick) + "' and (ns_admin=1 OR ns_ircop=1)");
            if (result.next()) {

                if (result.getString("ns_admin").equals("1"))
                    return "ADMIN";
                else if (result.getString("ns_ircop").equals("1"))
                    return "IRCOP";
            }

        } catch (Exception e) {

            new LogSQL("SQLNickServ.java", "getIRcopOrAdmin()", e + "");

        }

        return "";

    }

    public static void setNews(String nick, int status) {

        try {

            update("update nickserv set ns_news = '" + status + "' where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setNews()", e + "");

        }

    }

    public static void setEmail(String nick, String email) {

        try {

            update("update nickserv set ns_email = '" + antiInject(email) + "' where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setEmail()", e + "");

        }

    }

    public static void setPassword(String nick, String pass) {

        try {

            update("update nickserv set ns_password = '" + Util.encrypt(pass) + "' where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setPassword()", e + "");

        }

    }

    public static void setLast(String nick) {

        try {

            update("update nickserv set ns_last = " + System.currentTimeMillis() + " where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setLast()", "" + e + "");

        }

    }

    public static void setLang(String nick, String lang) {

        try {

            update("update nickserv set ns_lang = '" + antiInject(lang) + "' where ns_nick = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setLang()", "" + e + "");

        }

    }

    public static void setAjoin(String nick, String canal) {

        try {

            insert("INSERT INTO nickserv_ajoin (nsa_nick, nsa_channel) VALUES ('" + antiInject(nick) + "', '" + antiInject(canal) + "')");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "setAjoin()", "" + e + "");

        }

    }

    public static boolean isNews(String nick) {

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_nick = '" + antiInject(nick) + "' and ns_news = '0'");
            if (result.next())
                return true;

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isNews()", e + "");

        }

        return false;

    }

    public static boolean isRegistered(String nick) {

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_nick = '" + antiInject(nick) + "'");
            if (result.next())
                return true;

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isRegistered()", e + "");

        }

        return false;

    }

    public static boolean isNickCorrect(String nick, String senha) {

        try {

            ResultSet result = query("select ns_nick from nickserv where ns_nick = '" + antiInject(nick) + "' and ns_password = '" + Util.encrypt(senha) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isNickCorrect()", "" + e + "");

        }

        return false;

    }

    public static boolean isForbid(String nickOrChannel) {

        try {

            ResultSet result = query("select forbid from forbids where forbid = '" + antiInject(nickOrChannel) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isForbid()", "" + e + "");

        }

        return false;

    }

    public static boolean isAjoin(String nick, String canal) {

        try {

            ResultSet result = query("select nsa_channel from nickserv_ajoin where nsa_nick = '" + antiInject(nick) + "' and nsa_channel = '" + antiInject(canal) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isAjoin()", "" + e + "");

        }

        return false;

    }

    public static boolean isExistEmail(String email) {

        try {

            ResultSet result = query("select ns_email from nickserv where ns_email = '" + antiInject(email) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isExistEmail()", "" + e + "");

        }

        return false;

    }

    public static String getEmail(String nick) {

        try {

            ResultSet result = query("select ns_email from nickserv where ns_nick = '" + antiInject(nick) + "'");
            if (result.next())
                return result.getString("ns_email");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getEmail()", e + "");

        }

        return "";

    }

    public static String getPassword(String nick) {

        try {

            ResultSet result = query("select ns_password from nickserv where ns_nick = '" + antiInject(nick) + "'");
            if (result.next())
                return Util.decrypt(result.getString("ns_password"));

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getPassword()", "" + e + "");

        }

        return "";

    }

    public static String getLang(String nick) {

        try {

            ResultSet result = query("select ns_lang from nickserv where ns_nick = '" + antiInject(nick) + "' AND ns_nick != null");
            if (result.next())
                return result.getString("ns_lang");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getLang()", e + "");

        }

        return "";

    }

    public static ArrayList<String> getAjoin(String nick) {

        ArrayList<String> ajoin = new ArrayList<String>(5);

        try {

            ResultSet result = query("select nsa_channel from nickserv_ajoin where nsa_nick = '" + antiInject(nick) + "'");
            while (result.next()) {

                ajoin.add(result.getString("nsa_channel"));

            }

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getAjoin()", "" + e + "");

        }

        return ajoin;

    }

    public static ArrayList<String> getAll() {

        ArrayList<String> usuarios = new ArrayList<String>();

        try {

            ResultSet result = query("select ns_nick from nickserv");
            while (result.next())
                usuarios.add(result.getString("ns_nick"));

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getAll()", "" + e + "");

        }

        return usuarios;

    }

    public static Map<String, String> getInfoFor(String nick) {

        Map<String, String> info = new HashMap<String, String>();

        try {

            ResultSet result = query("select * from nickserv where ns_nick = '" + antiInject(nick) + "'");
            if (result.next()) {

                info.put("ns_nick", result.getString("ns_nick"));
                info.put("ns_password", Util.decrypt(result.getString("ns_password")));
                info.put("ns_date", new Date(result.getLong("ns_date")) + "");
                info.put("ns_email", result.getString("ns_email"));
                info.put("ns_news", result.getString("ns_news"));
                info.put("ns_lang", result.getString("ns_lang"));
                info.put("ns_news", result.getString("ns_news"));
                info.put("ns_last", result.getString("ns_last"));

            }

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "getInfoFor()", "" + e + "");

        }

        return info;

    }

    public static void removeNick(String nick) {

        try {

            delete("delete from nickserv where ns_nick = '" + antiInject(nick) + "'");
            delete("delete from nickserv_ajoin where nsa_nick = '" + antiInject(nick) + "'");
            delete("delete from chanserv where cs_founder = '" + antiInject(nick) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "removeNick()", "" + e + "");

        }

    }

    public static void removeAjoin(String nick, String canal) {

        try {

            delete("delete from nickserv_ajoin where nsa_nick = '" + antiInject(nick) + "' and nsa_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "removeAjoin()", e + "");

        }

    }

    public static String registerNick(String nick, String email) {

        String senha = "";

        try {

            nick = antiInject(nick);
            email = antiInject(email);

            senha = nick + (int) (Math.random() * 9999);
            String senhaCriptografada = Util.encrypt(senha);

            insert("INSERT INTO nickserv (ns_nick, ns_password, ns_email, ns_date, ns_last) " +
                    "VALUES ('" + nick + "', '" + senhaCriptografada + "', '" + email + "', '" + System.currentTimeMillis() + "', '" + System.currentTimeMillis() + "')");

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "registerNick()", "" + e + "");

        }

        return senha;

    }

    public static int countNicks() {

        int numNicks = 0;

        try {

            ResultSet result = query("select ns_nick from nickserv");
            while (result.next())
                numNicks++;

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "countNicks()", e + "");

        }

        return numNicks;

    }

    public static void expire() {

        try {

            ResultSet result = query("select ns_nick, ns_last from nickserv");
            while (result.next()) {

                // pega a ultima data identificada e soma com o numero de dias
                // permitido pelo nickserv antes de expirar
                long ultima = result.getLong("ns_last") + (NickServConfig.EXPIRE * 86400000);
                String nick = result.getString("ns_nick");

                if (ultima < System.currentTimeMillis())
                    removeNick(nick);

            }

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "expire()", "" + e + "");

        }

    }

    public static boolean isAjoinLimitOk(String nick) {

        int canais = 0;

        try {

            ResultSet result = query("select nsa_channel from nickserv_ajoin where nsa_nick = '" + antiInject(nick) + "'");
            while (result.next())
                canais++;

        } catch (Exception e) {

            new LogSQL("SQLQuery.java", "isAjoinLimitOk()", "" + e + "");

        }

        return canais < NickServConfig.AJOINLIMIT;

    }

    public static boolean isFounder(String canal, String nick) {

        try {

            ResultSet result = query("select cs_channel from chanserv where cs_channel = '" + antiInject(canal) + "' and cs_founder = '" + antiInject(nick) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isFounder()", e + "");

        }

        return false;

    }

    public static boolean isHost(String canal, String nick) {

        try {

            ResultSet result = query("select csh_nick from chanserv_host where csh_channel = '" + antiInject(canal) + "' and csh_nick = '" + antiInject(nick) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isHost()", e + "");

        }

        return false;

    }

    public static boolean isOwner(String canal, String nick) {

        try {

            ResultSet result = query("select cso_nick from chanserv_owner where cso_channel = '" + antiInject(canal) + "' and cso_nick = '" + antiInject(nick) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isOwner()", e + "");

        }

        return false;

    }

    public static boolean isHostLimitOk(String canal) {

        int hosts = 0;

        try {

            ResultSet result = query("select csh_nick from chanserv_host where csh_channel = '" + antiInject(canal) + "'");
            while (result.next())
                hosts++;

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isHostLimitOk()", "" + e + "");

        }

        return hosts < ChanServConfig.LIMITHOST;

    }

    public static boolean isOwnerLimitOk(String canal) {

        int owners = 0;

        try {

            ResultSet result = query("select cso_nick from chanserv_owner where cso_channel = '" + antiInject(canal) + "'");
            while (result.next())
                owners++;

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isOwnerLimitOk()", "" + e + "");

        }

        return owners < ChanServConfig.LIMITOWNER;

    }

    public static void setSucessor(String canal, String sucessor) {

        try {

            update("update chanserv set cs_sucessor = '" + antiInject(sucessor) + "' where cs_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setSucessor()", "" + e + "");

        }

    }

    public static void setFounder(String canal, String nick) {

        try {

            update("update chanserv set cs_founder = '" + antiInject(nick) + "' where cs_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setFounder()", "" + e + "");

        }

    }

    public static void setOwner(String canal, String nick) {

        try {

            insert("INSERT INTO chanserv_owner (cso_channel, cso_nick)" +
                    "VALUES ('" + antiInject(canal) + "', '" + antiInject(nick) + "')");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setOwner()", "" + e + "");

        }

    }

    public static void setHost(String canal, String nick) {

        try {

            insert("INSERT INTO chanserv_host (csh_channel, csh_nick)" +
                    "VALUES ('" + antiInject(canal) + "', '" + antiInject(nick) + "')");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setHost()", "" + e + "");

        }

    }

    public static void removeChannel(String canal) {

        try {

            delete("delete from chanserv where cs_channel = '" + antiInject(canal) + "'");
            delete("delete from chanserv_host where csh_channel = '" + antiInject(canal) + "'");
            delete("delete from chanserv_owner where cso_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "removeChannel()", "" + e + "");

        }

    }

    public static void registerChannel(String channel, String founder, String pass) {

        try {

            insert("INSERT INTO ChanServ (cs_channel, cs_founder, cs_date, cs_password, cs_last)" +
                    "VALUES ('" + antiInject(channel) + "', '" + antiInject(founder) + "', '" + System.currentTimeMillis() + "', '" + Util.encrypt(pass) + "', '" + System.currentTimeMillis() + "')");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "registerChannel()", "" + e + "");

        }

    }

    public static int countChannels() {

        int numNicks = 0;

        try {

            ResultSet result = query("select cs_channel from chanserv");
            while (result.next())
                numNicks++;

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "countChannels()", e + "");

        }

        return numNicks;

    }

    public static int getIntChannelToMore(String nick) {

        int numCanais = 0;

        try {

            ResultSet result = query("select cs_channel from chanserv where cs_founder = '" + antiInject(nick) + "'");
            while (result.next())
                numCanais++;

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getIntChannelToMore()", "" + e + "");

        }

        return numCanais;

    }

    public static String getStringChannelToMore(String nick) {

        StringBuffer channels = new StringBuffer();

        try {

            ResultSet result = query("select cs_channel from chanserv where cs_founder = '" + antiInject(nick) + "'");
            while (result.next())
                channels.append(result.getString("cs_channel")).append(" ");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getStringChannelToMore()", "" + e + "");

        }

        return channels.toString();

    }

    public static String getFounder(String canal) {

        try {

            ResultSet result = query("select cs_founder from chanserv where cs_channel = '" + antiInject(canal) + "'");
            if (result.next())
                return result.getString("cs_founder");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getFounder()", "" + e + "");

        }

        return "";

    }

    public static String getSucessor(String canal) {

        try {

            ResultSet result = query("select cs_sucessor from chanserv where cs_channel = '" + antiInject(canal) + "'");
            if (result.next())
                return result.getString("cs_sucessor");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getSucessor()", "" + e + "");

        }

        return "";

    }

    public static ArrayList<String> getOwners(String canal) {

        ArrayList<String> owners = new ArrayList<String>();

        try {

            ResultSet result = query("select cso_nick from chanserv_owner where cso_channel = '" + antiInject(canal) + "'");
            while (result.next())
                owners.add(result.getString("cso_nick"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getOwners()", "" + e + "");

        }

        return owners;

    }

    public static ArrayList<String> getHosts(String canal) {

        ArrayList<String> hosts = new ArrayList<String>();

        try {

            ResultSet result = query("select csh_nick from chanserv_host where csh_channel = '" + antiInject(canal) + "'");
            while (result.next())
                hosts.add(result.getString("csh_nick"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getHosts()", "" + e + "");

        }

        return hosts;

    }

    public static ArrayList<String> getFounderChannels(String nick) {

        ArrayList<String> canais = new ArrayList<String>();

        try {

            ResultSet result = query("select cs_channel from chanserv where cs_founder = '" + antiInject(nick) + "'");
            while (result.next())
                canais.add(result.getString("cs_channel"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getFounderChannels()", "" + e + "");

        }

        return canais;

    }

    public static ArrayList<String> getOwnerChannels(String nick) {

        ArrayList<String> canais = new ArrayList<String>();

        try {

            ResultSet result = query("select cso_channel from chanserv_owner where cso_nick = '" + antiInject(nick) + "'");
            while (result.next())
                canais.add(result.getString("cso_channel"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getOwnerChannels()", "" + e + "");

        }

        return canais;

    }

    public static ArrayList<String> getHostChannels(String nick) {

        ArrayList<String> canais = new ArrayList<String>();

        try {

            ResultSet result = query("select csh_channel from chanserv_host where csh_nick = '" + antiInject(nick) + "'");
            while (result.next())
                canais.add(result.getString("csh_channel"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getHostChannels()", "" + e + "");

        }

        return canais;

    }

    public static void removeOwner(String canal, String nick) {

        try {

            delete("delete from chanserv_owner where cso_channel = '" + canal + "' AND cso_nick = '" + nick + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "removeOwner()", "" + e + "");

        }

    }

    public static void removeHost(String canal, String nick) {

        try {

            delete("delete from chanserv_host where csh_channel = '" + canal + "' AND csh_nick = '" + nick + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "removeHost()", "" + e + "");

        }

    }

    public static void removeChannels() {

        try {

            delete("delete from channels");

        } catch (Exception e) {

            new LogSQL("SQLChannels.java", "removeChannels()", e.toString());

        }

    }

    public static void setChannel(String canal, int users, String webchat, String topico) {

        try {

            insert("INSERT INTO channels (channel, users, topic, webchat, date) VALUES ('" + antiInject(canal) + "', '" + users + "', '" + topico + "', '" + webchat + "', '" + System.currentTimeMillis() + "')");

        } catch (Exception e) {

            new LogSQL("SQLChannels.java", "setChannel()", e + "");

        }

    }

    public static boolean isChannelRegistered(String canal) throws SQLException {

        ResultSet result = query("select cs_channel from chanserv where cs_channel = '" + antiInject(canal) + "'");
        return result.next();

    }

    public static void setChannelPassword(String canal, String senha) {

        try {

            update("update chanserv set cs_password = '" + Util.encrypt(senha) + "' where cs_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setChannelPassword()", e + "");

        }

    }

    public static boolean isChannelCorrect(String canal, String senha) {

        try {

            ResultSet result = query("select cs_channel from chanserv where cs_channel = '" + antiInject(canal) + "' and cs_password = '" + Util.encrypt(senha) + "'");
            return result.next();

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "isChannelCorrect()", "" + e + "");

        }

        return false;

    }

    public static void setChannelLast(String canal) {

        try {

            update("update chanserv set cs_last = '" + System.currentTimeMillis() + "' where cs_channel = '" + antiInject(canal) + "'");

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "setChannelLast()", "" + e + "");

        }

    }

    public static Map<String, String> getChannelInfoFor(String canal) {

        Map<String, String> info = new HashMap<String, String>();

        try {

            ResultSet result = query("select * from chanserv where cs_channel = '" + antiInject(canal) + "'");
            while (result.next()) {

                info.put("cs_channel", result.getString("cs_channel"));
                info.put("cs_sucessor", result.getString("cs_sucessor"));
                info.put("cs_founder", result.getString("cs_founder"));
                info.put("cs_password", Util.decrypt(result.getString("cs_password")));
                info.put("cs_date", new Date(result.getLong("cs_date")) + "");

            }

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getChannelInfoFor()", "" + e + "");

        }

        return info;

    }

    public static String getChannelPassword(String canal) {

        try {

            ResultSet result = query("select cs_password from chanserv where cs_channel = '" + antiInject(canal) + "'");
            if (result.next())
                return Util.encrypt(result.getString("cs_password"));

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "getChannelPassword()", "" + e + "");

        }

        return "";

    }

    public static void channelExpire() {

        try {

            ResultSet result = query("select cs_channel, cs_last from chanserv");
            while (result.next()) {

                // pega a ultima data identificada e soma com o numero de dias
                // permitido pelo chanserv antes de expirar
                long ultima = result.getLong("cs_last") + (ChanServConfig.EXPIRE * 86400000);
                String canal = result.getString("cs_channel");

                if (ultima < System.currentTimeMillis()) {

                    // senao deleta o canal
                    removeChannel(canal);

                }

            }

        } catch (Exception e) {

            new LogSQL("SQLChanServ.java", "channelExpire()", "" + e + "");

        }

    }

}
