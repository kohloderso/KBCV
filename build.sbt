// Enforce Java 7 compilation (in case you have the JDK 8 installed)
javacOptions ++=
    "-source" :: "1.7" ::
    "-target" :: "1.7" ::
    Nil

// Don't upgrade to 2.12.x as it requires Java 8 which does not work with Android (but this might
// be resolvable with "retrolamda")
scalaVersion := "2.10.5"

resolvers += Resolver.bintrayRepo("sjwall", "maven")

libraryDependencies ++=
    "com.android.support" % "appcompat-v7" % "23.1.1" ::
    "com.android.support" % "cardview-v7" % "23.1.0" ::
    "com.android.support" % "design" % "23.1.1" ::
        "com.android.support" % "preference-v7" % "23.1.1" ::
        "com.android.support" % "recyclerview-v7" % "23.3.0" ::
    "com.android.support" % "support-v4" % "23.3.0" ::
        "org.scala-lang" % "scala-actors" % "2.10.5" ::
    Nil

lazy val project = Project(
    "kbcv",
    file(".")
) dependsOn (tl) aggregate (tl) androidBuildWith (arc)

lazy val tl = Project(
    "termlib",
    file("termlib")
).settings(exportJars:=true)

lazy val arc = Project(
    "arcLayout",
    file("arcLayout")
).settings(libraryProject := true)