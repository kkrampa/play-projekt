name := "Projekt"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.google.inject" % "guice" % "3.0",
  "com.restfb" % "restfb" % "1.6.14"
)

play.Project.playJavaSettings
