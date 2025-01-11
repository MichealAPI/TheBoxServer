package it.mikeslab.thebox.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
@AllArgsConstructor
public class Setting {

    public static final Map<String, Setting> DEFAULT_SETTINGS = Setting.getDefaultSettings();

    private final String displayName;
    private final String description;
    private final String dataType;

    @Setter
    private Object value;

    private static Map<String, Setting> getDefaultSettings() {
        return Map.of(
            "DARK_MODE", new Setting("Dark Mode", "Toggle dark mode", "boolean", "false"),
            "PROFILE_PICTURE", new Setting("Profile Picture", "Your profile picture", "image-picker", "none")
        );
    }

    public String getValueAsString() {
        return value.toString();
    }

    public boolean getValueAsBoolean() {
        return (boolean) value;
    }

    public int getValueAsInt() {
        return (int) value;
    }


}
