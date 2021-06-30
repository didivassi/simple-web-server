package academy.mindswap.server;

import academy.mindswap.server.responses.Headers;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


public class RequestHandler implements Runnable{
    String START_FOLDER="public_html";
    String NOT_FOUND="/404.html";
    String NOT_ALLOWED="/405.html";
    String DEFAULT="/index.html";
    Socket clientSocket;


    public RequestHandler(Socket clientSocket) {
       this.clientSocket=clientSocket;
    }

    private String[] getRequestParts(String request){
        return request.split(" ");
    }

    private boolean resourceNotExists(String resource){
        return !Files.exists(Paths.get(START_FOLDER.concat(resource)));
    }

    private String getResponseType(String request){
        String[] requestParts=getRequestParts(request);

        if(!requestParts[0].equals("GET")){
            return Headers.HTML_405;
        }
        if(resourceNotExists(requestParts[1])){
            return Headers.HTML_404;
        }

        return Headers.HTML_200;
    }

    private String getContentType(String resource) throws IOException {
        return Files.probeContentType(Paths.get(START_FOLDER.concat(resource)));
    }


    private String getResource(String request){
        String[] requestParts=getRequestParts(request);

        if(requestParts.length<3){
            return NOT_FOUND;
        }

        if(!requestParts[0].equals("GET")){
            return NOT_ALLOWED;
        }

        String resource = requestParts[1].split("\\?")[0];

        if(resource.length()==1){
            return DEFAULT  ;
        }

        if(resourceNotExists(resource)){
            return NOT_FOUND;
        }

        return  resource;
    }

    private void sendResponse(String resource,String responseType, String contentType){

        DataOutputStream out;
        FileInputStream file;
        int fileLength;
        try {

            out = new DataOutputStream(clientSocket.getOutputStream());
            file = new FileInputStream(START_FOLDER.concat(resource));
            fileLength = file.available();
            out.write(responseType.getBytes());
            out.write(String.format(Headers.CONTENT_TYPE,contentType,fileLength).getBytes());
            int length;
            byte[] buffer = new byte[2048];

            while ((length = file.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }

            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void run() {
        BufferedReader clientRequest;
        String request;
        String resource;

        try {
            clientRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            request = clientRequest.readLine();

            if(request==null){
                return;
            }

            resource=getResource(request);
            sendResponse(resource, getResponseType(request), getContentType(resource));
            System.out.println("Serving "
                    .concat(clientSocket.getInetAddress().getHostAddress())
                    .concat(request)
                    .concat(getContentType(resource))
                    .concat(getResponseType(request)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
