import sbt.Keys.libraryDependencies

import java.nio.file.Files
import scala.compat.java8.StreamConverters.RichStream

name := """play-sample"""
organization := "com.example"

version := "1.0-SNAPSHOT"
val slickVersion = "3.3.3"
val playSlickVersion = "5.0.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    scalaVersion := "2.13.4",
    libraryDependencies ++= List (
      guice withSources() withJavadoc(),
      "com.typesafe.play" %% "play-slick" % playSlickVersion withSources() withJavadoc(),
      "com.typesafe.slick" %% "slick-codegen" % slickVersion withSources() withJavadoc(),
      "com.h2database" % "h2" % "1.4.200",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test withSources() withJavadoc()
    ),
    Compile / sourceGenerators += slickCodeGenTask.taskValue
  )

lazy val slickCodeGenTask = Def.task {
  val dir = (Compile / sourceManaged).value
  val r = (Compile / runner).value
  val cp = (Compile / dependencyClasspath).value
  val s = streams.value
  val url = "jdbc:h2:mem:test;INIT=runscript from 'conf/sql/tables.sql'"
  val jdbcDriver = "org.h2.Driver"
  val slickDriver = "slick.jdbc.H2Profile"
  val pkg = "com.asem"

  r.run("slick.codegen.SourceCodeGenerator",
    cp.files,
    Seq(slickDriver, jdbcDriver, url, dir.getAbsolutePath, pkg),
    s.log
  )

  Files.find(dir.toPath, Integer.MAX_VALUE, (_, fileAttr) => fileAttr.isRegularFile)
    .toScala
    .map(_.toFile)
}
