package it.mikeslab.thebox.service;

import it.mikeslab.thebox.entity.Setting;
import it.mikeslab.thebox.entity.Settings;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.repository.SettingsRepository;
import it.mikeslab.thebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // todo Optimize!
    public void updateSettings(String username, Map<String, Object> updatedSettings) {

        User user = userService.getUserByUsername(username);

        Map<String, Setting> parsedSettings = user.getParsedSettings();

        for (Map.Entry<String, Object> entry : updatedSettings.entrySet()) {
            if (parsedSettings.containsKey(entry.getKey())) {
                Setting setting = parsedSettings.get(entry.getKey());
                setting.setValue(entry.getValue());
            }
        }

        Map<String, Object> updated = new Settings().fromParsedSettings(parsedSettings, username);

        settingsRepository.save(updated); // TODO Finish up translation

    }




    public void deleteSettings(String username) {
        settingsRepository.delete(settingsRepository.findSettingsByUsername(username));
    }

}
