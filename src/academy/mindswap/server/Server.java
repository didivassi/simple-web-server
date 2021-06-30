package academy.mindswap.server;

import academy.mindswap.server.responses.Headers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;


public class Server {


    public static void main(String[] args) {
        Server webServer=new Server();

        try {
            webServer.startServer(80);

        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
    /**
     * Starts the server and opens the serverSocket
     */
    private void startServer(int port) throws IOException {
        ServerSocket serverSocket;
        serverSocket = new ServerSocket(port);
        ExecutorService senderService = Executors.newCachedThreadPool();
        System.out.println("Server started at Port: ".concat(String.valueOf(port)));
        while (serverSocket.isBound()){
            senderService.submit(new RequestHandler(serverSocket.accept()));
        }
    }





}
