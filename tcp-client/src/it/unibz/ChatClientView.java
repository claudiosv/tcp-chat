package it.unibz;/**
 * Created by claudio on 05/03/2018.
 */

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

import javax.sound.sampled.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChatClientView implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    private TextArea chatTextArea;

    private String username;

    private BackgroundSocket backgroundSocket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("TCP Chat");
        dialog.setHeaderText("Please enter your username below.");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            this.username = name;
        });

        textField.setOnKeyPressed(event -> { if(event.getCode() == KeyCode.ENTER) onSendButton(); });
        textField.setFont(Font.font(60));
        chatTextArea.setFont(Font.font(60));
        backgroundSocket = new BackgroundSocket();
        Thread backgroundSocketThread = new Thread(backgroundSocket);
        backgroundSocketThread.setDaemon(true);
        backgroundSocketThread.start();
    }

    @FXML
    public void onSendButton() {

        //chatTextArea.setText(textField.getText());
        if(textField.getText().startsWith("/quit"))
        {
            backgroundSocket.sendMessage(username + " has left the server.");
            backgroundSocket.disconnect();
        }
        else {
            backgroundSocket.sendMessage(username + ": " + textField.getText());
            textField.clear();
        }
    }

    private class BackgroundSocket implements Runnable {
        private Socket echoSocket;
        private PrintWriter out;

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
                echoSocket = new Socket("127.0.0.1", 9999);
                out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                String fromServer = "";
                while (((fromServer = in.readLine()) != null)) {
                    String finalServerString = new String(fromServer);
                    Platform.runLater(() -> {
                        try {
                            chatTextArea.appendText(finalServerString + "\n");
                            try {
                                // Open an audio input stream.
                                File soundFile = new File("alert.wav"); //you could also get the sound file with an URL
                                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                                // Get a sound clip resource.
                                Clip clip = AudioSystem.getClip();
                                // Open audio clip and load samples from the audio input stream.
                                clip.open(audioIn);
                                clip.start();
                            } catch (UnsupportedAudioFileException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (LineUnavailableException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void disconnect() {
            try {
                echoSocket.close();
            }catch (Exception ex)
            {

            }
        }
    }
}