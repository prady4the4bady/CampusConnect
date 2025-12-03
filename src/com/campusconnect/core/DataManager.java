package com.campusconnect.core;

import com.campusconnect.security.EncryptionUtil;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;
    private List<User> users;
    private Map<String, Group> groups;
    private TreeSet<Event> events;
    private List<Message> messages;
    private List<WalkieTalkieChannel> channels;
    private List<Post> posts;
    private Map<String, List<Comment>> postComments; // postId -> comments
    private List<Notification> notifications;
    private List<GroupRequest> groupRequests;
    private User currentUser;

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String GROUPS_FILE = DATA_DIR + "/groups.csv";
    private static final String EVENTS_FILE = DATA_DIR + "/events.csv";
    private static final String MESSAGES_FILE = DATA_DIR + "/messages.csv";
    private static final String CHANNELS_FILE = DATA_DIR + "/channels.csv";
    private static final String POSTS_FILE = DATA_DIR + "/posts.csv";
    private static final String COMMENTS_FILE = DATA_DIR + "/comments.csv";
    private static final String NOTIFICATIONS_FILE = DATA_DIR + "/notifications.csv";
    private static final String GROUP_REQUESTS_FILE = DATA_DIR + "/group_requests.csv";

    private DataManager() {
        users = new ArrayList<>();
        groups = new HashMap<>();
        events = new TreeSet<>();
        messages = new ArrayList<>();
        channels = new ArrayList<>();
        posts = new ArrayList<>();
        postComments = new HashMap<>();
        notifications = new ArrayList<>();
        groupRequests = new ArrayList<>();
        loadData();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public Map<String, Group> getGroups() {
        return groups;
    }

    public TreeSet<Event> getEvents() {
        return events;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<WalkieTalkieChannel> getChannels() {
        return channels;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return notifications.stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted()
                .collect(Collectors.toList());
    }

    public int getUnreadNotificationCount(String userId) {
        return (int) notifications.stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }

    public void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public void addGroup(Group group) {
        groups.put(group.getId(), group);
        saveGroups();
    }

    public void addEvent(Event event) {
        events.add(event);
        saveEvents();
    }

    public void addMessage(Message message) {
        messages.add(message);
        saveMessages();
    }

    public void addChannel(WalkieTalkieChannel channel) {
        channels.add(channel);
        saveChannels();
    }

    // Social Media Methods
    public void addPost(Post post) {
        posts.add(0, post); // Add to beginning for reverse chronological
        savePosts();
    }

    public void deletePost(String postId) {
        posts.removeIf(p -> p.getId().equals(postId));
        postComments.remove(postId);
        savePosts();
        saveComments();
    }

    public void addComment(String postId, Comment comment) {
        postComments.computeIfAbsent(postId, k -> new ArrayList<>()).add(comment);

        // Update post object
        Post post = getPostById(postId);
        if (post != null) {
            post.addComment(comment);
            savePosts();
        }
        saveComments();
    }

    public List<Comment> getCommentsForPost(String postId) {
        return postComments.getOrDefault(postId, new ArrayList<>());
    }

    public Post getPostById(String postId) {
        return posts.stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElse(null);
    }

    public List<Post> getPostsByUser(String userId) {
        return posts.stream()
                .filter(p -> p.getAuthorId().equals(userId))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Post> getFeedPosts(String userId) {
        User user = getUserById(userId);
        if (user == null)
            return new ArrayList<>();

        return posts.stream()
                .filter(p -> user.isFollowing(p.getAuthorId()) || p.getAuthorId().equals(userId))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    public void addNotification(Notification notification) {
        notifications.add(0, notification);
        saveNotifications();
    }

    public void markNotificationRead(String notificationId) {
        notifications.stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
        saveNotifications();
    }

    public void followUser(String followerId, String followeeId) {
        User follower = getUserById(followerId);
        User followee = getUserById(followeeId);

        if (follower != null && followee != null && !followerId.equals(followeeId)) {
            follower.follow(followeeId);
            followee.addFollower(followerId);
            saveUsers();

            // Create notification
            String notifId = "N" + System.currentTimeMillis();
            Notification notif = new Notification(notifId, followeeId, "FOLLOW",
                    follower.getName() + " started following you", followerId);
            addNotification(notif);
        }
    }

    public void unfollowUser(String followerId, String followeeId) {
        User follower = getUserById(followerId);
        User followee = getUserById(followeeId);

        if (follower != null && followee != null) {
            follower.unfollow(followeeId);
            followee.removeFollower(followerId);
            saveUsers();
        }
    }

    // Group Request Management
    public void addGroupRequest(GroupRequest request) {
        groupRequests.add(request);
        saveGroupRequests();
    }

    public void approveGroupRequest(String requestId) {
        GroupRequest request = groupRequests.stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElse(null);

        if (request != null && request.isPending()) {
            request.approve();
            // Add user to group
            Group group = groups.get(request.getGroupId());
            if (group != null) {
                group.addMember(request.getUserId());
                saveGroups();
            }
            saveGroupRequests();
        }
    }

    public void rejectGroupRequest(String requestId) {
        GroupRequest request = groupRequests.stream()
                .filter(r -> r.getId().equals(requestId))
                .findFirst()
                .orElse(null);

        if (request != null && request.isPending()) {
            request.reject();
            saveGroupRequests();
        }
    }

    public List<GroupRequest> getPendingRequestsForGroup(String groupId) {
        return groupRequests.stream()
                .filter(r -> r.getGroupId().equals(groupId) && r.isPending())
                .sorted()
                .collect(Collectors.toList());
    }

    public List<GroupRequest> getGroupRequestsForUser(String userId) {
        return groupRequests.stream()
                .filter(r -> r.getUserId().equals(userId))
                .sorted()
                .collect(Collectors.toList());
    }

    public User getUserById(String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.checkPassword(password)) {
                currentUser = u;
                return u;
            }
        }
        return null;
    }

    private void loadData() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));

            // Load Users
            if (Files.exists(Paths.get(USERS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            User u = UserFactory.fromCSV(decrypted);
                            if (u != null)
                                users.add(u);
                        }
                    }
                }
            }

            // Load Groups
            if (Files.exists(Paths.get(GROUPS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(GROUPS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Group g = new Group("", "", "");
                            g.fromCSV(decrypted);
                            groups.put(g.getId(), g);
                        }
                    }
                }
            }

            // Load Events
            if (Files.exists(Paths.get(EVENTS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(EVENTS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Event e = new Event("", "", "", java.time.LocalDateTime.now(), "", "");
                            e.fromCSV(decrypted);
                            events.add(e);
                        }
                    }
                }
            }

            // Load Messages
            if (Files.exists(Paths.get(MESSAGES_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(MESSAGES_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Message m = Message.parse(decrypted);
                            if (m != null)
                                messages.add(m);
                        }
                    }
                }
            }

            // Load Channels
            if (Files.exists(Paths.get(CHANNELS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(CHANNELS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            WalkieTalkieChannel c = new WalkieTalkieChannel("", "", false, "");
                            c.fromCSV(decrypted);
                            channels.add(c);
                        }
                    }
                }
            }

            // Load Posts
            if (Files.exists(Paths.get(POSTS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(POSTS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Post p = new Post("", "", "");
                            p.fromCSV(decrypted);
                            posts.add(p);
                        }
                    }
                }
            }

            // Load Comments
            if (Files.exists(Paths.get(COMMENTS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(COMMENTS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Comment c = new Comment("", "", "", "");
                            c.fromCSV(decrypted);
                            postComments.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);

                            // Also add to post object
                            Post post = getPostById(c.getPostId());
                            if (post != null) {
                                post.addComment(c);
                            }
                        }
                    }
                }
            }

            // Load Notifications
            if (Files.exists(Paths.get(NOTIFICATIONS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(NOTIFICATIONS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            Notification n = new Notification("", "", "", "", "");
                            n.fromCSV(decrypted);
                            notifications.add(n);
                        }
                    }
                }
            }

            // Load Group Requests
            if (Files.exists(Paths.get(GROUP_REQUESTS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(GROUP_REQUESTS_FILE));
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String decrypted = EncryptionUtil.decrypt(line);
                        if (decrypted != null) {
                            GroupRequest gr = new GroupRequest("", "", "");
                            gr.fromCSV(decrypted);
                            groupRequests.add(gr);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                writer.println(EncryptionUtil.encrypt(u.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGroups() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GROUPS_FILE))) {
            for (Group g : groups.values()) {
                writer.println(EncryptionUtil.encrypt(g.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEvents() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event e : events) {
                writer.println(EncryptionUtil.encrypt(e.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessages() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MESSAGES_FILE))) {
            for (Message m : messages) {
                writer.println(EncryptionUtil.encrypt(m.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveChannels() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CHANNELS_FILE))) {
            for (WalkieTalkieChannel c : channels) {
                writer.println(EncryptionUtil.encrypt(c.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePosts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(POSTS_FILE))) {
            for (Post p : posts) {
                writer.println(EncryptionUtil.encrypt(p.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveComments() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMMENTS_FILE))) {
            for (List<Comment> comments : postComments.values()) {
                for (Comment c : comments) {
                    writer.println(EncryptionUtil.encrypt(c.toCSV()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveNotifications() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(NOTIFICATIONS_FILE))) {
            for (Notification n : notifications) {
                writer.println(EncryptionUtil.encrypt(n.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGroupRequests() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GROUP_REQUESTS_FILE))) {
            for (GroupRequest r : groupRequests) {
                writer.println(EncryptionUtil.encrypt(r.toCSV()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getRecommendedPeers() {
        if (currentUser == null)
            return new ArrayList<>();

        return users.stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .sorted((u1, u2) -> {
                    long match1 = u1.getInterests().stream().filter(currentUser.getInterests()::contains).count();
                    long match2 = u2.getInterests().stream().filter(currentUser.getInterests()::contains).count();
                    return Long.compare(match2, match1);
                })
                .limit(5)
                .collect(Collectors.toList());
    }
}
