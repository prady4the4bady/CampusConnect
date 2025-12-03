package com.campusconnect.core;

public class Alumni extends User {
    private int graduationYear;
    private String currentCompany;

    public Alumni(String id, String name, String email, String password, int graduationYear, String currentCompany) {
        super(id, name, email, password);
        this.graduationYear = graduationYear;
        this.currentCompany = currentCompany;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    @Override
    public String getUserType() {
        return "ALUMNI";
    }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + graduationYear + "," + currentCompany;
    }

    @Override
    public void fromCSV(String csv) {
        super.fromCSV(csv);
        String[] parts = csv.split(",");
        if (parts.length >= 13) {
            this.graduationYear = Integer.parseInt(parts[11]);
            this.currentCompany = parts[12];
        }
    }
}
