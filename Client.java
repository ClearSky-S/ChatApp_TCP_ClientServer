
import java.io.*;
import java.net.*;
import  java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

class ClientChatReciver extends Thread {
    BufferedReader reader;
    PrintWriter writer;
    int chatIndex;
    public ClientChatReciver(Socket socket) throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        chatIndex = -1;
    }
    synchronized void recive() throws IOException {
        writer.println("#READ "+chatIndex);  //서버로 데이터를 전송한다.
        writer.flush();   //버퍼 안에 있는 값들을 전부 비워준다.
        String line = reader.readLine();
        if(line.equals(".")){
            System.out.println("....");
        } else if(line.equals("")){
            System.out.println("nullll");

        } else{
            String[] list = line.split("@");
            chatIndex = Integer.parseInt(list[0]);
            for(int i=1; i< list.length;i++){
                System.out.println(list[i]);  //채팅로그 출력
            }
            

        }

    }
    @Override
    public void run() {
        // 주기적으로 서버에서 채팅 기록을 읽어온다.
        super.run();
        String line;
        while (true) {
            try {
                recive();
                sleep(5000);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
    public class Client {
    public static void main(String[] args) throws IOException {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = null;
        InetAddress serverAddress;
        int port1, port2;
        if(args.length == 3){
            serverAddress = InetAddress.getByName(args[0]);
            port1 = Integer.parseInt(args[1]);
            port2 = Integer.parseInt(args[2]);
        } else{
            serverAddress = InetAddress.getByName("127.0.0.1");
            port1 = 2020;
            port2 = 2021;
        }
        try {
            socket = new Socket(serverAddress, port1);
            System.out.println("연결성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ClientChatReciver clientChatReciver = new ClientChatReciver(socket);
        clientChatReciver.start();

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(true){
            //클라이언트 -> 소켓
            String input = inFromUser.readLine();
            String line;
            if(input.equals("")){
                continue;
            }
            if(input.charAt(0)=="#".charAt(0)){
                line = input;
            } else{
                line = "#WRITE @"+input;
            }
            writer.println(line);  //서버로 데이터를 전송한다.
            writer.flush();   //버퍼 안에 있는 값들을 전부 비워준다.
            line = reader.readLine();
            String[] list = line.split("@");
            for(int i=0; i< list.length;i++) {
                System.out.println(list[i]);  //채팅로그 출력
            }
        }

    }
}
