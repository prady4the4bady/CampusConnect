package com.campusconnect.core;

import java.time.LocalDateTime;

public class DataSeeder {
    public static void seed() {
        DataManager dm = DataManager.getInstance();
        if (!dm.getUsers().isEmpty())
            return; // Don't seed if data exists

        System.out.println("Seeding BITS Pilani Dubai Data...");

        // 1. Users (Students)
        User s1 = UserFactory.createUser("STUDENT", "2022A7PS001U", "Aarav Sharma", "aarav@bits-dubai.ac.ae", "pass123",
                "Computer Science", "3");
        s1.addInterest(new Interest("Coding", "Skill"));
        s1.addInterest(new Interest("Music", "Hobby"));
        dm.addUser(s1);

        User s2 = UserFactory.createUser("STUDENT", "2023A4PS005U", "Fatima Al-Ali", "fatima@bits-dubai.ac.ae",
                "pass123", "Mechanical", "2");
        s2.addInterest(new Interest("Robotics", "Skill"));
        s2.addInterest(new Interest("Debate", "Hobby"));
        dm.addUser(s2);

        User s3 = UserFactory.createUser("STUDENT", "2021A3PS010U", "Rohan Mehta", "rohan@bits-dubai.ac.ae", "pass123",
                "EEE", "4");
        s3.addInterest(new Interest("Electronics", "Skill"));
        s3.addInterest(new Interest("Photography", "Hobby"));
        dm.addUser(s3);

        // 2. Groups (Clubs)
        Group g1 = new Group("C001", "Trebel (Music Club)",
                "The official music club of BPDC. Join for jam sessions and Jashn performances.");
        g1.addMember(s1.getId());
        dm.addGroup(g1);

        Group g2 = new Group("C002", "Flummoxed (Quiz Club)",
                "For the curious minds. Weekly quizzes and trivia nights.");
        dm.addGroup(g2);

        Group g3 = new Group("C003", "Team IFOR",
                "Intelligent Flying Objects for Reconnaissance. Robotics and Drones.");
        g3.addMember(s2.getId());
        g3.addMember(s3.getId());
        dm.addGroup(g3);

        Group g4 = new Group("C004", "ACM Student Chapter",
                "Association for Computing Machinery. Coding competitions and hackathons.");
        g4.addMember(s1.getId());
        dm.addGroup(g4);

        // 3. Events
        dm.addEvent(new Event("E001", "Jashn 2025", "Annual Inter-University Cultural Festival. Dance, Music, Drama.",
                LocalDateTime.now().plusMonths(2), "Main Auditorium", "Admin"));
        dm.addEvent(new Event("E002", "BITS Tech Fest", "Showcase of innovation, AI, and Robotics.",
                LocalDateTime.now().plusMonths(1), "Academic Block", "Admin"));
        dm.addEvent(new Event("E003", "Sports Festival", "Inter-university sports tournament.",
                LocalDateTime.now().plusWeeks(3), "Sports Complex", "Admin"));

        System.out.println("Seeding Complete.");
    }
}
