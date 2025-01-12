package it.mikeslab.thebox.entity;

import it.mikeslab.thebox.util.AuthUtil;
import it.mikeslab.thebox.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
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

    private String verificationToken;

    private List<Course> courses;

    private Map<String, Object> settings;

    private Collection<? extends GrantedAuthority> authorities;

    public User(BsonDocument document) {

        this.username = document.getString("username").getValue();
        this.email = document.getString("email").getValue();
        this.firstName = document.getString("firstName").getValue();
        this.lastName = document.getString("lastName").getValue();

        this.verificationToken = document.getString("verificationToken", new BsonString("not-verified")).getValue();

        // Encode the password before storing it
        this.password = AuthUtil.bCryptEncode(document.getString("password").getValue());

        this.courses = new ArrayList<>();
        this.authorities = new ArrayList<>();
        this.settings = new HashMap<>();

    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("email", email);
        map.put("firstName", StringUtil.capitalize(firstName));
        map.put("lastName", StringUtil.capitalize(lastName));

        // todo add also settings here!
        return map;
    }

    public static Set<String> getFields() {
        return Set.of("username", "email", "firstName", "lastName", "password");
    }

    public String getFullName() {
        return StringUtil.capitalize(firstName) + " " + StringUtil.capitalize(lastName);
    }

    /**
     * Processes and returns a map of parsed settings, combining the default settings
     * with raw settings provided in the instance. The method ensures that default settings
     * are updated with the corresponding values from the raw settings if available.
     *
     * @return a map containing the parsed settings where each setting is updated with its corresponding value.
     */
    public Map<String, Object> getParsedSettings() { // Todo: Cache the parsed settings
        // Clone the default settings into a new map
        Map<String, Object> parsedSettings = new HashMap<>(Setting.DEFAULT_SETTINGS);

        // Iterate over this instance's raw settings
        if(this.settings == null) {
            return parsedSettings;
        }

        // Update the parsed settings with the corresponding value
        parsedSettings.putAll(this.settings);

        return parsedSettings;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(verificationToken, "none");
    }
}
