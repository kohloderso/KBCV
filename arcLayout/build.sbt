import android.Keys._

androidBuildAar

name := "arcLayout"

minSdkVersion in Android := "21" // seems necessary to be able to compile, but I don't want minApi to be 21 :-(

platformTarget in Android := "android-23"

showSdkProgress in Android := false