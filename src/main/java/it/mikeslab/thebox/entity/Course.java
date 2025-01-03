package it.mikeslab.thebox.entity;

import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
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
    private List<Idea> ideas;

    public String formattedTimestamp() {
        return StringUtil.formatTimestamp(timestamp);
    }

}
