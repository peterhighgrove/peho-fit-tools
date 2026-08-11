# Fit Fix Android prototype

This is an Android front-end prototype for the existing fit-fix-gps-activities workflow.

## What it does
- Lets you pick a FIT/ZIP file from the device or open one via "Open with..." from a file manager.
- Defaults the file picker to the Downloads folder.
- Runs a selected existing fit-core command on the file and shows the log output.

## Build notes
1. Install Android Studio or the Android SDK.
2. Open the android-fit-fix-app folder in Android Studio.
3. Let Gradle download dependencies.
4. Run the app on an Android device/emulator.

## Current status
This is a first working prototype. The UI currently runs one selected command against the loaded file and shows the resulting log. It is not yet a full replacement for the CLI menu, but it provides a foundation for expanding to more commands and better file handling.
