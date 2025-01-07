package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Setting;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final UserRepository userRepository;
    private final UserService userService;

    public void saveSettings(String username, Map<String, Object> settings) {
        User user = userService.getUserByUsername(username);

        user.setSettings(settings);

        userRepository.save(user);
    }

    public Map<String, Object> retrieveUserSettings(String username) {

        User user = userService.getUserByUsername(username);

        return user.getSettings();
    }

    public void updateSettings(String username, Map<String, Object> updatedSettings) {

        User user = userService.getUserByUsername(username);

        Map<String, Object> current = user.getSettings();

        if(current == null) {
            current = new HashMap<>();
        }

        // Update the settings
        current.putAll(updatedSettings); // Replace the current settings with the updated settings

        user.setSettings(current);

        userRepository.save(user); // TODO Finish up translation

    }


}
