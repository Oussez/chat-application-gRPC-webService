package com.Oussez;

import com.Oussez.service.ChatService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ServerGRPC {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9999)
                .addService(new ChatService()) //ClassXService is the class that defines the methods included in fileX.proto
                .build();

        server.start();
        System.out.println(">> SevrerGRPC is ON ....");
        server.awaitTermination(); //permet de laisser le thread figé après l'execution de cette instruction pour que
        //le serveur reste toujours actif !
    }
}