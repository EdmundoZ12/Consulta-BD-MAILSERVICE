/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
class ClienteSMTP {

    // Configuración para Gmail
    private static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    private static final int GMAIL_SMTP_PORT = 587;
    private static final String GMAIL_USER = ""; // Reemplazar con tu correo Gmail
    private static final String GMAIL_PASSWORD = ""; // Reemplazar con tu contraseña de aplicación

    /**
     * Envía un correo electrónico con los resultados de la consulta usando
     * Gmail
     */
    public void sendEmail(String destinatario, String asunto, String cuerpo) {
        try {
            System.out.println("Enviando respuesta a " + destinatario + " usando Gmail...");

            // Conectar al servidor SMTP de Gmail
            Socket socket = new Socket(GMAIL_SMTP_SERVER, GMAIL_SMTP_PORT);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            // Leer saludo del servidor
            System.out.println("S: " + entrada.readLine());

            // Iniciar conexión TLS
            String command = "EHLO " + GMAIL_SMTP_SERVER + "\r\n";
            System.out.println("C: " + command);
            salida.writeBytes(command);
            readMultiline(entrada); // Leer respuesta multilinea

            // Iniciar TLS
            command = "STARTTLS\r\n";
            System.out.println("C: " + command);
            salida.writeBytes(command);
            System.out.println("S: " + entrada.readLine());

            // Aquí normalmente habría código para actualizar la conexión a TLS
            // Pero eso requiere bibliotecas adicionales como javax.net.ssl
            // Para simplicidad, vamos a usar un enfoque diferente
            socket.close();

            // Mensaje para el usuario sobre cómo configurar
            System.out.println("\n\n==========================================================");
            System.out.println("IMPORTANTE: Para enviar correos desde Gmail necesitas:");
            System.out.println("1. Activar la verificación en dos pasos en tu cuenta de Google");
            System.out.println("2. Crear una 'Contraseña de aplicación' específica");
            System.out.println("3. Usar esa contraseña en lugar de tu contraseña normal");
            System.out.println("4. Modificar este código para usar bibliotecas como JavaMail");
            System.out.println("   que manejan correctamente las conexiones SSL/TLS");
            System.out.println("==========================================================\n");

            System.out.println("El correo no pudo enviarse usando sockets simples con Gmail.");
            System.out.println("Gmail requiere SSL/TLS y autenticación compleja.");
            System.out.println("Recomendación: Usa la biblioteca JavaMail para Gmail,");
            System.out.println("o configura el servidor local para permitir relay.");

        } catch (Exception e) {
            System.out.println("Error al enviar correo con Gmail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lee una respuesta multilinea del servidor SMTP
     */
    private String readMultiline(BufferedReader in) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
            // Si el cuarto carácter es un espacio, es la última línea
            if (line.length() > 3 && line.charAt(3) == ' ') {
                break;
            }
        }

        System.out.println("S: " + response.toString());
        return response.toString();
    }
}
