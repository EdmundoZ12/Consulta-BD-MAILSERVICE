/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
public class Cliente {

    static String SERVIDOR = "mail.tecnoweb.org.bo";
    static int PORT = 25;
    static String user_emisor = "evansbv@gmail.com";
    static String user_receptor = "grupo20sa@tecnoweb.org.bo";
    static String command = "";

    public Cliente() {
        try {
            Socket socket = new Socket(SERVIDOR, PORT);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            System.out.println("C: Conectado a <" + SERVIDOR + ">");
            System.out.println("S: " + entrada.readLine());

            socket.close();

            System.out.println("C: Desconectado del <" + SERVIDOR + ">");
        } catch (Exception e) {
            System.out.println("C: " + e.getMessage());

        }
    }

    public static void main(String[] args) {
        Cliente c = new Cliente();
    }

}
