package it.mikeslab.thebox.rest;

import it.mikeslab.thebox.dto.Email;
import it.mikeslab.thebox.entity.Course;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.helper.ValidationResult;
import it.mikeslab.thebox.service.CourseService;
import it.mikeslab.thebox.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailRestController {

    private static final String LOGO_IMAGE = "/static/images/logo.webp";

    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final CourseService courseService;
    private final UserService userService;

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUser(@RequestBody String payload) throws MessagingException, UnsupportedEncodingException {

        ValidationResult validationResult = ValidationResult.validatePayloadAndUser(
                payload,
                courseService
        );

        if (!validationResult.isSuccess()) {
            return validationResult.getErrorResponse();
        }

        BsonDocument document = validationResult.getBsonDocument();
        String courseId = document.getString("courseId").getValue();
        String targetEmail = document.getString("target").getValue();

        // Check if user is the owner of course
        Course course = courseService.fetchCourseById(courseId);

        if (!course.getOwnerUsername().equals(validationResult.getUser().getUsername())) {
            return ResponseEntity.badRequest().body("You are not the owner of this course");
        }

        // Check if user exists
        User user = userService.getUserByEmail(targetEmail);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(course.getMembers().contains(user.getUsername())) {
            return ResponseEntity.badRequest().body("User is already a member of this course");
        }

        if(course.getInvites().containsValue(user.getUsername())) {
            return ResponseEntity.badRequest().body("User has already been invited to this course");
        }

        // Send invitation email
        ObjectId inviteIdObj = new ObjectId();
        String inviteId = inviteIdObj.toHexString();

        // Send email
        sendEmail(new Email(
                Map.of(
                        "targetEmail", user.getEmail(),
                        "fullName", user.getFullName(),
                        "subject", "Invitation to join course",
                        "confirmationUrl", environment.getProperty("url.base") + "/api/courses/invite/confirm/" + inviteId + "?courseId=" + courseId,
                        "template", "invite",
                        "course", course
                    )
                )
        );

        // Add invite to course
        courseService.addInvite(
                course,
                user.getUsername(),
                inviteId
        );

        return ResponseEntity.ok().build();
    }


    @PostMapping("/registration/verify")
    public ResponseEntity<String> verifyUser(@RequestBody String payload) {

        String targetEmail = BsonDocument.parse(payload)
                .getString("target")
                .getValue();

        // Check if user exists
        User user = userService.getUserByEmail(targetEmail);

        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if(user.isEnabled()) {
            return ResponseEntity.badRequest().body("User is already verified");
        }

        // Send verification email
        ObjectId verificationIdObj = new ObjectId();
        String verificationId = verificationIdObj.toHexString();

        // Send email

        new Thread(() -> {
            try {
                sendEmail(new Email(
                                Map.of(
                                        "targetEmail", user.getEmail(),
                                        "targetFullName", user.getFullName(),
                                        "subject", "Verify your account",
                                        "confirmationUrl", environment.getProperty("url.base") + "/registration/verify?token=" + verificationId + "&username=" + user.getUsername(),
                                        "template", "verify"
                                )
                        )
                );


            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // Add verification to user
        userService.addVerification(
                user,
                verificationId
        );

        return ResponseEntity.ok().build();
    }


    public void sendEmail(Email emailDto) throws MessagingException, UnsupportedEncodingException {

        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "TheBox");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Map<String, Object> attributes = emailDto.getData();

        // Set email properties
        email.setTo(emailDto.getTo());
        email.setSubject(emailDto.getSubject());
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        ctx.setVariable("logoImage", LOGO_IMAGE);

        final String htmlContent = this.htmlTemplateEngine.process(emailDto.getTemplate(), ctx);
        email.setText(htmlContent, true);

        ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);

        email.addInline("logoImage", clr, "image/webp");

        mailSender.send(mimeMessage);

    }

}
