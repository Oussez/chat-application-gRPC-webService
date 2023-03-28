package com.Oussez.client;

public class Singleton implements Runnable {
    public static int onlineUsers;
    private static Singleton clientHUB;

    public static void main(String[] args) {
        clientHUB = new Singleton();
        new Thread(clientHUB).start();
        System.out.println("client : "+clientHUB);

    }

    @Override
    public void run() {
        System.out.println("Thread is running");
        while(true){}


    }

    public void increment(){
        Singleton.onlineUsers++;
        System.out.println(">> online users : "+onlineUsers);
    }

    public static Singleton getThread(){
        if(clientHUB != null)
            return clientHUB;
        System.out.println("thread null");
        return null;
    }
}

