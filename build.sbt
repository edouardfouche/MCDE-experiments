name := "MCDE-experiments"

organization:= "com.edouardfouche"

version := "1.0"
scalaVersion := "2.11.8"
fork in run := true
scalacOptions += "-feature"

unmanagedJars in Compile += file("lib/uds.jar")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "de.lmu.ifi.dbs.elki" % "elki" % "0.7.5" // Ans see the merging strategy

// With those two it would compile, but then the assembly would complain that it
// "Cannot find a usable implementation of interface de.lmu.ifi.dbs.elki.database.ids.DBIDFactory"
// e.g., when calling II.
//libraryDependencies += "de.lmu.ifi.dbs.elki" % "elki-index-rtree" % "0.7.5"
//libraryDependencies += "de.lmu.ifi.dbs.elki" % "elki-core" % "0.7.5"

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"
libraryDependencies += "commons-io" % "commons-io" % "2.6"
resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

libraryDependencies ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "0.13.1",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13.1"

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  // "org.scalanlp" %% "breeze-viz" % "0.13.1"
)

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

//sbt-assembly

assemblySettings

import sbtassembly.Plugin.AssemblyKeys._
import sbtassembly.Plugin._

jarName in assembly := s"${name.value}-${version.value}.jar"
test in assembly := {}

mergeStrategy in assembly ~= { old =>
{
  case PathList("META-INF", "elki", xs @ _*) => MergeStrategy.first
  case x => old(x)
}
}

javacOptions ++= Seq("-encoding", "UTF-8")

//logLevel := Level.Debug
