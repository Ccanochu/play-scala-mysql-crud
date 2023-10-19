package modules

import com.google.inject.AbstractModule
import services.UsuarioService

class AppModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UsuarioService]).asEagerSingleton()
  }
}
