# Student-Sphere

**Student-Sphere** is a student-oriented Android application designed to enhance the student experience by providing features such as profile management, note sharing, holiday tracking, task management, and student login functionalities. The app is built with Java and Android SDK, offering a simple and interactive interface for students to manage their academic and extracurricular activities.

---

## Features

- **User Authentication**: Allows students to register and log in to their accounts.
- **Profile Management**: Students can edit and update their profiles, including personal and academic information.
- **Holiday Tracker**: The app tracks holidays and important academic dates to keep students informed.
- **To-Do List Management**: Students can add, manage, and track their academic tasks and to-dos.
- **Note Uploading**: Students can upload and share notes with others.
- **Main Activity**: Acts as the central hub where users can navigate to various sections like Profile, Holidays, Notes, etc.

---

## File Overview

The following are the core files of the project:

1. **CalculateHolidays.java**  
   This file contains the logic for calculating holidays and displaying relevant information related to academic holidays.

2. **EditProfile.java**  
   Allows students to edit their profile details such as name, email, and academic details.

3. **Login.java**  
   Manages user login functionality, ensuring only authenticated users can access the app's features.

4. **MainActivity.java**  
   The main screen of the application where students can navigate to various sections like Profile, Holidays, Notes, etc.

5. **Register.java**  
   Handles the user registration process, allowing new users to create an account.

6. **UploadNotes.java**  
   Enables students to upload and share their notes with others in the student community.

7. **TODO.java**  
   Responsible for managing to-do lists or tasks within the app. Students can create and manage their daily academic tasks, mark them as completed, and track deadlines. This file may also interact with a local database (like SQLite or Firebase) to persist task data.

---

## Getting Started

To get started with the **Student-Sphere** app, follow these steps:

### Prerequisites

- **Android Studio** (latest version)
- **Java Development Kit (JDK)** installed on your system
- A **Google Firebase** account (if using Firebase for authentication and storage)
  
### Installation

1. Clone the repository to your local machine:

    ```bash
    git clone https://github.com/Shreyas-kp/Student-Sphere.git
    ```

2. Open the project in **Android Studio**.

3. If the project is using Firebase, follow the instructions in the Firebase documentation to configure your Firebase project and connect it with your Android app. You'll need to add the `google-services.json` file to the `app/` directory.

4. Build the project and run the app on your emulator or physical device.

---

## Usage

Once the app is up and running, here are the key functionalities:

- **Login/Registration**: Upon opening the app, you will be prompted to either log in with your existing account or create a new account.
  
- **Profile Editing**: After logging in, you can navigate to the Profile section where you can update your details.

- **Holiday Tracker**: The app displays important holidays for students, and you can view this information by navigating to the Holidays section.

- **To-Do List Management**: In the To-Do section, students can add tasks, mark them as complete, or delete them. This helps in tracking assignments, projects, and other academic responsibilities.

- **Upload Notes**: In the Upload Notes section, you can upload your study materials (such as PDFs, images, etc.) for others to access.

---

## Technologies Used

- **Java**: The programming language used for developing the Android application.
- **Android SDK**: The software development kit for building Android applications.
- **Firebase**: Used for user authentication and storing data (if applicable).
- **SQLite**: May be used for local task management (if applicable).
- **XML**: Used for designing the app's UI in Android.

---

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Make your changes and commit them (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

---

## License

This project is open-source and available under the [MIT License](LICENSE).

---

## Acknowledgments

- Thanks to all contributors and the Android community for their open-source libraries and frameworks that made this project possible.
- Firebase for authentication and data storage (if used).
