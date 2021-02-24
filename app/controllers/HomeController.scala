package controllers

import dao.{PgTables, Repository}
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(repo: Repository,
                               val controllerComponents: ControllerComponents
                              )
                              (implicit executionContext: ExecutionContext) extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action.async { implicit request: Request[AnyContent] =>
    println(s"Hello from $request")

    try {
      repo.all()
        .andThen(_ => repo.insert(PgTables.UsersRow ("test", "test@google.com", attr50 = Some("test value"))))
        .map { res =>
          println(s"Result: $res")
          Ok(views.html.index())
        }.recover { ex =>
          ex.printStackTrace()
          Ok(views.html.index())
        }
    } catch {
      case NonFatal(ex) =>
        ex.printStackTrace()
        Future.successful(Ok(views.html.index()))
    }
  }
}
