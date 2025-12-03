package com.campusconnect.core;

public class Teacher extends User {
    private String department;

    public Teacher(String id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getUserType() {
        return "TEACHER";
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + department;
    }

    @Override
    public void fromCSV(String csv) {
        super.fromCSV(csv);
        String[] parts = csv.split(",");
        if (parts.length >= 12) {
            this.department = parts[11];
        }
    }
}
