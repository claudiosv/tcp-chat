package it.unibz;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServer {

    public List<TestThread> connectedClients;

    public ChatServer()
    {
        connectedClients = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            while(true)
            {
                TestThread thread = new TestThread(this, serverSocket.accept());
                connectedClients.add(thread);
                Thread backgroundSocketThread = new Thread(thread);
                backgroundSocketThread.setDaemon(true);
                backgroundSocketThread.start();

            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void broadcastMessage(TestThread caller, String message)
    {
        for(TestThread client : connectedClients)
        {
            client.sendMessage(message);
        }
    }

    public void clientDisconnected(TestThread client)
    {
        connectedClients.remove(client);
    }

    public static void main(String[] args) {
	// write your code here
        new ChatServer();

    }

    private class TestThread implements Runnable
    {

        public Socket clientSocket;
        public ChatServer server;

        public TestThread(ChatServer server, Socket clientSocket)
        {
            this.server = server;
            this.clientSocket = clientSocket;
        }

        public void sendMessage(String message)
        {
            try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream())));
            out.println(message);
            out.flush();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        public void readMessage()
        {
            try {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                while(clientSocket.isConnected())
                {
                    String lineReceived = in.readLine();
                    System.out.println(lineReceived);
                    server.broadcastMessage(this, lineReceived);
                }
                if(!clientSocket.isConnected()) server.clientDisconnected(this);
            } catch (SocketException ex)
            {
                ex.printStackTrace();
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            readMessage();
        }
    }
}

