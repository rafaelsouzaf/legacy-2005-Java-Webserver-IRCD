package chatmania.ircd;

import chatmania.extra.log.LogIRCd;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Connection implements Runnable {

    public Listener(int porta) {

        try {

            ServerSocket listener = new ServerSocket(porta);

            while (true)
                conexao(listener.accept());

        } catch (BindException e) {

            new LogIRCd("Listener.java", "", "Port " + porta + " is already in use.");

        } catch (Exception e) {

            new LogIRCd("Listener.java", "", "" + e + "");

        }

    }

    public void conexao(Socket server) {

        try {

            Conexao connectionThread = new Conexao(this, server);
            connectionThread.start();

        } catch (Exception e) {

            new LogIRCd("Listener.java", "conexao()", "" + e + "");

        }

    }

    public void run() {

        try {

            Conexao currentThread = (Conexao) Thread.currentThread();
            super.conexao(currentThread.serverSocket);

        } catch (IOException e) {

            new LogIRCd("Listener.java", "run()", "" + e + "");

        }

    }

    private static class Conexao extends Thread {

        public Socket serverSocket;

        public Conexao(Runnable serverObject, Socket svrSock) {

            super(serverObject);
            serverSocket = svrSock;

        }

    }

}