// Enforce Java 7 compilation (in case you have the JDK 8 installed)
javacOptions ++=
    "-source" :: "1.7" ::
    "-target" :: "1.7" ::
    Nil

libraryDependencies ++=
    aar("com.android.support" % "appcompat-v7" % "23.1.1") ::
    //"com.android.support" % "cardview-v7" % "23.1.0" ::
    "com.android.support" % "design" % "23.1.1" ::
    //"com.android.support" % "gridlayout-v7" % "23.1.0" ::
    //"com.android.support" % "recyclerview-v7" % "23.1.0" ::
    "com.android.support" % "support-v4" % "23.1.1" ::
    // Version 2.4.x requires Java 8
    //"com.typesafe.play" %% "play-json" % "2.3.10" ::
    //"termlib" % "termlib_2.10" % "latest.integration" ::
        "com.daimajia.swipelayout" % "library" % "1.2.0" ::
    Nil

name := "KBCV"

scalacOptions ++=
    // Print detailed deprecation warnings to the console
    "-deprecation" ::
    // Print detailed feature warnings to the console
    "-feature" ::
    Nil

// Don't upgrade to 2.12.x as it requires Java 8 which does not work with Android (but this might
// be resolvable with "retrolamda")
scalaVersion := "2.10.0"


//lazy val termlib = project.in(file("termlib"))
//lazy val main = project.in(file(".")).dependsOn(termlib).aggregate(termlib)