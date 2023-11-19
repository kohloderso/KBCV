Knuth-Bendix Comletion Visualizer for Android
==========
Repository for my bachelor thesis project (finished in 2017).

Additional Downloads
==========
    o thesis.pdf  - a copy of the thesis
    o kbcv.apk  - compiled android package 

APK Installation Instructions
=============================
Requirements: mobile device (or emulator) running Android 3.0 or higher

1. Go to the settings -> Security -> select "Unknown sources"
2. Transfer the apk-file to your device
3. On your device open a filemanager and with it select the apk-file.
   A dialog will open where you have to confirm that you want to install the app.

Compile the sources
===================
If you want to edit the source code and compile your own version of KBCV
carry out the following steps:

1. Install a JDK

2. Install the Android SDK
   i) Go to [1]. Choose "SDK Tools Only" and download the appropriate version.
   ii) Extract
   iii) Set ANDROID_HOME and add the platform-tools to your PATH
   export ANDROID_HOME=<path-to-sdk>
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   iv) Start the Android SDK-Manager by typing ./android
   In the manager select
   - Android SDK Platform-tools
   - Android SDK Build-tools (eine Version)
   - SDK Platform der neuesten Android Version
   - Local Maven repository for Support Library
   - Android Support Library
   and download them

3. Download and setup IntelliJ
   i) Download IntelliJ Ultimate Edition [2]
   ii) Extract
   iii) Change into the extracted folder and go to /bin. Execute ./idea.sh
   iv) At first start IntelliJ asks for a license. As a student you can create a
   Jetbrains account with your @student.uibk.ac.at address to use all Jetbrains products
   free of charge.
   v) Install the SBT-plugin for IntelliJ:
   - Press Ctrl+Alt+S to open the Settings-window
   - Go to plugins
   - Select "Browse Repositories"
   - search for "SBT" and install the SBT-plugin

4. Import the project into IntelliJ
   i) Extract the sources
   ii) In IntelliJ select File -> New -> Project from Existing Sources and open the directory
   where you extracted the source files
   iii) In the dialog that opens select "Import project from external model" and choose "SBT".
   In the following windows make sure to select "Use auto-import" and finish.
   iv) IntelliJ will now build the project and open it in a new window

5. Deploy to device
   i) Attach an Android device in debug mode to your computer (alternatively start an emulator)
   ii) Open the SBT Console via View -> Tool Windows -> SBT
   iii) type 'run' into the console 

[1] http://developer.android.com/sdk/index.html#Other
[2] https://www.jetbrains.com/idea/#chooseYourEdition