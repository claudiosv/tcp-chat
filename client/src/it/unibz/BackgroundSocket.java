package it.unibz;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class BackgroundSocket implements Runnable {
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
