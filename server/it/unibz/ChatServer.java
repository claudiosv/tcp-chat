package it.unibz;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatServer {

    public List<ServerThread> connectedClients;
    protected ServerSocket serverSocket;
    public ChatServer()
    {
        connectedClients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket();
            //biding the TCP socket to the address 0.0.0.0 at the port 9999
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("0.0.0.0"), 9999));
            while(true)
            {   
                //creates a new instance of the server 
                ServerThread thread = new ServerThread(this, serverSocket.accept());
                connectedClients.add(thread);
                Thread backgroundSocketThread = new Thread(thread);
                backgroundSocketThread.setDaemon(true);
                backgroundSocketThread.start();

            }
        }
        catch (Exception ex){
            //ex.printStackTrace();
        }
    }
    /**
     * Method used to spread the message towards all the clients
     * @param caller the client thread
     * @param message the message that has to be broadcasted
     */
    public void broadcastMessage(ServerThread caller, String message)
    {
        for(ServerThread client : connectedClients)
        {
            client.sendMessage(message);
        }
    }
    /**
     * Removes the client from the clients' list
     * @param client the client to remove
     */
    public void clientDisconnected(ServerThread client)
    {
        connectedClients.remove(client);
    }

    public static void main(String[] args) {
        //starts by creating a new instance of ChatServer
        new ChatServer(); 
    }

    private class ServerThread implements Runnable
    {

        public Socket clientSocket;
        public ChatServer server;

        public ServerThread(ChatServer server, Socket clientSocket)
        {
            this.server = server;
            this.clientSocket = clientSocket;
        }
        /**
         * Sends the message out to the client socket
         * @param message the message to send 
         */
        public void sendMessage(String message)
        {
            try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream())));
            out.println(message);
            out.flush();
            } catch (Exception ex)
            {}
        }
        /**
         * Reads the message at the input of the client socket
         */
        public void readMessage()
        {
            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while(clientSocket.isConnected() && clientSocket.isBound() && !clientSocket.isClosed())
                {
                    String lineReceived = in.readLine();
                    /*If the server recieves null than the connection with that client is closed
                     so we remove the client from the list and close its socket*/
                    if(lineReceived != null){
                        System.out.println(lineReceived);
                    }else{
                        server.clientDisconnected(this);//remove the client from the list 
                        clientSocket.close(); //close its socket
                    }
                    server.broadcastMessage(this, lineReceived);
                }
            } catch (SocketException ex)
            {
                //ex.printStackTrace();
            } catch (Exception ex)
            {
                //ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            readMessage();
        }
    }
}

