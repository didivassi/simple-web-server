package academy.mindswap.server;

import academy.mindswap.server.responses.Headers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {


    ServerSocket serverSocket;
    BufferedReader clientRequest;
    String START_FOLDER="public_html";
    String NOT_FOUND="/404.html";
    String DEFAULT="/index.html";

    public static void main(String[] args) {
        Server webServer=new Server();

        try {
            webServer.startServer(8080);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * Starts the server and opens the serverSocket
     */
    private void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(8080);

        while (serverSocket.isBound()){
            Socket clientSocket=serverSocket.accept();
            clientRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String request=clientRequest.readLine();//we only need the first line for this exercise
            if(request==null){
                continue;
            }
            String resource= getResourceFromRequest(request);
            sendResponse(clientSocket,getResponseType(resource),resource, getContentType(resource));
            clientSocket.close();
        }
    }

    private boolean resourceExists(String resource){
        return Files.exists(Paths.get(START_FOLDER.concat(resource)));
    }

    private String getResponseType(String resource){
        return resourceExists(resource)?Headers.HTML_200:Headers.HTML_404;
    }

    private String getContentType(String resource) throws IOException {
        System.out.println(resource);
        return Files.probeContentType(Paths.get(START_FOLDER.concat(resource)));
    }

    private String getResourceFromRequest(String request){
        //request verb [0], resource[1], protocol[2]
        String[] requestParts=request.split(" ");
        if(requestParts.length<3){
            return NOT_FOUND;
        }

        String resource = requestParts[1];
        if(resource.length()==1){
            return DEFAULT  ;
        }
        if(!resourceExists(resource)){
            return NOT_FOUND;
        }
        return  resource;
    }


    private void sendResponse(Socket clientSocket, String responseType, String resource, String contentType) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        System.out.println( contentType );
        byte[] requestedFile = Files.readAllBytes(Paths.get(START_FOLDER.concat(resource)));
        byte[] buffer = new byte[2048];


        out.write(responseType.getBytes());
        out.flush();
        out.write(String.format(Headers.CONTENT_TYPE,contentType,requestedFile.length).getBytes());
        out.flush();
        out.write(requestedFile);
        out.flush();


        out.close();

    }


}
