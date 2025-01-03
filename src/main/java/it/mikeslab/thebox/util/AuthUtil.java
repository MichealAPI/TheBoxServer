package it.mikeslab.thebox.util;

import lombok.experimental.UtilityClass;
import org.bson.BsonDocument;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@UtilityClass
public class AuthUtil {

    public boolean containsFields(BsonDocument document, Set<String> fields) {
        for (String field : fields) {
            if (!document.containsKey(field)) {
                return false;
            }
        }
        return true;
    }

    public String bCryptEncode(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }


}
