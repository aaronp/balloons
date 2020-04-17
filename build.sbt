lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.1"
    )),
    name := "balloons",
    version := "0.0.1",
    mainClass in assembly := Some("balloons.Main")
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test
