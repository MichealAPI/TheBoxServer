package it.mikeslab.thebox.entity;

import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@Document(collection = "courses")
public class Course {

    @Id
    private String id; // MongoDB provides this by default.

    private String title;
    private String description;
    private String ownerUsername; // Not editable

    private List<String> members; // User IDs

    private long timestamp;

    // Ideas are stored as a list for sorting purposes.
    private Map<String, Idea> ideas;

    private Map<String, String> invites; // Usernames of users invited to join the course

    private Map<String, String> settings; // Individual. Also contains eventual wallpaper, etc.

    public String formattedTimestamp() {
        return StringUtil.formatTimestamp(timestamp);
    }

}
