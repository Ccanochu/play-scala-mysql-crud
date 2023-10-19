package models

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


class UsuarioDAO(connection: Connection) {
  
  def crearUsuario(email: String, name: String, lastname: String): Int = {
    val query = "INSERT INTO usuarios (email, name, lastname) VALUES (?, ?, ?)"
    val statement: PreparedStatement = connection.prepareStatement(query)
    
    statement.setString(1, email)
    statement.setString(2, name)
    statement.setString(3, lastname)
    
    val rowsAffected = statement.executeUpdate()
    
    statement.close()
    rowsAffected
  }

  def obtenerUsuarios(): Seq[Usuario] = {
    val query = "SELECT * FROM usuarios"
    val statement: PreparedStatement = connection.prepareStatement(query)
    val resultSet: ResultSet = statement.executeQuery()

    var usuarios = Seq.empty[Usuario]

    while (resultSet.next()) {
      val id = resultSet.getLong("id")
      val email = resultSet.getString("email")
      val name = resultSet.getString("name")
      val lastname = resultSet.getString("lastname")
      usuarios :+= Usuario(Some(id), email, name, lastname)
    }

    resultSet.close()
    statement.close()
    
    usuarios
  }

  def eliminarUsuario(id: Long): Int = {
  val query = "DELETE FROM usuarios WHERE id = ?"
  val statement: PreparedStatement = connection.prepareStatement(query)
  
  statement.setLong(1, id)
  
  val rowsAffected = statement.executeUpdate()
  
  statement.close()
  rowsAffected
}

def obtenerUsuarioPorId(id: Long): Option[Usuario] = {
  val query = "SELECT * FROM usuarios WHERE id = ?"
  val statement: PreparedStatement = connection.prepareStatement(query)
  statement.setLong(1, id)

  val resultSet = statement.executeQuery()

  if (resultSet.next()) {
    val id = resultSet.getLong("id")
    val email = resultSet.getString("email")
    val name = resultSet.getString("name")
    val lastname = resultSet.getString("lastname")
    Some(Usuario(Some(id), email, name, lastname))
  } else {
    None
  }
}

def actualizarUsuario(id: Long, email: String, name: String, lastname: String): Int = {
  val query = "UPDATE usuarios SET email = ?, name = ?, lastname = ? WHERE id = ?"
  val statement: PreparedStatement = connection.prepareStatement(query)
  
  statement.setString(1, email)
  statement.setString(2, name)
  statement.setString(3, lastname)
  statement.setLong(4, id)
  
  val rowsAffected = statement.executeUpdate()
  
  statement.close()
  rowsAffected
}


}
