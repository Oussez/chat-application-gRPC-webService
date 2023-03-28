package com.Oussez.client;

import com.Oussez.stubs.ChatServiceGrpc;
import com.Oussez.stubs.ChatSystem;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.lang.Thread;

public class ChatClient {
    private static int onlineUsers;
    private int id;
    private static File file = new File("src/main/java/com/Oussez/client/onlineUsers.txt");

    /**                               Client Streaming MODEL          1:30           **/

    public ChatClient() throws IOException {
        /**Ecrire 0 sur le fichier onlineUsers.txt**/
        // Lire la valeur de onlineUsers à partir du fichier
        onlineUsers = Integer.parseInt(new String(Files.readAllBytes(Paths.get(file.getPath()))));

        // Incrémenter onlineUsers et l'écrire dans le fichier

            onlineUsers++;
            Files.write(Paths.get(file.getPath()), Integer.toString(onlineUsers).getBytes());

        // Définir l'id de l'instance actuelle
        id = onlineUsers;
    }

    public int  getOnlineUsers() {
           return onlineUsers++;

    }
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient();
        System.out.println(">> UserID "+client.id+" | onlineUsers : "+onlineUsers);
        //Declarer un canal de communication
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9999)
                .usePlaintext()
                .build();
        Scanner scanf = new Scanner(System.in);
        System.out.println(">> Please, write your username:");
        String userName = scanf.nextLine();

        //Initialisation de msg request en utilsiant le stub:
        ChatServiceGrpc.ChatServiceStub asyncStub = ChatServiceGrpc.newStub(managedChannel);

        //la partie de streaming des reponses de serveur
        StreamObserver<ChatSystem.requestMsg> responseObservable =
                asyncStub.chatStream(new StreamObserver<ChatSystem.response>() {
                    //traitement sur les réponses envoyées par le serveur en mode streaming

                    @Override
                    public void onNext(ChatSystem.response response) {
                        System.out.println("\n>> [Server]: "+response.getResponseToRequest());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println(">> Server ERROR : "+throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println(">> Server has ended the communication");
                    }
                });

        //la partie de streaming de requete d'user via un nouveau thread 'threadChatting'


            new Thread(()-> {
                int conteur = 0;
                ChatSystem.requestMsg requestMsg;
                while (!Thread.currentThread().isInterrupted()) {
                    String request;
                    do {
                        System.out.print(userName + ">> ");
                        request = scanf.nextLine();

                    }
                    while (request.isEmpty());

                    requestMsg = ChatSystem.requestMsg.newBuilder()
                            .setUserName(userName)
                            .setRequest(request)
                            .build();

                    responseObservable.onNext(requestMsg);
                    conteur++;

                   // Pause de 0.1 secondes
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                    //partie d'arret le thread de chatting : après chaque cycle de 5 message
                    if(conteur%5==0) {
                        System.out.print("[EXIT]--> if you want to stop chatting , write 'yes': ");
                        String answer = scanf.nextLine();
                        if (answer.equals("yes")) {
                            responseObservable.onCompleted();
                            try {
                                Thread.sleep(100); //pour attendre la terminaison de  l'appel de {responseObservable.onCompleted();} sinon le thread va pas attendre et continue l'execution des lignes suivantes
                                Thread.currentThread().interrupt(); //arreter le threadChatting


                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                Files.write(Paths.get(file.getPath()), Integer.toString(onlineUsers - 1).getBytes());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                }
            }).start();



            System.in.read(); //permet de bloquer le main-thread jusqu"à qu'il lit la méthode Next() ou Complete()

    }


}
