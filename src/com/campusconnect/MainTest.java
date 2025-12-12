package com.campusconnect;

import com.campusconnect.core.*;
import com.campusconnect.security.EncryptionUtil;
import java.io.File;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Starting CampusConnect Phase 2 Verification...");

        
        deleteDirectory(new File("data"));

        
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

        
        System.out.println("\n--- Testing Data Seeding ---");
        if (!dm.getUsers().isEmpty()) {
            System.out.println("Users loaded: " + dm.getUsers().size());
            System.out.println("First User: " + dm.getUsers().get(0).getName());
        } else {
            System.err.println("Data Seeding FAILED");
        }

        
        System.out.println("\n--- Testing Walkie-Talkie ---");
        WalkieTalkieChannel channel = new WalkieTalkieChannel("WC1", "Emergency Channel", true, "123456");
        dm.addChannel(channel);
        System.out.println("Channel created: " + channel.getName() + " (Private: " + channel.isPrivate() + ")");

        
        System.out.println("\n--- Verifying Encrypted Persistence ---");
        if (new File("data/users.csv").exists()) {
            System.out.println("users.csv exists.");
            
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
