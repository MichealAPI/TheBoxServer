package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Setting;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/settings")
    public String settingsPage(Model model, User user) {

        if (user == null) {
            return "redirect:/login";
        }

        model.addAllAttributes(user.toMap());

        // Both non-set and set settings
        Map<String, Setting> parsedSettings = user.getParsedSettings();

        // Add settings
        model.addAttribute(
                "settings",
                parsedSettings
        );

        // Add user initial
        model.addAttribute("userInitial", user.getUsername().charAt(0));

        return "settings";

    }


}
