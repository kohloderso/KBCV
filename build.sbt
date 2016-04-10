// Enforce Java 7 compilation (in case you have the JDK 8 installed)
javacOptions ++=
    "-source" :: "1.7" ::
    "-target" :: "1.7" ::
    Nil

// Don't upgrade to 2.12.x as it requires Java 8 which does not work with Android (but this might
// be resolvable with "retrolamda")
scalaVersion := "2.10.0"


libraryDependencies ++=
    "com.android.support" % "appcompat-v7" % "23.1.1" ::
    "com.android.support" % "cardview-v7" % "23.1.0" ::
    "com.android.support" % "design" % "23.1.1" ::
    //"com.android.support" % "gridlayout-v7" % "23.1.0" ::
    "com.android.support" % "recyclerview-v7" % "23.1.0" ::
    "com.android.support" % "support-v4" % "23.1.1" ::
        "org.scala-lang" % "scala-actors" % "2.10.0"::
    // Version 2.4.x requires Java 8
    //"com.typesafe.play" %% "play-json" % "2.3.10" ::
    Nil


lazy val project = Project(
    "kbcv",
    file(".")
) dependsOn(tl) aggregate(tl)

lazy val tl = Project(
    "termlib",
    file("termlib")
)

