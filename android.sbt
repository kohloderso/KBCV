// Prevent common com.android.builder.packaging.DuplicateFileException.
// Add further file names if you experience the exception after adding new dependencies
packagingOptions in Android := PackagingOptions(
    excludes =
        "META-INF/LICENSE" ::
        "META-INF/LICENSE.txt" ::
        "META-INF/NOTICE" ::
        "META-INF/NOTICE.txt" ::
        Nil
)

// flowLayout library needs API-Level 14
minSdkVersion in Android := "14"

platformTarget in Android := "android-23"

proguardCache in Android ++=
    "android.support" ::
    "play" ::
    Nil

proguardOptions in Android ++=
    "-keepattributes EnclosingMethod,InnerClasses,Signature" ::
    Nil

// Shortcut: allows you to execute "sbt run" instead of "sbt android:run"
run <<= run in Android

targetSdkVersion in Android := "23"

versionCode in Android := Some( 1 )

versionName in Android := Some( "1.0.1" )