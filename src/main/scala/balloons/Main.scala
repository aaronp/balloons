package balloons

import balloons.fp.Task
import balloons.game._

import scala.io.StdIn

/**
 * Out main entry point to our console [[BalloonGame]]
 */
object Main extends App {
  type Prompt = String

  def userInput(prompt: Prompt): Task[String] = Task.eval(StdIn.readLine(prompt))

  println(BalloonGame.forInput(RandomGameSettings.fromEnv(), userInput))
}