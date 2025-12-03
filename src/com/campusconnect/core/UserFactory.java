package com.campusconnect.core;

public class UserFactory {
    public static User createUser(String type, String id, String name, String email, String password, String... extra) {
        switch (type.toUpperCase()) {
            case "STUDENT":
                // extra[0] = major, extra[1] = year
                return new Student(id, name, email, password, extra.length > 0 ? extra[0] : "", extra.length > 1 ? Integer.parseInt(extra[1]) : 1);
            case "TEACHER":
                // extra[0] = department
                return new Teacher(id, name, email, password, extra.length > 0 ? extra[0] : "");
            case "ALUMNI":
                // extra[0] = gradYear, extra[1] = company
                return new Alumni(id, name, email, password, extra.length > 0 ? Integer.parseInt(extra[0]) : 0, extra.length > 1 ? extra[1] : "");
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }

    public static User fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length < 5) return null;
        String type = parts[0];
        // We can use the specific classes' fromCSV, but we need an instance first.
        // Or we can parse here. Let's parse basic info and create instance.
        String id = parts[1];
        String name = parts[2];
        String email = parts[3];
        String password = parts[4];
        
        User user = null;
        if (type.equals("STUDENT")) {
             user = new Student(id, name, email, password, "", 0);
        } else if (type.equals("TEACHER")) {
             user = new Teacher(id, name, email, password, "");
        } else if (type.equals("ALUMNI")) {
             user = new Alumni(id, name, email, password, 0, "");
        }
        
        if (user != null) {
            user.fromCSV(csv);
        }
        return user;
    }
}
