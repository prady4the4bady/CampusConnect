package com.campusconnect.interfaces;

public interface Storable {
    String toCSV();
    void fromCSV(String csv);
}
