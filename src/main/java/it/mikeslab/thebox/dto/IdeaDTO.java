package it.mikeslab.thebox.dto;

import it.mikeslab.thebox.pojo.Idea;
import it.mikeslab.thebox.util.StringUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class IdeaDTO {

    private final Idea idea;

    private final String courseId;

    private final String username,
            firstName,
            lastName;

    public String getAuthorFullName() {
        return StringUtil.capitalize(firstName) + " " + StringUtil.capitalize(lastName);
    }

}
