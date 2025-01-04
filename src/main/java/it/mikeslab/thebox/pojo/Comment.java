package it.mikeslab.thebox.pojo;

import it.mikeslab.thebox.util.StringUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Comment {

    private String content;
    private String author;
    private String ideaId;
    private long timestamp;

    public String getAuthorInitial() {
        return author.substring(0, 1).toUpperCase();
    }

    public String formattedTimestamp() {
        return StringUtil.formatTimeAgo(timestamp);
    }








}
