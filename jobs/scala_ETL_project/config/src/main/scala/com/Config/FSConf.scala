package com.Config

import com.typesafe.config.Config

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, ZoneId}

class FSConf(conf: Config, path: String) extends filePath(conf, path) {
  private lazy val fs: String = getString("url")
  private lazy val currentDate: String = LocalDate.now(ZoneId.of(getString("zoneId")))
    .format(DateTimeFormatter.ISO_LOCAL_DATE)

  lazy val vacanciesRawFileName: String = getString("fileName.vacanciesRaw")
  lazy val vacanciesTransformedFileName: String = getString("fileName.vacanciesTransformed")

  def getPath(isRoot: Boolean, fileName: String = ""): String = fs + (if (!isRoot) s"$currentDate/" else "") + fileName
}
