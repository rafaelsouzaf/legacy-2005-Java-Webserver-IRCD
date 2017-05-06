package chatmania.extra.sql;

import chatmania.extra.log.LogSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQL {

    private static final String proibidos[] = {"=", "!", "'", "\\", "/", "`", ";", "<", ">", "%"};
    private static Connection con = null;
    private static Statement stmt = null;

    public SQL() {

        conecta(true);

    }

    public static void conecta(boolean log) {

        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://" + SQLConfig.HOST + ":" + SQLConfig.PORT + "/" + SQLConfig.DB, SQLConfig.USER, SQLConfig.PASS);
            stmt = con.createStatement();

        } catch (Exception e) {

            if (log) {
                new LogSQL("SQL.java", "conecta()", "" + e + "");
                System.out.println("Erro na conexao com o SQL. Verifique se o seu banco de dados est� funcionando" +
                        " corretamente ou edite o arquivo sql.properties com os dados corretos de sua conexao.\r\n");
            }

        }

    }

    public static ResultSet query(String sql) {

        try {

            if (con.isClosed())
                conecta(false);

            return stmt.executeQuery(sql);

        } catch (Exception e) {

            new LogSQL("SQL.java", "query(" + sql + ")", "" + e);

        }

        return null;

    }

    public static void insert(String sql) {

        try {

            stmt.executeUpdate(sql);

        } catch (Exception e) {

            new LogSQL("SQL.java", "insert(" + sql + ")", "" + e);

        }

    }

    public static void update(String sql) {

        try {

            stmt.executeUpdate(sql);

        } catch (Exception e) {

            new LogSQL("SQL.java", "update(" + sql + ")", "" + e);

        }

    }

    public static void delete(String sql) {

        try {

            stmt.executeUpdate(sql);

        } catch (Exception e) {

            new LogSQL("SQL.java", "delete(" + sql + ")", "" + e);

        }

    }

    public static String antiInject(String query) {

        // se query conter alguma palavra proibida
        for (String separa : proibidos) {

            if (query.contains(separa))
                query = query.replace(separa, "");

        }

        return query;

    }

}