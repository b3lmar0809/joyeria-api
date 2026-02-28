package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.exception.EmailConfigurationException;
import com.joyeria.joyeria_api.exception.EmailSendException;
import com.joyeria.joyeria_api.exception.InvalidEmailException;
import com.joyeria.joyeria_api.model.Order;
import com.joyeria.joyeria_api.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * EmailService class
 *
 * @Version: 1.0.1 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 25 feb. 2026
 */

//servicio para el envio de email
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.email.from}")
    private String emailFrom;

    @Value("${app.email.support}")
    private String emailSupport;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    //para validar emails
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    //para verificar que el email este puesto ante de iniciar
    @PostConstruct
    public void init() {
        if (mailUsername == null || mailUsername.isEmpty()) {
            log.warn("ADVERTENCIA: Email no configurado. Los emails NO se enviaran.");
        } else {
            log.info("Servicio de email configurado correctamente: {}", mailUsername);
        }
    }

    //para valisar el formato del email
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailException("El email no puede estar vacío");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException(email);
        }
    }

    //para verificar que el servicio de email este  configurado
    private void checkEmailConfiguration() {
        if (mailUsername == null || mailUsername.isEmpty()) {
            throw new EmailConfigurationException(
                    "El servicio de email no está configurado. Configura spring.mail.username en application.properties"
            );
        }
    }

    //Envia un email de bienvenida
    @Async
    public void sendWelcomeEmail(User user) {
        try {
            validateEmail(user.getEmail());
            checkEmailConfiguration();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("¡Bienvenido a Joyería E-commerce! 💍");

            String htmlContent = buildWelcomeEmail(user);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email de bienvenida enviado a: {}", user.getEmail());

        } catch (InvalidEmailException | EmailConfigurationException e) {
            log.error("Error de validación enviando bienvenida a {}: {}", user.getEmail(), e.getMessage());
            throw e;
        } catch (MessagingException e) {
            log.error("Error de mensajería enviando bienvenida a {}: {}", user.getEmail(), e.getMessage());
            throw new EmailSendException(user.getEmail(), "bienvenida", e);
        } catch (Exception e) {
            log.error("Error inesperado enviando bienvenida a {}: {}", user.getEmail(), e.getMessage());
            throw new EmailSendException(user.getEmail(), "bienvenida", e);
        }
    }

    //envia para confirmar la orden
    @Async
    public void sendOrderConfirmation(Order order) {
        try {
            validateEmail(order.getCustomerEmail());
            checkEmailConfiguration();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Confirmación de Orden #" + order.getId() + " 🎁");

            String htmlContent = buildOrderConfirmationEmail(order);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email de confirmación enviado para orden #{}", order.getId());

        } catch (InvalidEmailException | EmailConfigurationException e) {
            log.error("Error de validación enviando confirmación orden #{}: {}", order.getId(), e.getMessage());
            //no lanzar excepcion para no interrumpir el flujo de la orden
        } catch (MessagingException e) {
            log.error("Error de mensajería enviando confirmación orden #{}: {}", order.getId(), e.getMessage());
            //no lanzar excepcion para no interrumpir el flujo de la orden
        } catch (Exception e) {
            log.error("Error inesperado enviando confirmación orden #{}: {}", order.getId(), e.getMessage());
            //no lanzar excepcion para no interrumpir el flujo de la orden
        }
    }

    //envia un notificaciom de entrega
    @Async
    public void sendShippingNotification(Order order) {
        try {
            validateEmail(order.getCustomerEmail());
            checkEmailConfiguration();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Tu orden #" + order.getId() + " ha sido enviada 📦");

            String htmlContent = buildShippingNotificationEmail(order);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email de envío enviado para orden #{}", order.getId());

        } catch (InvalidEmailException | EmailConfigurationException e) {
            log.error("Error de validación enviando notificación envío orden #{}: {}", order.getId(), e.getMessage());
        } catch (MessagingException e) {
            log.error("Error de mensajería enviando notificación envío orden #{}: {}", order.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado enviando notificación envío orden #{}: {}", order.getId(), e.getMessage());
        }
    }

    //manda una noti de entrega
    @Async
    public void sendDeliveryNotification(Order order) {
        try {
            validateEmail(order.getCustomerEmail());
            checkEmailConfiguration();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Tu orden #" + order.getId() + " ha sido entregada ✅");

            String htmlContent = buildDeliveryNotificationEmail(order);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email de entrega enviado para orden #{}", order.getId());

        } catch (InvalidEmailException | EmailConfigurationException e) {
            log.error("Error de validación enviando notificación entrega orden #{}: {}", order.getId(), e.getMessage());
        } catch (MessagingException e) {
            log.error("Error de mensajería enviando notificación entrega orden #{}: {}", order.getId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado enviando notificación entrega orden #{}: {}", order.getId(), e.getMessage());
        }
    }

    private String buildWelcomeEmail(User user) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Bienvenido a Joyería E-commerce! 💍</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        
                        <p>¡Gracias por registrarte en nuestra tienda de joyería!</p>
                        
                        <p>Estamos emocionados de tenerte como parte de nuestra familia. Ahora puedes:</p>
                        
                        <ul>
                            <li>✨ Explorar nuestra colección exclusiva de joyas</li>
                            <li>💎 Realizar compras seguras</li>
                            <li>📦 Rastrear tus órdenes en tiempo real</li>
                            <li>⭐ Dejar reseñas de tus productos favoritos</li>
                        </ul>
                        
                        <div style="text-align: center;">
                            <a href="%s/products" class="button">Explorar Productos</a>
                        </div>
                        
                        <p>Si tienes alguna pregunta, no dudes en contactarnos en <a href="mailto:%s">%s</a></p>
                        
                        <p>¡Felices compras!</p>
                        
                        <p><strong>El equipo de Joyería E-commerce</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este es un email automático, por favor no responder.</p>
                        <p>&copy; 2026 Joyería E-commerce. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                user.getFirstName(),
                frontendUrl,
                emailSupport,
                emailSupport
        );
    }

    private String buildOrderConfirmationEmail(Order order) {
        StringBuilder itemsHtml = new StringBuilder();
        order.getItems().forEach(item -> {
            itemsHtml.append("""
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #ddd;">%s</td>
                    <td style="padding: 10px; border-bottom: 1px solid #ddd; text-align: center;">%d</td>
                    <td style="padding: 10px; border-bottom: 1px solid #ddd; text-align: right;">$%.2f</td>
                </tr>
                """.formatted(
                    item.getProductName(),
                    item.getQuantity(),
                    item.getSubtotal()
            ));
        });

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .order-summary { background: white; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    table { width: 100%%; border-collapse: collapse; }
                    .total { font-size: 18px; font-weight: bold; color: #11998e; }
                    .button { display: inline-block; background: #11998e; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Orden Confirmada! 🎉</h1>
                        <p>Orden #%d</p>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        
                        <p>¡Gracias por tu compra! Hemos recibido tu orden y está siendo procesada.</p>
                        
                        <div class="order-summary">
                            <h3>Resumen de tu orden:</h3>
                            
                            <table>
                                <thead>
                                    <tr style="background: #f0f0f0;">
                                        <th style="padding: 10px; text-align: left;">Producto</th>
                                        <th style="padding: 10px; text-align: center;">Cantidad</th>
                                        <th style="padding: 10px; text-align: right;">Precio</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    %s
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="2" style="padding: 15px; text-align: right; font-weight: bold;">Total:</td>
                                        <td style="padding: 15px; text-align: right;" class="total">$%.2f</td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                        
                        <p><strong>Dirección de envío:</strong><br>
                        %s<br>
                        %s, %s<br>
                        %s, %s</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/orders/%d" class="button">Ver Orden</a>
                        </div>
                        
                        <p>Te notificaremos cuando tu orden sea enviada.</p>
                        
                        <p>Si tienes alguna pregunta, contáctanos en <a href="mailto:%s">%s</a></p>
                        
                        <p><strong>El equipo de Joyería E-commerce</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este es un email automático, por favor no responder.</p>
                        <p>&copy; 2026 Joyería E-commerce. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                order.getId(),
                order.getCustomerName(),
                itemsHtml.toString(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                frontendUrl,
                order.getId(),
                emailSupport,
                emailSupport
        );
    }

    private String buildShippingNotificationEmail(Order order) {
        String trackingLink = order.getTrackingNumber() != null
                ? "<p><strong>Número de rastreo:</strong> " + order.getTrackingNumber() + "</p>"
                : "";

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: #f5576c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Tu orden va en camino! 📦</h1>
                        <p>Orden #%d</p>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        
                        <p>¡Buenas noticias! Tu orden ha sido enviada y está en camino.</p>
                        
                        %s
                        
                        <p><strong>Dirección de entrega:</strong><br>
                        %s<br>
                        %s, %s<br>
                        %s, %s</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/orders/%d" class="button">Rastrear Orden</a>
                        </div>
                        
                        <p>Recibirás otra notificación cuando tu orden sea entregada.</p>
                        
                        <p><strong>El equipo de Joyería E-commerce</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este es un email automático, por favor no responder.</p>
                        <p>&copy; 2026 Joyería E-commerce. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                order.getId(),
                order.getCustomerName(),
                trackingLink,
                order.getShippingAddress(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                frontendUrl,
                order.getId()
        );
    }

    private String buildDeliveryNotificationEmail(Order order) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #4facfe 0%%, #00f2fe 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; background: #4facfe; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>¡Tu orden ha sido entregada! ✅</h1>
                        <p>Orden #%d</p>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        
                        <p>¡Tu orden ha sido entregada exitosamente!</p>
                        
                        <p>Esperamos que disfrutes tus nuevas joyas. 💎</p>
                        
                        <div style="text-align: center;">
                            <a href="%s/products/%d/review" class="button">Dejar una Reseña ⭐</a>
                        </div>
                        
                        <p>Tu opinión es muy importante para nosotros. ¿Te gustaría compartir tu experiencia?</p>
                        
                        <p>Si tienes algún problema con tu orden, contáctanos en <a href="mailto:%s">%s</a></p>
                        
                        <p><strong>El equipo de Joyería E-commerce</strong></p>
                    </div>
                    <div class="footer">
                        <p>Este es un email automático, por favor no responder.</p>
                        <p>&copy; 2026 Joyería E-commerce. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                order.getId(),
                order.getCustomerName(),
                frontendUrl,
                order.getItems().get(0).getProduct().getId(),
                emailSupport,
                emailSupport
        );
    }

    //email de recuperacion
    @Async
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            validateEmail(user.getEmail());
            checkEmailConfiguration();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("Recuperación de Contraseña - Joyería E-commerce 🔐");

            String htmlContent = buildPasswordResetEmail(user, resetToken);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email de recuperación enviado a: {}", user.getEmail());

        } catch (InvalidEmailException | EmailConfigurationException e) {
            log.error("Error de validación enviando recuperación a {}: {}", user.getEmail(), e.getMessage());
        } catch (MessagingException e) {
            log.error("Error de mensajería enviando recuperación a {}: {}", user.getEmail(), e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado enviando recuperación a {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // tmplate HTML para recuperación de contraseña
    private String buildPasswordResetEmail(User user, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background: linear-gradient(135deg, #fc466b 0%%, #3f5efb 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                .button { display: inline-block; background: #fc466b; color: white; padding: 15px 40px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; }
                .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }
                .footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }
                .code { background: #f0f0f0; padding: 10px; border-radius: 5px; font-family: monospace; margin: 15px 0; text-align: center; font-size: 18px; letter-spacing: 2px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>Recuperación de Contraseña 🔐</h1>
                </div>
                <div class="content">
                    <p>Hola <strong>%s</strong>,</p>
                    
                    <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta.</p>
                    
                    <p>Haz click en el siguiente botón para crear una nueva contraseña:</p>
                    
                    <div style="text-align: center;">
                        <a href="%s" class="button">Restablecer Contraseña</a>
                    </div>
                    
                    <p>O copia y pega este enlace en tu navegador:</p>
                    <div class="code">%s</div>
                    
                    <div class="warning">
                        <strong>⚠️ Importante:</strong>
                        <ul>
                            <li>Este enlace es <strong>válido por 1 hora</strong></li>
                            <li>Solo puede usarse <strong>una vez</strong></li>
                            <li>Si no solicitaste este cambio, ignora este email</li>
                        </ul>
                    </div>
                    
                    <p>Si tienes problemas, contáctanos en <a href="mailto:%s">%s</a></p>
                    
                    <p><strong>El equipo de Joyería E-commerce</strong></p>
                </div>
                <div class="footer">
                    <p>Este es un email automático, por favor no responder.</p>
                    <p>Si no solicitaste este cambio, tu cuenta sigue segura.</p>
                    <p>&copy; 2026 Joyería E-commerce. Todos los derechos reservados.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                user.getFirstName(),
                resetLink,
                resetLink,
                emailSupport,
                emailSupport
        );
    }

}
