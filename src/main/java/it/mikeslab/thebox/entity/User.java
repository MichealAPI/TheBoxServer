package it.mikeslab.thebox.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id; // MongoDB provides this by default.

    private String username,
                    email,
                    firstName,
                    lastName,
                    password;

    private List<Course> courses;

}
