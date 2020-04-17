package balloons

import scala.io.StdIn

sealed trait UserInput

object UserInput {

  case object Inflate extends UserInput

  case object Bank extends UserInput

  def stdIn(): UserInput = {
    StdIn.readLine("Next:") match {
      case "INFLATE" => UserInput.Inflate
      case "BANK" => UserInput.Bank
      case other =>
        println(s"Invalid input '$other', expected 'INFLATE' or 'BANK'")
        stdIn()
    }
  }
}
