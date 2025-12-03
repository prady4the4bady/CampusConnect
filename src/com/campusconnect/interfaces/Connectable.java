package com.campusconnect.interfaces;

import com.campusconnect.core.User;

public interface Connectable {
    void connect(User user);
    void disconnect(User user);
}
