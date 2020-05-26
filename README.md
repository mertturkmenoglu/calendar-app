# Introduction to Mobile Programming Semester Project - Calendar App
***
* Video link: https://www.youtube.com/watch?v=zxYeMyc-8iE
***
# Table of Contents
* [Description](#description)
* [Prerequisites](#prerequisites)
* [Installing the Project](#installing-the-project)
* [Libraries and Dependencies](#libraries-and-dependencies)
* [Contributing](#contributing)
* [Authors](#authors)
* [Contact](#contact)
* [License](#license)
***
# Description
* This repository contains my **Introduction to Mobile Programming** semester project.
* Please read the whole *prerequisites* and *installation* steps.
***
# Prerequisites
Ensure you have met the following requirements:
* You have installed **JDK**.
* You have installed **JRE**.
* You have installed **Android Studio 3.6.1** or later.
* You have installed **Gradle 5.6.4** or later.
* You have installed **Android SDK 29** or later.
* You have a **Firebase** account.
# Installing the Project
* **Clone** the repository.
* **Import** your project to Android Studio.
* Click `Tools -> Firebase -> Authentication -> Connect your app to Firebase`.
    * You **have to** connect your application to a Firebase Project.
    * If you have a problem with the Firebase Assistant, go to **Firebase Console**.
    * Select your project.
    * Follow the Firebase guide to **link your project with Android Studio**.
    * Add your `google-services.json` file to your `app` module.
* You have to enable `Email/Password` sign-in provied under `Authentication -> Sign-in Method -> Sign-in providers`.
* You have to enable `Cloud Firestore` database.
* You have to allow write/read operations to database. You can set these rules from `Database -> Rules`.
* You have to have a `Users` collection inside your database root.
* Inside `Users` collection, every document should have a subcollection named `Events`.
# Libraries and Dependencies
* [Firebase Auth](https://firebase.google.com/)
* [Firebase Firestore](https://firebase.google.com/)
* [Firebase Crashlytics](https://firebase.google.com/)
* [Firebase Analytics](https://firebase.google.com/)
* [Android Material](https://material.io/develop/android/docs/getting-started/)
* [Android ConstraintLayout](https://developer.android.com/jetpack/androidx/releases/constraintlayout)
* [Glide](https://github.com/bumptech/glide)
* [Easy Splash Screen](https://github.com/pantrif/EasySplashScreen)
* [Google Play Services](https://developers.google.com/android/guides/setup)
* [Androidx Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
* [Androidx Recyclerview](https://developer.android.com/jetpack/androidx/releases/recyclerview)
* [Androidx Cardview](https://developer.android.com/jetpack/androidx/releases/cardview)
# Contributing
See the `CONTRIBUTING.md` file.
# Authors
* Mert Türkmenoğlu
# Contact
* [@mertturkmenoglu](https://github.com/mertturkmenoglu) on **GitHub**
* [@mert-turkmenoglu](https://www.linkedin.com/in/mert-turkmenoglu/) on **LinkedIn**
* [@mertturkmenoglu](https://medium.com/@mertturkmenoglu) on **Medium**
* [@capreaee](https://twitter.com/capreaee) on **Twitter**
# License
* This project uses the following license: [GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.en.html)
