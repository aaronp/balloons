package balloons

import balloons.Main.Prompt
import balloons.fp.Task
import balloons.game.{BalloonGame, RandomGameSettings}

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

/**
 * A tool to generate repeatable, automated tests based on known-good games based on real user inputs.
 *
 * This is the same as our 'Main' entry point, except at the end it spits out a test scenario based on your session which can just
 * be copy/pasted into the 'MainTest'
 *
 */
object TestGenerator extends App {

  class Recorder {
    lazy val prompts = ListBuffer[String]()
    lazy val inputs = ListBuffer[String]()

    def wrap(underlying: Main.Prompt => Task[String]) = {
      (prompt: Main.Prompt) => {
        prompts += prompt
        underlying(prompt).map {
          result =>
            inputs += result
            result
        }
      }
    }

    def makeTest(settings: RandomGameSettings, result: String) = {
      def fmt(lines: Iterable[String]) = {
        val allLines = lines.flatMap(_.linesIterator).toList
        if (allLines.isEmpty) {
          ""
        } else {
          allLines.tail.map(line => s"        >$line").mkString(allLines.head + "\n", "\n", "")
        }
      }

      val scenario = inputs.head

      println(
        s"""
           |    //
           |    // copy/paste this test scenario to MainTest.scala:
           |    //
           |
           |    "generate the expected output for game input '${scenario}'" in {
           |      val settings = $settings
           |      val userInputs = \"\"\"${fmt(inputs)}\"\"\".stripMargin('>')
           |      val expectedPrompts = \"\"\"${fmt(prompts)}\"\"\".stripMargin('>')
           |      val expectedOutput = \"\"\"${fmt(result :: Nil)}\"\"\".stripMargin('>')
           |      verify(settings, userInputs, expectedPrompts, expectedOutput)
           |    }
           |""".stripMargin)
    }
  }

  def userInput(prompt: Prompt) = Task.eval(StdIn.readLine(prompt))

//  val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
  val settings = RandomGameSettings.fromEnv()
  val recorder = new Recorder
  val result = BalloonGame.forInput(settings, recorder.wrap(Main.userInput))
  recorder.makeTest(settings, result)
}
