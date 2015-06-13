package com.github.dnvriend.jdbc

import com.typesafe.config.Config

trait JdbcConfig {

  def name: String
  def config: Config

  def cfg = config.getConfig(s"jdbc-connection.$name")
  def username = cfg.getString("username")
  def password = cfg.getString("password")
  def url = cfg.getString("url")
  def maxIdle: Int = cfg.getInt("maxIdle")
  def initialSize: Int = cfg.getInt("initialSize")
  def maxActive = cfg.getInt("maxActive")
  def driverClassName: String = cfg.getString("driverClassName")

  override def toString: String =
    s"""
       |Credentials:
       |============
       |username: $username
        |password: $password
        |driverClassName: $driverClassName
        |url: $url
        |===========
        |Pool config:
        |============
        |maxActive: $maxActive
        |maxIdle: $maxIdle
        |initialSize: $initialSize
  """.stripMargin
}
