package it.mikeslab.thebox.entity;

import it.mikeslab.thebox.util.AuthUtil;
import it.mikeslab.thebox.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id; // MongoDB provides this by default.

    private String username = "",
                    email,
                    firstName,
                    lastName,
                    password;

    private List<Course> courses;

    private Collection<? extends GrantedAuthority> authorities;

    public User(BsonDocument document) {

        this.username = document.getString("username").getValue();
        this.email = document.getString("email").getValue();
        this.firstName = document.getString("firstName").getValue();
        this.lastName = document.getString("lastName").getValue();

        // Encode the password before storing it
        this.password = AuthUtil.bCryptEncode(document.getString("password").getValue());

        this.courses = new ArrayList<>();
        this.authorities = new ArrayList<>();

    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("firstName", StringUtil.capitalize(firstName));
        map.put("lastName", StringUtil.capitalize(lastName));
        return map;
    }

    public static Set<String> getFields() {
        return Set.of("username", "email", "firstName", "lastName", "password");
    }

    public String getFullName() {
        return StringUtil.capitalize(firstName) + " " + StringUtil.capitalize(lastName);
    }

}
