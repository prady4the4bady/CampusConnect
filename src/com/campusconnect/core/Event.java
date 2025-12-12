package com.campusconnect.core;

import com.campusconnect.interfaces.Storable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event implements Storable, Comparable<Event> {
    private String id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private String organizerId;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Event(String id, String name, String description, LocalDateTime dateTime, String location,
            String organizerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
        this.organizerId = organizerId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getLocation() {
        return location;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    @Override
    public String toCSV() {
        
        String safeName = name.replace(",", ";");
        String safeDescription = description.replace(",", ";");
        String safeLocation = location.replace(",", ";");
        return id + "," + safeName + "," + safeDescription + "," + dateTime.format(formatter) + "," + safeLocation + ","
                + organizerId;
    }

    @Override
    public void fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length < 6)
            return;

        
        int dateIndex = -1;
        
        
        for (int i = 3; i < parts.length - 1; i++) {
            try {
                LocalDateTime.parse(parts[i], formatter);
                dateIndex = i;
                break;
            } catch (Exception e) {
                
            }
        }

        if (dateIndex != -1) {
            this.id = parts[0];
            this.name = parts[1];

            
            StringBuilder descBuilder = new StringBuilder();
            for (int i = 2; i < dateIndex; i++) {
                descBuilder.append(parts[i]);
                if (i < dateIndex - 1)
                    descBuilder.append(",");
            }
            this.description = descBuilder.toString();

            this.dateTime = LocalDateTime.parse(parts[dateIndex], formatter);

            
            
            StringBuilder locBuilder = new StringBuilder();
            for (int i = dateIndex + 1; i < parts.length - 1; i++) {
                locBuilder.append(parts[i]);
                if (i < parts.length - 2)
                    locBuilder.append(",");
            }
            this.location = locBuilder.toString();

            this.organizerId = parts[parts.length - 1];
        } else {
            
            if (parts.length >= 6) {
                this.id = parts[0];
                this.name = parts[1];
                this.description = parts[2];
                try {
                    this.dateTime = LocalDateTime.parse(parts[3], formatter);
                } catch (Exception e) {
                    
                }
                this.location = parts[4];
                this.organizerId = parts[5];
            }
        }
    }

    @Override
    public int compareTo(Event other) {
        return this.dateTime.compareTo(other.dateTime);
    }
}
