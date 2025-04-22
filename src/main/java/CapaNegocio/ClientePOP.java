/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.io.*;
import java.net.*;

/**
 *
 * @author Usuario
 */
class ClientePOP {

    private static final String SERVIDOR = "mail.tecnoweb.org.bo";
    private static final int PUERTO = 110;
    private static final String USUARIO = "grupo20sa";
    private static final String CONTRASENA = "grup020grup020*";

    /**
     * Revisa todos los correos disponibles en el buzón
     *
     * @return Array con arrays de [emisor, asunto] o null si no hay correos
     */
    public String[][] checkAllEmails() {
        String[][] result = null;
        Socket socket = null;
        BufferedReader entrada = null;
        DataOutputStream salida = null;

        try {
            // Establecer conexión con servidor POP3
            socket = new Socket(SERVIDOR, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new DataOutputStream(socket.getOutputStream());

            if (socket != null && entrada != null && salida != null) {
                System.out.println("S : " + entrada.readLine());

                // Autenticación
                String comando = "USER " + USUARIO + "\r\n";
                System.out.print("C : " + comando);
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                comando = "PASS " + CONTRASENA + "\r\n";
                System.out.print("C : " + comando);
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());

                // Listar mensajes
                comando = "LIST\r\n";
                System.out.print("C : " + comando);
                salida.writeBytes(comando);
                String listResponse = getMultiline(entrada);
                System.out.println("S : " + listResponse);

                // Contar cuántos correos hay y crear el array
                String[] lines = listResponse.split("\n");
                int messageCount = 0;

                // Contar mensajes válidos (saltando la línea de respuesta +OK)
                for (int i = 1; i < lines.length; i++) {
                    if (!lines[i].trim().isEmpty()) {
                        messageCount++;
                    }
                }

                // Si hay mensajes, procesar cada uno
                if (messageCount > 0) {
                    result = new String[messageCount][2];
                    int currentMessage = 0;

                    for (int i = 1; i < lines.length && currentMessage < messageCount; i++) {
                        String line = lines[i].trim();
                        if (!line.isEmpty()) {
                            int messageNumber = Integer.parseInt(line.split(" ")[0]);

                            // Obtener el mensaje
                            comando = "RETR " + messageNumber + "\r\n";
                            System.out.print("C : " + comando);
                            salida.writeBytes(comando);
                            String message = getMultiline(entrada);

                            // Extraer remitente y asunto
                            result[currentMessage] = parseEmailInfo(message);
                            currentMessage++;

                            // Eliminar el mensaje procesado
                            comando = "DELE " + messageNumber + "\r\n";
                            System.out.print("C : " + comando);
                            salida.writeBytes(comando);
                            System.out.println("S : " + entrada.readLine());
                        }
                    }
                }

                // Cerrar sesión
                comando = "QUIT\r\n";
                System.out.print("C : " + comando);
                salida.writeBytes(comando);
                System.out.println("S : " + entrada.readLine());
            }

            // Cerrar conexiones
            salida.close();
            entrada.close();
            socket.close();

        } catch (UnknownHostException e) {
            System.out.println("S : no se pudo conectar con el servidor indicado");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.out.println("S : error de E/S: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return result;
    }

    /**
     * Revisa los correos y devuelve información del primer correo no procesado
     *
     * @return Array con [emisor, asunto] o null si no hay correos
     */
    public String[] checkEmails() {
        String[][] allEmails = checkAllEmails();
        if (allEmails != null && allEmails.length > 0) {
            return allEmails[0]; // Devolver solo el primer correo
        }
        return null;
    }

    /**
     * Extrae el remitente y asunto de un mensaje de correo
     */
    private String[] parseEmailInfo(String message) {
        String[] result = new String[2];
        String[] lines = message.split("\n");

        for (String line : lines) {
            // Buscar el remitente (intentar varias formas de encontrar la dirección)
            if (line.startsWith("From:")) {
                // Buscar primero entre <>
                int start = line.indexOf('<');
                int end = line.indexOf('>');

                if (start != -1 && end != -1 && end > start) {
                    result[0] = line.substring(start + 1, end);
                } else {
                    // Si no hay <>, intentar otra forma más básica
                    String fromPart = line.substring(5).trim();

                    // Buscar cualquier cosa que parezca un email
                    if (fromPart.contains("@")) {
                        int atPos = fromPart.indexOf('@');
                        int startPos = fromPart.lastIndexOf(' ', atPos);
                        int endPos = fromPart.indexOf(' ', atPos);

                        if (startPos != -1 && endPos != -1) {
                            result[0] = fromPart.substring(startPos + 1, endPos);
                        } else if (startPos != -1) {
                            result[0] = fromPart.substring(startPos + 1);
                        } else if (endPos != -1) {
                            result[0] = fromPart.substring(0, endPos);
                        } else {
                            result[0] = fromPart;
                        }
                    } else {
                        // Si no hay @, usar todo lo que hay después de "From:"
                        result[0] = fromPart;
                    }
                }
            }

            // Buscar el asunto
            if (line.startsWith("Subject:")) {
                result[1] = line.substring(8).trim();
            }

            // Si ya tenemos ambos, salir del bucle
            if (result[0] != null && result[1] != null) {
                break;
            }
        }

        return result;
    }

    // Método para leer respuestas multilínea
    private String getMultiline(BufferedReader in) throws IOException {
        StringBuilder lines = new StringBuilder();
        while (true) {
            String line = in.readLine();
            if (line == null) {
                throw new IOException("S : El servidor cerró la conexión inesperadamente.");
            }
            if (line.equals(".")) {
                break;
            }
            if ((line.length() > 0) && (line.charAt(0) == '.')) {
                line = line.substring(1);
            }
            lines.append(line).append("\n");
        }
        return lines.toString();
    }
}
