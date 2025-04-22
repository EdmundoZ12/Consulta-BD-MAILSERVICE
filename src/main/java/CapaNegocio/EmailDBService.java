/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Usuario
 */
public class EmailDBService {

    private static final int SLEEP_TIME = 5000; // 5 segundos entre verificaciones

    // Configuración para Gmail
    private static final String GMAIL_USER = ""; // Tu correo Gmail
    private static final String GMAIL_PASSWORD = ""; // Tu contraseña de aplicación

    public static void main(String[] args) {
        System.out.println("Iniciando servicio de emails con JavaMail...");
        System.out.println("Esperando correos en grupo20sa@tecnoweb.org.bo");
        System.out.println("Enviando respuestas desde " + GMAIL_USER);
        System.out.println("----------------------------------------------");

        while (true) {
            try {
                System.out.println("\nVerificando correos nuevos...");

                // Revisar correos para procesar
                ClientePOP clientePOP = new ClientePOP();
                String[][] emailInfoList = clientePOP.checkAllEmails();

                if (emailInfoList != null && emailInfoList.length > 0) {
                    System.out.println("Se encontraron " + emailInfoList.length + " correos nuevos.");

                    // Procesar cada correo
                    for (String[] emailInfo : emailInfoList) {
                        if (emailInfo != null && emailInfo.length == 2 && emailInfo[0] != null) {
                            String sender = emailInfo[0];
                            String subject = emailInfo[1] != null ? emailInfo[1].trim() : "";

                            System.out.println("\nProcesando correo de: " + sender);
                            System.out.println("Asunto: " + subject);

                            // Verificar si es un comando de consulta
                            if (subject.startsWith("ListPerson")) {
                                // Extraer el patrón después de "ListPerson"
                                String pattern = "";
                                if (subject.length() > "ListPerson".length()) {
                                    pattern = subject.substring("ListPerson".length()).trim();
                                }

                                System.out.println("Ejecutando búsqueda con patrón: '" + pattern + "'");

                                // Conectar a la base de datos y realizar consulta
                                ConnectionDatabase db = new ConnectionDatabase();
                                db.conectar();
                                String result = db.buscarPersonasPorNombre(pattern);
                                db.desconectar();

                                // Enviar resultados por correo usando JavaMail
                                sendEmailWithJavaMail(sender, "Resultados para: " + (pattern.isEmpty() ? "ListPerson (todos)" : pattern), result);
                                System.out.println("Respuesta enviada a: " + sender);
                            } else {
                                // Enviar mensaje de bienvenida si no es un comando
                                sendEmailWithJavaMail(sender, "Hola", "Bienvenido al servicio de consulta de base de datos.\n\nPara consultar, envía un correo con el asunto 'ListPerson' seguido del patrón de búsqueda.\nPor ejemplo:\n- 'ListPerson er' buscará personas con 'er' en cualquier campo\n- 'ListPerson' (sin patrón) mostrará todas las personas");
                                System.out.println("Mensaje de bienvenida enviado a: " + sender);
                            }
                        }
                    }
                } else {
                    System.out.println("No hay correos nuevos.");
                }

                // Esperar antes de la siguiente verificación
                System.out.println("Esperando " + (SLEEP_TIME / 1000) + " segundos para la próxima verificación...");
                Thread.sleep(SLEEP_TIME);

            } catch (Exception e) {
                System.out.println("Error en el servicio: " + e.getMessage());
                e.printStackTrace();

                // Esperar antes de continuar
                try {
                    Thread.sleep(SLEEP_TIME * 2); // Esperar más tiempo en caso de error
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Envía un correo usando JavaMail (compatible con Gmail)
     */
    private static void sendEmailWithJavaMail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Añadir confianza al servidor SMTP
        props.put("mail.smtp.connectiontimeout", "10000"); // Timeout de conexión: 10 segundos
        props.put("mail.smtp.timeout", "10000"); // Timeout de socket: 10 segundos

        // Crear sesión de correo con autenticación
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(GMAIL_USER, GMAIL_PASSWORD);
            }
        });

        try {
            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(GMAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Enviar mensaje
            Transport.send(message);
            System.out.println("Correo enviado con éxito a " + to + " usando JavaMail");

        } catch (MessagingException e) {
            System.out.println("Error al enviar correo con JavaMail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
