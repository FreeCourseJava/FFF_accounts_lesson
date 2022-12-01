package org.homework.controller.impl;

import org.homework.annotation.Service;
import org.homework.annotation.StartPoint;
import org.homework.controller.AccountController;
import org.homework.service.AccountService;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

@Service
public class AccountControllerHTTP implements AccountController {
    private AccountService accServ;

    public AccountControllerHTTP(AccountService accServ) {
        this.accServ = accServ;
        System.out.println("Выполнен конструктор AccCtrlHTTP");
    }

    @StartPoint
    @Override
    public void receiveCommand() {
        try (ServerSocket server = new ServerSocket(6670); Socket socket = server.accept();
             InputStream inStream = socket.getInputStream(); OutputStream outStream = socket.getOutputStream();
             Scanner scanner = new Scanner(inStream); Writer writer = new PrintWriter(outStream)) {
            String query = scanner.nextLine();
            if (query.contains("/transfer")) {
                String[] parts = query.split("=");
                String donor = (parts[1].split("&"))[0];
                String acceptor = (parts[2].split("&"))[0];
                int sum = Integer.parseInt(parts[3].split(" ")[0]);
                accServ.cashTranslation(donor, acceptor, sum);
            } else {
                writer.write("\"HTTP/1.1 200 OK\\n Content-Type: text/html\\n Connection: close\\n\\n Unknown command!\"");
            }

            writer.write("HTTP/1.1 200 OK\n Content-Type: text/html\n Connection: close\n\n Query was sent!");


        } catch (Exception e) {
            System.out.println("Проблемы с сервером");
        }
    }
}



