package models

import play.api.libs.json.{Json, OFormat}


case class Usuario(id: Option[Long], email: String, name: String, lastname: String)

object Usuario {
implicit val format: OFormat[Usuario] = Json.format[Usuario]
}
