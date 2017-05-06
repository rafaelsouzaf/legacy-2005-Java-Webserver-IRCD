package chatmania.extra.log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogIRCd {

    public LogIRCd(String arquivo, String comando, String erro) {

        try {

            // se log for gigantesco, entao resume ele
            if (erro.length() > 300)
                erro = erro.substring(300);

            // se log for gigantesco, entao resume ele
            if (comando.length() > 300)
                comando = comando.substring(300);

            Date data = new Date();
            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat horaFormatada = new SimpleDateFormat("kk:mm");

            String titulo = dataFormatada.format(data);
            String horaAtual = horaFormatada.format(data);

            File file = new File("log/" + titulo + "_IRCD.txt");
            RandomAccessFile grava = new RandomAccessFile(file, "rw");

            // envia cursos para ultima linha
            grava.seek(file.length());

            // cria mensagem para grava�ao
            StringBuffer novaLinha = new StringBuffer("\r\n-------------------------------------------\r\n");
            novaLinha.append("File    : ").append(horaAtual).append(" - ").append(arquivo).append("\r\n");
            novaLinha.append("Command : ").append(comando).append("\r\n");
            novaLinha.append("Error   : ").append(erro);

            // grava novos dados
            grava.writeBytes(novaLinha.toString());

            // fecha arquivo
            grava.close();

        } catch (Exception e) {

            new LogIRCd("LogIRCd.java", "LogIRCd()", e + "");

        }

    }

}
