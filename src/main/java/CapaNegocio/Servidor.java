/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
public class Servidor {

    static int PUERTO = 5000;

    public Servidor() {
        try {
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println("S: Escucho el puerto " + PUERTO);

            for (int i = 0; i < 10; i++) {
                Socket skCliente = skServidor.accept();
                System.out.println("S: Sirvo al cliente " + i);
                DataOutputStream salida = new DataOutputStream(skCliente.getOutputStream());
                salida.writeBytes("Hola Cliente" + i);
                skCliente.close();
            }

            System.out.println("S: Demasiados Clientes por hoy");
        } catch (Exception e) {
        }
    }

}
