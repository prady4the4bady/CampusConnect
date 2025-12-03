package com.campusconnect.core;

public class Student extends User {
    private String major;
    private int year;

    public Student(String id, String name, String email, String password, String major, int year) {
        super(id, name, email, password);
        this.major = major;
        this.year = year;
    }

    public String getMajor() {
        return major;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String getUserType() {
        return "STUDENT";
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + major + "," + year;
    }

    @Override
    public void fromCSV(String csv) {
        super.fromCSV(csv);
        String[] parts = csv.split(",");
        if (parts.length >= 13) {
            this.major = parts[11];
            this.year = Integer.parseInt(parts[12]);
        }
    }
}
