/**
 * tcp chat
 * 
 * Aufgabe 2
 * April 2018
 *  
 * Riccardo Felluga 14330
 * Claudio Spieß 14329
 * 
 */
import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private static boolean connected = false, quit = false;
    private static String username = "";
    private static BackgroundSocket bkgSocket;
    private static Scanner r = new Scanner(System.in);

    public static void main(String[] args) {

        while (!quit) {

            if (!connected) {

                System.out.println("You are not connected to the chat :(");
                System.out.println("Type '\\connect username' to connect");
                System.out.println("Type '\\quit' to exit the program");
                String cmd = r.nextLine();
                switch (cmd) {
                    case "\\quit":
                        System.out.println("See you next time ;)");
                        quit = true;
                        break;
                    default:
                        if (cmd.length() > 9 && cmd.substring(0, cmd.indexOf(' ')).equals("\\connect")) {
                            username = cmd.substring(cmd.indexOf(' ') + 1);

                            bkgSocket = new BackgroundSocket();
                            Thread backgroundSocketThread = new Thread(bkgSocket);
                            backgroundSocketThread.setDaemon(true);
                            backgroundSocketThread.start();

                            connected = true;
                            System.out.println("Type '\\disconnect' to log out");
                            System.out.println("Type '\\quit' to exit the program");
                            System.out.println("Write your message and press enter to send");
                            bkgSocket.sendMessage(username + " has joined the server!");

                        } else {
                            System.out.println("Unknown command sorry...");
                        }
                        break;
                }
            } else {
                //System.out.print(username + ": ");
                String message = r.nextLine();

                switch (message) {
                    case "\\disconnect":
                        bkgSocket.sendMessage(username + " has left the server.");
                        bkgSocket.disconnect();
                        connected = false;
                        break;
                    case "\\quit":
                        System.out.println("See you next time ;)");
                        bkgSocket.sendMessage(username + " has left the server.");
                        quit = true;
                        break;

                    default:
                        bkgSocket.sendMessage(username + ": " +message);
                        break;

                }

            }

        }
    }

    public static void notify(String message){
        if(connected){
            System.out.println(message);
        }
    }

    private static class BackgroundSocket implements Runnable {
        private Socket echoSocket;
        private PrintWriter out;

        public BackgroundSocket(){
            try {
                echoSocket = new Socket("127.0.0.1", 9999);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
            } catch (IOException e) {
                //error initializing the client
            }
        }

        public void sendMessage(String message)
        {
            try
            {
                out.println(message);
                out.flush();
            }catch (Exception ex)
            {

            }
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                String fromServer = "";
                while (((fromServer = in.readLine()) != null) && !fromServer.equals("null")) {
                    String finalServerString = new String(fromServer);
                    try {
                        ChatClient.notify(finalServerString);
                        try {
                            // Open an audio input stream.
                            File soundFile = new File("../alert.wav"); //you could also get the sound file with an URL
                            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                            // Get a sound clip resource.
                            Clip clip = AudioSystem.getClip();
                            // Open audio clip and load samples from the audio input stream.
                            clip.open(audioIn);
                            clip.start();
                        }
                        catch (UnsupportedAudioFileException e) {}
                        catch (IOException e) {}
                        catch (LineUnavailableException e) {}
                    } catch (Exception ex) {}


                }
            } catch (Exception ex) {}
        }

        public void disconnect() {
            try {
                echoSocket.close();
            }catch (Exception ex) {}
        }
    }



}

