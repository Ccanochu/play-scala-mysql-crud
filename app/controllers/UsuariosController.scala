package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json.Json
import models._
import services.UsuarioService
import play.api.data._
import play.api.data.Forms._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import java.sql.DriverManager


class UsuariosController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Establecer la conexión a la base de datos (mejoraría esto en un entorno real)
  val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_usuarios", "root", "")
  val usuarioDAO = new UsuarioDAO(connection)

val formularioUsuario = Form(
    tuple(
      "email" -> email,
      "name" -> nonEmptyText,
      "lastname" -> nonEmptyText
    )
  )

  def mostrarFormulario = Action { implicit request =>
    Ok(views.html.formulario(formularioUsuario))
  }


  def crearUsuario = Action.async { implicit request =>
    formularioUsuario.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.formulario(formWithErrors)))
      },
      userData => {
        val (email, name, lastname) = userData

        val rowsAffected = usuarioDAO.crearUsuario(email, name, lastname)

        if (rowsAffected > 0) {
          //Future.successful(Ok(s"Usuario $name $lastname creado correctamente"))
          Future.successful(Redirect(routes.UsuariosController.mostrarLista()))

        } else {
          Future.successful(InternalServerError("Error al crear el usuario"))
        }
      }
    )
  }

def mostrarLista = Action { implicit request =>
  // Obtener la lista de usuarios (puedes implementar esto en tu DAO)
  val usuarios: Seq[Usuario] = usuarioDAO.obtenerUsuarios() // Seq[Usuario]
  val usuariosList: List[Usuario] = usuarios.toList // Convertir a List[Usuario]

  Ok(views.html.lista(usuariosList)) // Pasar la lista a la plantilla
}


def eliminarUsuario(id: Long) = Action { implicit request =>
  val rowsAffected = usuarioDAO.eliminarUsuario(id)
  
  if (rowsAffected > 0) {
    Redirect(routes.UsuariosController.mostrarLista()).flashing("success" -> "Usuario eliminado correctamente")
  } else {
    Redirect(routes.UsuariosController.mostrarLista()).flashing("error" -> "Error al eliminar el usuario")
  }
}

def mostrarEditarUsuario(id: Long) = Action { implicit request =>
  val usuarioOption = usuarioDAO.obtenerUsuarioPorId(id)

  usuarioOption match {
    case Some(usuario) => Ok(views.html.editar(usuario))
    case None => NotFound("Usuario no encontrado") // O el mensaje de error que prefieras
  }
}
  

def editarUsuario(id: Long) = Action { implicit request =>
  val usuarioActualizadoOption: Option[Usuario] = for {
    email <- request.body.asFormUrlEncoded.flatMap(_.get("email")).flatMap(_.headOption)
    name <- request.body.asFormUrlEncoded.flatMap(_.get("name")).flatMap(_.headOption)
    lastname <- request.body.asFormUrlEncoded.flatMap(_.get("lastname")).flatMap(_.headOption)
  } yield Usuario(Some(id), email, name, lastname) // Aquí utilizamos Some(id) para convertirlo en un Option

 usuarioActualizadoOption match {
  case Some(usuarioActualizado) =>
    val filasActualizadas = usuarioDAO.actualizarUsuario(
      usuarioActualizado.id.get, // Obtener el id del usuario
      usuarioActualizado.email, 
      usuarioActualizado.name, 
      usuarioActualizado.lastname
    )
      if (filasActualizadas > 0) {
        Redirect(routes.UsuariosController.mostrarLista())
      } else {
        InternalServerError("Error al actualizar el usuario")
      }
    case None =>
      BadRequest("Datos de usuario no válidos") // O el mensaje de error que prefieras
  }
}



}