name := """play-handson"""
organization := "tasks"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play"    % "5.0.0" % Test,
  // 最新のplay-slickを指定。
  "com.typesafe.play"      %% "play-slick"            % "5.0.0",
  "com.typesafe.play"      %% "play-slick-evolutions" % "5.0.0",
  // play-slickの5.0.0ではslick 3.3.2を利用しているため、codegenも同様に3.3.2を指定しています。
  // https://github.com/playframework/play-slick#all-releases
  "com.typesafe.slick"     %% "slick-codegen"         % "3.3.2",
  // 指定すべきmysql-connectorのバージョンは以下のリンク先を参照
  // https://scala-slick.org/doc/3.3.1/database.html
  "mysql"                   % "mysql-connector-java"  % "6.0.6",

  "com.typesafe"            % "config"                % "1.4.0",
)

// add code generation task
lazy val slickCodeGen = taskKey[Unit]("execute Slick CodeGen")
slickCodeGen         := (runMain in Compile).toTask(" tasks.CustomSlickCodeGen").value
