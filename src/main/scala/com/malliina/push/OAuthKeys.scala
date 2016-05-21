package com.malliina.push

object OAuthKeys extends OAuthKeys

trait OAuthKeys {
  val ClientCredentials = "client_credentials"
  val ClientId = "client_id"
  val ClientSecret = "client_secret"
  val GrantType = "grant_type"
  val Scope = "scope"
}
