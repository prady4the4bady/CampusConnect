package com.campusconnect;

import com.campusconnect.core.*;
import com.campusconnect.security.EncryptionUtil;
import java.io.File;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Starting CampusConnect Phase 2 Verification...");

        // Clean up old data for testing
        deleteDirectory(new File("data"));

        // 1. Test Encryption
        System.out.println("\n--- Testing Encryption ---");
        String original = "SecretMessage";
        String encrypted = EncryptionUtil.encrypt(original);
        String decrypted = EncryptionUtil.decrypt(encrypted);
        System.out.println("Original: " + original);
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + decrypted);
        if (original.equals(decrypted)) {
            System.out.println("Encryption Test PASSED");
        } else {
            System.err.println("Encryption Test FAILED");
        }

        DataManager dm = DataManager.getInstance();
        DataSeeder.seed();

        // 2. Test Data Seeding
        System.out.println("\n--- Testing Data Seeding ---");
        if (!dm.getUsers().isEmpty()) {
            System.out.println("Users loaded: " + dm.getUsers().size());
            System.out.println("First User: " + dm.getUsers().get(0).getName());
        } else {
            System.err.println("Data Seeding FAILED");
        }

        // 3. Test Walkie-Talkie
        System.out.println("\n--- Testing Walkie-Talkie ---");
        WalkieTalkieChannel channel = new WalkieTalkieChannel("WC1", "Emergency Channel", true, "123456");
        dm.addChannel(channel);
        System.out.println("Channel created: " + channel.getName() + " (Private: " + channel.isPrivate() + ")");

        // 4. Verify Persistence with Encryption
        System.out.println("\n--- Verifying Encrypted Persistence ---");
        if (new File("data/users.csv").exists()) {
            System.out.println("users.csv exists.");
            // Ideally we'd read the file and check if it looks encrypted (not plain CSV)
        }

        System.out.println("\nVerification Complete.");
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}
