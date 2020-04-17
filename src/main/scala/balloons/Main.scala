package balloons

import scala.io.StdIn

object Main extends App {

  def userInputs() = LazyList.continually(UserInput.stdIn())

  val sizes = StdIn.readLine().split(",", -1).map(_.toInt)

  GameState.run(sizes.toList, userInputs()) match {
    case None => println("Ran out of input")
    case Some(score) => println(s"SCORE: $score")
  }
}
