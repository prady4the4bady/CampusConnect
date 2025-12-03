package com.campusconnect.core;

import java.util.Objects;

public class Interest {
    private String name;
    private String category; // e.g., "Hobby", "Skill", "Subject"

    public Interest(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interest interest = (Interest) o;
        return Objects.equals(name, interest.name) && Objects.equals(category, interest.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}
