package chatmania;

import chatmania.extra.log.LogIRCd;
import chatmania.extra.sql.SQL;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Kline {

    // key � o IP, value � o Motivo
    private static ConcurrentHashMap<String, String> listaIps = new ConcurrentHashMap<String, String>();

    public Kline() {

        try {

            ResultSet result = SQL.query("select kl_mask, kl_reason, kl_date from kline");
            while (result.next())
                listaIps.put(result.getString("kl_mask"), new Date(result.getLong("kl_date")) + " - " + result.getString("kl_reason"));

        } catch (Exception e) {

            new LogIRCd("Kline.java", "Kline()", e + "");

        }

    }

    public static Iterator<Map.Entry<String, String>> listKLines() {

        return listaIps.entrySet().iterator();

    }

    public static String isKline(String userip) {

        if (listaIps.size() > 0) {

            for (Enumeration<String> enu = listaIps.keys(); enu.hasMoreElements(); ) {

                String sera = enu.nextElement();
                if (matches(sera, userip))
                    return listaIps.get(sera);

            }

        }

        return "";

    }

    public static void deleteKLine(String mask) {

        try {

            if (!mask.equals("")) {

                SQL.delete("delete from kline where kl_mask = '" + SQL.antiInject(mask) + "'");
                listaIps.remove(mask);

            }

        } catch (Exception e) {

            new LogIRCd("Kline.java", "", "" + e + "");

        }

    }

    public static void limpa() {

        try {

            SQL.delete("delete from kline");
            listaIps.clear();

        } catch (Exception e) {

            new LogIRCd("Kline.java", "", "" + e + "");

        }

    }

    public static void writeKLine(String mask, String ircop, String motivo) {

        try {

            if (!mask.equals("")) {

                mask = SQL.antiInject(mask);
                motivo = SQL.antiInject(motivo);

                SQL.insert("INSERT INTO kline (kl_mask, kl_ircop, kl_reason, kl_date) VALUES ('" + mask + "', '" + ircop + "', '" + motivo + "', '" + System.currentTimeMillis() + "')");
                listaIps.put(mask, motivo);

            }

        } catch (Exception e) {

            new LogIRCd("Kline.java", "writeKLine", "" + e + "");

        }

    }

    private static boolean matches(String mask, String nick) {

        int npos;

        for (int i = 0; i < mask.length(); i++) {

            if (mask.charAt(i) != '*') {

                npos = nick.indexOf(mask.charAt(i));
                if (npos == -1)
                    return false;
                else
                    nick = nick.substring(npos);

            }

        }

        return true;

    }

}