name := "nem2-camel"

version := "0.1.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.apache.camel" % "camel-core" % "2.20.2",
  "org.apache.camel" % "camel-netty4-http" % "2.20.2",
  "org.apache.camel" % "camel-http" % "2.20.2",
  "org.apache.camel" % "camel-jackson" % "2.20.2",

  "org.asynchttpclient" % "async-http-client" % "2.4.3",

  "org.apache.logging.log4j" % "log4j-core" % "2.9.1",
  "org.slf4j" % "slf4j-log4j12" % "1.7.25",

  "io.netty" % "netty-handler" % "4.1.22.Final",

  "commons-cli" % "commons-cli" % "1.4",
  "com.github.tototoshi" %% "scala-csv" % "1.3.5",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.0",

  "javax.xml.bind" % "jaxb-api" % "2.3.0",
  "javax.activation" % "activation" % "1.1.1"
)

mainClass in assembly := Some("io.nem.camel.Server")

cancelable in Global := true

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}

assemblyJarName in assembly := "nem2-camel.jar"