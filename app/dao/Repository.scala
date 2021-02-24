package dao

import com.asem.Tables
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{JdbcProfile, PostgresProfile}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                                       (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val entity = PgTables.Users

  private val inserts_sql = Source.fromResource("sql/tables.sql").mkString

  def all(): Future[Seq[PgTables.UsersRow]] = {
    println(s"Hello from all()")

    db.run(sqlu"#$inserts_sql").flatMap { _ =>
      db.run(entity.result)
    }
  }

  def insert(user: PgTables.UsersRow): Future[Int] = {
    println(s"Inserting...")
    db.run(entity += user)
  }
}

class PgTables(val profile: JdbcProfile) extends Tables {}
object PgTables extends PgTables(PostgresProfile)
