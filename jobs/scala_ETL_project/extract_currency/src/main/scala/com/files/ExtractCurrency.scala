package com.files

import EL.Load.give
import Spark.SparkApp
import com.Config.LocalConfig
import com.extractURL.ExtractURL.takeURL
import org.apache.spark.sql.functions.{col, explode}
import org.apache.spark.sql.{DataFrame, SparkSession}

object ExtractCurrency extends App with SparkApp {

  private val conf = new LocalConfig(args) {
    define()
  }

  override val ss: SparkSession = defineSession(conf.fileConf.spark)

  private val currencyDF: DataFrame = toDF(takeURL("https://api.hh.ru/dictionaries", conf.fileConf.api).get)
  give(
    conf = conf.fileConf.fs,
    fileName = "currency",
    isRoot = true,
    data = currencyDF.withColumn("currency", explode(col("currency")))
      .select("currency.*")
      .withColumn("id", col("code"))
      .select("id", "name", "rate")
  )

  stopSpark()

  private def toDF(s: String): DataFrame = {
    import ss.implicits._
    ss.read.json(Seq(s).toDS())
  }
}