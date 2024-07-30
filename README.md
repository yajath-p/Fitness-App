# Fitness Tracker App

## Overview

The Fitness Tracker App is an Android application designed to help users manage their workout routines, track fitness goals, and set preferences. The app features workout addition, workout tracking with a countdown timer, and customizable settings for user preferences and themes.

## Features

- **Add Workouts**: Users can add new workouts by specifying the type, duration, and day.
- **View Workouts**: View scheduled workouts and goals for the day.
- **Workout Timer**: Start and stop a countdown timer for workouts, with real-time updates.
- **Preferences**: Set and save preferences for rest days and theme colors.
- **Ad Integration**: Includes Google AdMob integration for displaying ads.

## Technologies Used

- **Kotlin**: Primary language for Android development.
- **Firebase**: For real-time database management and data storage.
- **AdMob**: For in-app advertisement integration.
- **Android SDK**: For building and designing the Android user interface.

## Code Overview

### MainActivity

The main activity allows users to:

- **Add new workouts** to Firebase.
- **View workouts** and navigate to different app sections.
- **Set preferences** including rest days and theme colors.

### WorkoutActivity

The workout activity provides:

- **A countdown timer** for selected workouts.
- **Real-time updates** and user interface changes based on workout selection and timer state.

### Fitness Class

Handles user preferences, including:

- **Rest day** and **theme color** storage and retrieval using `SharedPreferences`.

### Layouts

- **`activity_main.xml`**: Layout for the main activity, including buttons for adding workouts, viewing workouts, and setting preferences.
- **`activity_workout.xml`**: Layout for the workout activity, featuring a countdown timer and workout selection.
- **`workout_item.xml`**: XML layout for the workout item representation.
