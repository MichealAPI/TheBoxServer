package it.mikeslab.thebox.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "ideas")
public class Idea {

    @Id
    private String id; // MongoDB provides this by default.

    private String title;
    private String description;
    private String courseId;

    private int votes;

    private long timestamp;

    private String userId;

    private List<Comment> comments;

}
