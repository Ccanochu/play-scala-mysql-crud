package services

import javax.inject.Inject
import models.Usuario
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

class UsuarioService @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

class UsuarioTable(tag: Tag) extends Table[Usuario](tag, "usuarios") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def name = column[String]("name")
  def lastname = column[String]("lastname")
  def * = (id.?, email, name, lastname) <> ((Usuario.apply _).tupled, Usuario.unapply)
}


  val usuarios = TableQuery[UsuarioTable]

  def crearUsuario(usuario: Usuario): Future[Unit] = db.run(usuarios += usuario).map { _ => () }
}
