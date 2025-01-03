package it.mikeslab.thebox.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class StringUtil {

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("dd.MM.yyyy").format(new Date(timestamp));
    }

}
