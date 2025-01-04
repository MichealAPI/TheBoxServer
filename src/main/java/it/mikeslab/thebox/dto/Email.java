package it.mikeslab.thebox.dto;

import it.mikeslab.thebox.entity.Course;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Email {

    private String subject;
    private String template;
    private String targetEmail;
    private String confirmationUrl;

    private String targetUsername;
    private String targetFullName;

    private Course course;

}
