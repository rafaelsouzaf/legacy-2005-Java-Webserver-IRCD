package chatmania.extra.log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogOpers {

    public LogOpers(String ircop, String vitima, String comando) {

        try {

            // se log nao gigantesco, entao resume ele
            if (comando.length() > 300)
                comando = comando.substring(300);

            Date data = new Date();
            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat horaFormatada = new SimpleDateFormat("kk:mm");

            String titulo = dataFormatada.format(data);
            String horaAtual = horaFormatada.format(data);

            File file = new File("log/" + titulo + "_OPER.txt");
            RandomAccessFile grava = new RandomAccessFile(file, "rw");

            // envia cursos para ultima linha
            grava.seek(file.length());

            // cria mensagem para grava�ao
            StringBuffer novaLinha = new StringBuffer("\r\n-------------------------------------------\r\n");
            novaLinha.append("IRCop   : ").append(horaAtual).append(" - ").append(ircop).append("\r\n");
            novaLinha.append("Victim  : ").append(vitima).append("\r\n");
            novaLinha.append("Command : ").append(comando);

            // grava novos dados
            grava.writeBytes(novaLinha.toString());

            // fecha arquivo
            grava.close();

        } catch (Exception e) {

            new LogIRCd("LogOpers.java", "LogOpers()", e + "");

        }

    }

}
