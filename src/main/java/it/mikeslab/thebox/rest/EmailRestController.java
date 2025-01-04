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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class EmailRestController {


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
        sendVerificationEmail(
                Email.builder()
                        .targetEmail(user.getEmail())
                        .targetFullName(user.getFullName())
                        .subject("Invitation to join course")
                        .confirmationUrl("http://localhost:8080/api/courses/invite/confirm/" + inviteId + "?courseId=" + courseId)
                        .template("invite")
                        .course(course)
                        .build()
        );

        // Add invite to course
        courseService.addInvite(
                course,
                user.getUsername(),
                inviteId
        );

        return ResponseEntity.ok().build();
    }


    public void sendVerificationEmail(Email emailDto) throws MessagingException, UnsupportedEncodingException {

        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "TheBox");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // Set email properties
        email.setTo(emailDto.getTargetEmail());
        email.setSubject(emailDto.getSubject());
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("email", emailDto.getTargetEmail());
        ctx.setVariable("fullName", emailDto.getTargetFullName());
        ctx.setVariable("url", emailDto.getConfirmationUrl());
        ctx.setVariable("course", emailDto.getCourse());

        final String htmlContent = this.htmlTemplateEngine.process(emailDto.getTemplate(), ctx);
        email.setText(htmlContent, true);

        mailSender.send(mimeMessage);

    }

}
