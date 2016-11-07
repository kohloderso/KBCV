import android.Keys._

androidBuildAar

// Enforce Java 7 compilation (in case you have the JDK 8 installed)
javacOptions ++=
  "-source" :: "1.7" ::
    "-target" :: "1.7" ::
    Nil


name := "arcLayout"

minSdkVersion in Android := "21" // seems necessary to be able to compile, but I don't want minApi to be 21 :-(

platformTarget in Android := "android-23"

showSdkProgress in Android := false