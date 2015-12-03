Knuth-Bendix Comletion Visualizer for Android
==========
Overview
-----------------------------------------------
This app will allow its users to interactively perform the Knuth-Bendix completion procedure. (Up until now it's just the very bare bones of the app.)
Installation
-------------
You will need:
- JDK
- Android SDK
- sbt

Clone or download this repository.

Download and install the Android SDK: http://developer.android.com/sdk/installing/index.html
Start the SDK Manager to install missing packages. A more detailed description of how to do this and what to install, can be found here: http://scala-on-android.taig.io/prerequisites/

Then download and install sbt.

Now you need to connect an android device to your computer or start a virtual device. Then change to the root folder of this project and run the command `sbt run`. This will build and deploy the project to your connected device. On first start this may take a while, because sbt first needs to download all necessary resources.