import sbt.Keys.libraryDependencies
import scala.collection.Seq

lazy val sparkVersion = "3.5.4"

ThisBuild / version := "1"
ThisBuild / scalaVersion := "2.12.18"
ThisBuild / libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
)


// modules
lazy val config = (project in file("config")).settings(
  libraryDependencies += "com.typesafe" % "config" % "1.4.3",
  libraryDependencies += "org.rogach" %% "scallop" % "5.2.0"
)

lazy val core = (project in file ("core"))
  .dependsOn(config)

lazy val extract_url = (project in file("core_ExtractURL"))
  .settings(
      libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.10.1"
  ).dependsOn(config)

lazy val load_db = (project in file("core_LoadDB"))
  .settings(
      libraryDependencies += "org.postgresql" % "postgresql" % "42.7.4"
  ).dependsOn(core)



// dictionaries
lazy val extract_dict = (project in file("extract_dict"))
  .settings(
    assembly / mainClass := Some("com.files.ExtractDict"),
  )
  .dependsOn(extract_url, core)

lazy val load_dict = (project in file("load_dict"))
  .settings(
    assembly / mainClass := Some("com.files.LoadDict")
  )
  .dependsOn(load_db)

lazy val extract_currency = (project in file("extract_currency"))
  .settings(
    assembly / mainClass := Some("com.files.ExtractCurrency")
  )
  .dependsOn(extract_url, core)

lazy val load_currency = (project in file("load_currency"))
  .settings(
    assembly / mainClass := Some("com.files.LoadCurr")
  )
  .dependsOn(load_db)


// vacancies
lazy val extract_vac = (project in file("extract_vac"))
  .settings(
    assembly / mainClass := Some("com.files.ExtractVac")
  )
  .dependsOn(extract_url, core)

lazy val transform_vac = (project in file("transform_vac"))
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    assembly / mainClass := Some("com.files.TransformVac")
  )
  .dependsOn(core)

lazy val load_vac = (project in file("load_vac"))
  .settings(
    assembly / mainClass := Some("com.files.LoadVac")
  )
  .dependsOn(load_db)



lazy val root = (project in file("."))
  .aggregate(config, load_db, extract_url, core, extract_dict, load_dict,
    extract_currency, extract_vac, transform_vac, load_vac, load_currency)


// bash: export JAVA_OPTS='--add-exports java.base/sun.nio.ch=ALL-UNNAMED'