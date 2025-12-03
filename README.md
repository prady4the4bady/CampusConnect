# CampusConnect

**CampusConnect** is a modern campus social networking application built for BITS Pilani Dubai students, faculty, and alumni. It provides a secure, feature-rich platform for connecting, sharing, and engaging with the campus community.

## Features

### 🔐 Secure Authentication
- User registration and login with AES-encrypted local data storage
- Role-based access (Student, Teacher, Alumni)
- Encrypted password storage

### 📱 Social Networking
- **Feed**: Share posts, images, and updates with the campus community
- **Groups**: Create and join student clubs and organizations
- **Events**: Discover and participate in campus events
- **Messaging**: Direct chat with other users
- **Notifications**: Stay updated on interactions and announcements

### 🎯 Smart Features
- **AI-Powered Recommendations**: Personalized content suggestions using collaborative filtering
- **Trending Analysis**: Discover popular topics and discussions
- **Interest-Based Connections**: Find people with similar interests
- **Advanced Search**: Search users, groups, and events with filters

### 📡 Walkie-Talkie Channels
- Create private, invite-only communication channels
- Perfect for events, emergencies, or group coordination
- Passcode-protected channels

### 🎨 Modern UI
- Custom Swing components with modern aesthetics
- Dark mode design with glassmorphism effects
- Smooth animations and transitions
- Mobile-inspired navigation

## Project Structure

```
OOPS project/
├── src/
│   └── com/
│       └── campusconnect/
│           ├── ai/                    # AI recommendation engine
│           ├── core/                  # Core domain models
│           ├── gui/                   # GUI components and panels
│           │   └── components/        # Custom UI components
│           ├── interfaces/            # Java interfaces
│           ├── security/              # Encryption utilities
│           └── MainTest.java          # Test/verification entry point
├── data/                              # Encrypted data storage
├── bin/                               # Compiled classes
└── .gitignore

```

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **Java Swing**: Included in standard JDK

## How to Compile

### Using Command Line

1. **Navigate to the project directory:**
   ```bash
   cd "c:\Users\prady\Desktop\OOPS project"
   ```

2. **Compile all Java files:**
   ```bash
   javac -d bin -sourcepath src src/com/campusconnect/gui/MainFrame.java
   ```

   Or compile all files at once:
   ```bash
   javac -d bin -sourcepath src src/com/campusconnect/**/*.java
   ```

### Using an IDE (Recommended)

#### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select `File > Open` and choose the `OOPS project` folder
3. Wait for the project to index
4. The IDE will automatically detect the source structure
5. Build the project: `Build > Build Project`

#### Eclipse
1. Open Eclipse
2. Select `File > Open Projects from File System`
3. Choose the `OOPS project` folder
4. Right-click the project and select `Build Project`

#### VS Code
1. Open VS Code
2. Install the "Extension Pack for Java" extension
3. Open the `OOPS project` folder
4. The project will compile automatically

## How to Run

### Option 1: Run the GUI Application (Recommended)

**Using Command Line:**
```bash
java -cp bin com.campusconnect.gui.MainFrame
```

**Using an IDE:**
- Navigate to `src/com/campusconnect/gui/MainFrame.java`
- Right-click and select `Run MainFrame.main()`

This will launch the CampusConnect GUI application where you can:
1. Sign up for a new account or log in
2. Explore the feed, groups, events
3. Connect with other users
4. Join walkie-talkie channels

### Option 2: Run Verification Tests

**Using Command Line:**
```bash
java -cp bin com.campusconnect.MainTest
```

**Using an IDE:**
- Navigate to `src/com/campusconnect/MainTest.java`
- Right-click and select `Run MainTest.main()`

This will run verification tests for:
- Encryption/decryption functionality
- Data seeding and persistence
- Walkie-talkie channel creation

## Default Test Credentials

When you run the application, sample data is automatically seeded. You can use these credentials to log in:

- **Students**: `alice@bits.ae`, `bob@bits.ae`, `charlie@bits.ae`
- **Teachers**: `prof.smith@bits.ae`, `prof.jones@bits.ae`
- **Alumni**: `john.doe@alumni.bits.ae`

**Default Password**: `password123` (for all test accounts)

## Data Storage

CampusConnect stores all data locally in the `data/` directory with AES encryption:
- `users.csv` - User accounts (encrypted)
- `posts.csv` - Feed posts
- `groups.csv` - Student groups and clubs
- `events.csv` - Campus events
- `messages.csv` - Direct messages
- `notifications.csv` - User notifications
- `channels.csv` - Walkie-talkie channels

## Technologies Used

- **Language**: Java 8+
- **GUI Framework**: Java Swing
- **Security**: AES-256 Encryption
- **AI**: Collaborative Filtering for Recommendations
- **Persistence**: CSV-based encrypted local storage

## Architecture

The application follows object-oriented design principles:
- **MVC Pattern**: Separation of UI (GUI), business logic (Core), and data management
- **Factory Pattern**: `UserFactory` for creating different user types
- **Singleton Pattern**: `DataManager` for centralized data access
- **Interface-based Design**: `Connectable`, `Searchable` interfaces

## Contributing

This is an academic project for BITS Pilani Dubai. For questions or contributions, please contact the development team.

## License

This project is developed as part of an Object-Oriented Programming course at BITS Pilani Dubai.

---

**Note**: Make sure to delete the `data/` directory if you want to reset the application to its initial state with fresh seed data.
