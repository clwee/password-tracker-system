package com.example.password_tracker_system.controller;

import com.example.password_tracker_system.model.CredentialEntry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VaultController {

    private final List<CredentialEntry> entries = new ArrayList<>();
    private final String FILE_NAME = "entries.txt";
    private final UnlockController unlockController;

    public VaultController(UnlockController unlockController) {
        this.unlockController = unlockController;
    }

    @PostConstruct
    public void loadEntries() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);

                if (parts.length == 3) {
                    entries.add(new CredentialEntry(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveEntries() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (CredentialEntry entry : entries) {
                writer.write(entry.getServiceName() + "|" + entry.getUsername() + "|" + entry.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/entries")
    public String showEntries(Model model) {
        if (!unlockController.isLoggedIn()) {
            return "redirect:/";
        }

        model.addAttribute("entries", entries);
        return "entries";
    }

    @PostMapping("/addEntry")
    public String addEntry(@RequestParam String serviceName,
                           @RequestParam String username,
                           @RequestParam String password) {
        if (!unlockController.isLoggedIn()) {
            return "redirect:/";
        }

        entries.add(new CredentialEntry(serviceName, username, password));
        saveEntries();

        return "redirect:/entries";
    }

    @GetMapping("/deleteEntry")
    public String deleteEntry(@RequestParam int index) {
        if (!unlockController.isLoggedIn()) {
            return "redirect:/";
        }

        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            saveEntries();
        }

        return "redirect:/entries";
    }
}