package com.example.password_tracker_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Controller
public class UnlockController {

    private final String PASSWORD_FILE = "masterpassword.txt";
    private String masterPasscode = null;
    private boolean loggedIn = false;

    public UnlockController() {
        loadMasterPassword();
    }

    private void loadMasterPassword() {
        File file = new File(PASSWORD_FILE);

        if (!file.exists()) {
            masterPasscode = null;
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String savedPassword = reader.readLine();
            if (savedPassword != null && !savedPassword.isBlank()) {
                masterPasscode = savedPassword;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMasterPassword(String newPassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE))) {
            writer.write(newPassword);
            masterPasscode = newPassword;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String home() {
        if (masterPasscode == null) {
            return "setup";
        }
        return "unlock";
    }

    @PostMapping("/setup")
    public String setupPassword(@RequestParam String newPassword, Model model) {
        if (newPassword == null || newPassword.isBlank()) {
            model.addAttribute("error", "Password cannot be empty");
            return "setup";
        }

        saveMasterPassword(newPassword);
        model.addAttribute("message", "Master password created successfully");
        return "unlock";
    }

    @PostMapping("/unlock")
    public String unlockVault(@RequestParam String passcode, Model model) {
        if (masterPasscode != null && passcode.equals(masterPasscode)) {
            loggedIn = true;
            return "redirect:/entries";
        }

        model.addAttribute("error", "Incorrect passcode");
        return "unlock";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        if (!loggedIn) {
            return "redirect:/";
        }
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 Model model) {
        if (!loggedIn) {
            return "redirect:/";
        }

        if (!currentPassword.equals(masterPasscode)) {
            model.addAttribute("error", "Current password is incorrect");
            return "change-password";
        }

        if (newPassword == null || newPassword.isBlank()) {
            model.addAttribute("error", "New password cannot be empty");
            return "change-password";
        }

        saveMasterPassword(newPassword);
        model.addAttribute("message", "Master password changed successfully");
        return "change-password";
    }

    @GetMapping("/lock")
    public String lockVault() {
        loggedIn = false;
        return "redirect:/";
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}