package chatmania.extra.log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogSQL {

    public LogSQL(String arquivo, String metodo, String erro) {

        try {

            Date data = new Date();
            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat horaFormatada = new SimpleDateFormat("kk:mm");

            String titulo = dataFormatada.format(data);
            String horaAtual = horaFormatada.format(data);

            File file = new File("log/" + titulo + "_SQL.txt");
            RandomAccessFile grava = new RandomAccessFile(file, "rw");

            // envia cursos para ultima linha
            grava.seek(file.length());

            // cria mensagem para grava�ao
            StringBuffer novaLinha = new StringBuffer("\r\n-------------------------------------------\r\n");
            novaLinha.append("File    : ").append(horaAtual).append(" - ").append(arquivo).append("\r\n");
            novaLinha.append("Method : ").append(metodo).append("\r\n");
            novaLinha.append("Error   : ").append(erro);

            // grava novos dados
            grava.writeBytes(novaLinha.toString());

            // fecha arquivo
            grava.close();

        } catch (Exception e) {

            new LogSQL("LogSQL.java", "LogSQL()", e + "");

        }

    }

}
