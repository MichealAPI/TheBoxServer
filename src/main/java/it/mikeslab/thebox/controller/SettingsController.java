package it.mikeslab.thebox.controller;

import it.mikeslab.thebox.entity.Setting;
import it.mikeslab.thebox.entity.User;
import it.mikeslab.thebox.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final UserService userService;

    @GetMapping("/settings")
    public String settingsPage(Model model, User user, HttpServletRequest request) {

        if (user == null) {
            return "redirect:/login";
        }

        // Get updated user instance
        user = userService.getUserByUsername(user.getUsername());

        model.addAllAttributes(user.toMap());

        // Add settings
        model.addAttribute(
                "settings",
                user.getParsedSettings()
        );

        model.addAttribute(
                "settingDetails",
                Setting.DEFAULT_SETTINGS
        );

        model.addAttribute(
                "referer",
                request.getHeader("Referer")
        );

        // Add user initial
        model.addAttribute("userInitial", user.getUsername().charAt(0));

        return "settings";

    }


}
