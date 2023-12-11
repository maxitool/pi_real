package com.example.pi.bd;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

public class SendToServer extends Thread {

    private static final String SERVER_IP = "217.71.129.139";
    private static final int SERVER_PORT = 5330;
    private CountDownLatch countDownLatch;
    private String message;
    private String answer = "";
    private byte[] bytes;

    public SendToServer(CountDownLatch countDownLatch, String message) {
        this.countDownLatch = countDownLatch;
        this.message = message;
        this.start();
    }

    public String getAnswer() { return answer; }

    @Override
    public void run() {
        super.run();
        try {
            InetSocketAddress serverAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
            Socket socket = new Socket();
            socket.connect(serverAddress, 10000);
            PrintStream outStream = new PrintStream(socket.getOutputStream(), true);
            DataInputStream inStream = new DataInputStream(socket.getInputStream());
            outStream.print(message + "<END>");
            boolean isEnd = false;
            while (!isEnd) {
                bytes = new byte[512];
                inStream.read(bytes);
                answer += new String(bytes, "UTF-8");
                if (answer.lastIndexOf("<END>") > -1)
                    isEnd = true;
            }
            outStream.print("<END>"); //Call to close the socket to the server
            socket.close();
            answer = answer.replace("<END>", "");
            deleteNull();
            countDownLatch.countDown();
        } catch (UnknownHostException e) {
            countDownLatch.countDown();
            e.printStackTrace();
        } catch (IOException e) {
            countDownLatch.countDown();
            e.printStackTrace();
        }
    }
    private void deleteNull() {
        char[] chars = answer.toCharArray();
        answer = "";
        for (int i = 0; i < chars.length; i++)
            if (chars[i] != 0)
                answer += chars[i];
            else
                break;
    }
}
