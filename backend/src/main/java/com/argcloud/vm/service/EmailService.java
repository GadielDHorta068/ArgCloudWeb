package com.argcloud.vm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para el envío de correos electrónicos.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${app.from.email}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * Envía un correo de verificación a la dirección de correo electrónico especificada.
     *
     * @param to la dirección de correo electrónico del destinatario.
     * @param verificationToken el token de verificación a incluir en el correo.
     */
    public void sendVerificationEmail(String to, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Verificación de Email - ArgCloud");
        
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        
        String messageText = "Hola,\n\n" +
                "Gracias por registrarte en ArgCloud.\n\n" +
                "Para completar tu registro, por favor haz clic en el siguiente enlace:\n" +
                verificationUrl + "\n\n" +
                "Este enlace expirará en 24 horas.\n\n" +
                "Si no te registraste en ArgCloud, puedes ignorar este email.\n\n" +
                "Saludos,\n" +
                "El equipo de ArgCloud";
        
        message.setText(messageText);
        
        emailSender.send(message);
    }
} 