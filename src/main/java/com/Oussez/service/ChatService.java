package com.Oussez.service;

import com.Oussez.stubs.ChatServiceGrpc;
import com.Oussez.stubs.ChatSystem;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {
    private List<StreamObserver<ChatSystem.response>> listUsers = new ArrayList<>();

    /** Bi-Directional Stream Model**/
    @Override
    public StreamObserver<ChatSystem.requestMsg> chatStream(StreamObserver<ChatSystem.response> responseObserver) {
        if(!listUsers.contains(responseObserver)){
            listUsers.add(responseObserver);
            System.out.println(">> listUSERS: "+listUsers.size());
            //Partie de notification des autres users qu'un nouveau user a joint la discussion
            for(StreamObserver<ChatSystem.response> userObservable : listUsers){
                if(!userObservable.equals(responseObserver)) {
                    ChatSystem.response response1 = ChatSystem.response.newBuilder()
                            .setResponseToRequest("** New user has entered the chat | ONLINE USERS : "+listUsers.size())
                            .build();
                    userObservable.onNext(response1);
                }
            }
        }
        return new StreamObserver<ChatSystem.requestMsg>() {
                List<ChatSystem.requestMsg> listRequest = new ArrayList<>();
            @Override
            public void onNext(ChatSystem.requestMsg requestMsg) {
                listRequest.add(requestMsg);
                /** Partie 1 : envoyer une réponse à un user qui vient d'envoyer un msg
                ChatSystem.response response = ChatSystem.response.newBuilder()
                        .setResponseToRequest(">> Your request is "+requestMsg.getRequest())
                        .build();
                responseObserver.onNext(response);**/
                //Partie 2:  de broadCast de msg aux autres users connectés:
                for(StreamObserver<ChatSystem.response> userObservable : listUsers){
                    if(!userObservable.equals(responseObserver)) {
                        ChatSystem.response response1 = ChatSystem.response.newBuilder()
                                .setResponseToRequest("** NewMSG from "+requestMsg.getUserName() + " : "+requestMsg.getRequest())
                                .build();
                        userObservable.onNext(response1);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println(">> Extracting the collection of request from user: "+listRequest.get(0).getUserName());
                for(ChatSystem.requestMsg req : listRequest){
                    System.out.println("** Request_"+listRequest.indexOf(req)+" --> "+req.getRequest());
                }

                listUsers.remove(responseObserver);
                responseObserver.onCompleted();
            }
        };
    }





/**    @Override
    public void getListAccount(ChatSystem.listAccountRequest request, StreamObserver<ChatSystem.listAccount> responseObserver) {
        ChatSystem.listAccount.Builder listAccount = ChatSystem.listAccount.newBuilder();
        //List<ChatSystem.AccountTransaction> performedTransactions=new ArrayList<>();
        for(int i=1;i<=request.getTotalAccount();i++) {
            ChatSystem.Account account = ChatSystem.Account.newBuilder()
                    .setId(i)
                    .setName("Client_" + i)
                    .setAmount(Math.random() * 500)
                    .setStatus("Activated")
                    .build();

            listAccount.addAccount(account);
        };
        responseObserver.onNext(listAccount.build());
        responseObserver.onCompleted();
    }**/
}
