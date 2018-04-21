package it.unibz;

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
}


//        if(args.length > 0 && !args[0].isEmpty()){
//            String cmd = args[0];
//            switch (cmd){
//                case "gui" :
//                    gui = true;
//                    break;
//                default:
//                    System.out.println("type 'gui' for the visual version of the program");
//                    break;
//            }
//        }
//
//        launch(args);

////chatTextArea.setText(textField.getText());
//            if(textField.getText().startsWith("/quit"))
//    {
//        backgroundSocket.sendMessage(username + " has left the server.");
//        backgroundSocket.disconnect();
//    }
//            else {
//        backgroundSocket.sendMessage(username + ": " + textField.getText());
//        textField.clear();
//    }

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("ChatClientScene.fxml"));
//
//        primaryStage.setTitle("Chat Application");
//        primaryStage.setScene(new Scene(root, 300, 275));
//
//        if (gui) {
//            primaryStage.show();
//        }
//
//    }


//    public class ChatClientView implements Initializable {
//
//        @FXML
//        private TextField textField;
//        @FXML
//        private TextArea chatTextArea;
//
//        private String username;
//
//        private BackgroundSocket backgroundSocket;
//
//        @Override
//        public void initialize(URL location, ResourceBundle resources) {
//            TextInputDialog dialog = new TextInputDialog();
//
//            dialog.setTitle("TCP Chat");
//            dialog.setHeaderText("Please enter your username below.");
//            dialog.setContentText("Username:");
//
//            Optional<String> result = dialog.showAndWait();
//
//            result.ifPresent(name -> {
//                this.username = name;
//            });
//
//            textField.setOnKeyPressed(event -> { if(event.getCode() == KeyCode.ENTER) onSendButton(); });
//            textField.setFont(Font.font(60));
//            chatTextArea.setFont(Font.font(60));
//
//            backgroundSocket = new BackgroundSocket();
//            Thread backgroundSocketThread = new Thread(backgroundSocket);
//            backgroundSocketThread.setDaemon(true);
//            backgroundSocketThread.start();
//        }
//
//        @FXML
//        public void onSendButton() {
//
//            //chatTextArea.setText(textField.getText());
//            if(textField.getText().startsWith("/quit"))
//            {
//                backgroundSocket.sendMessage(username + " has left the server.");
//                backgroundSocket.disconnect();
//            }
//            else {
//                backgroundSocket.sendMessage(username + ": " + textField.getText());
//                textField.clear();
//            }
//        }
//    }
//}
