package oxnrtr.chat

// Import the session management, including the implicit threadLocalSession

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession

// Import the query language

import org.scalaquery.ql._

// Import the standard SQL types

import org.scalaquery.ql.TypeMapper._

// Use H2Driver which implements ExtendedProfile and thus requires ExtendedTables

import org.scalaquery.ql.extended.H2Driver.Implicit._
import org.scalaquery.ql.extended.{ExtendedTable => Table}

object DB {
  val User = new Table[(Int, String, String)]("USERS") {
    def id = column[Int]("ID", O.PrimaryKey)

    def username = column[String]("USERNAME")

    def password = column[String]("PASSWORD")

    def * = id ~ username ~ password
  }

  val Messages = new Table[(Int, Int, String, String)]("MESSAGES") {
    def id = column[Int]("ID", O.PrimaryKey)

    def userId = column[Int]("USER_ID")

    def sender = column[String]("SENDER")

    def message = column[String]("MESSAGE")

    def * = id ~ conversationId ~ sender ~ message
  }

  // Connect to the database and execute the following block within a session
  val db = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")


  db withSession {

    Messages.ddl.create
  }
}