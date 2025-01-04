package it.mikeslab.thebox.pojo;

import it.mikeslab.thebox.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

@Data
public class Idea {

    private ObjectId id = new ObjectId();

    private String name;
    private String details;

    private Map<String, Vote> votes;

    private long timestamp;

    private String authorUsername; // Not editable

    private List<Comment> comments;

    public Idea(String name, String details, String authorUsername, Map<String, Vote> votes) {
        this.name = name;
        this.details = details;
        this.authorUsername = authorUsername;
        this.votes = votes;
        this.timestamp = System.currentTimeMillis();
        this.comments = List.of();
    }

    public String formattedTimestamp() {
        return StringUtil.formatTimestamp(timestamp);
    }

    public int calculateVotes() {
        return votes.values().stream().mapToInt(Vote::getValue).sum();
    }

    public String getId() {
        return id.toHexString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Vote {
        UP(+1),
        DOWN(-1);

        private final int value;

        public static Vote fromInt(int value) {
            return value > 0 ? UP : DOWN;
        }
    }

}
