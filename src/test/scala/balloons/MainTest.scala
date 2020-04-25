package balloons

import balloons.TestGenerator.Recorder
import balloons.fp.Task
import balloons.game.{BalloonGame, RandomGameSettings}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * This is our integration test. This gives us test coverage at the level of our interface, which is user prompts (e.g. StdIn.readLine()) and displays (e.g. println).
 *
 * This test should survive any backwards-compatible refactorings, where other tests may be deleted/changed according to the unit which they are testing.
 *
 *
 * The 'TestGenerator' sibling to this class can be run as an alternative to [[Main]].
 *
 * The idea is that you run 'TestGenerator' instead of Main, verify you're happy with your manual test (e.g. you verify it produces the prompts/output as expected), and
 * if so, you can just copy/paste a generated test scenario as part of this suite.
 *
 * This way we can have very meaningful tests which give us confidence that, if the tests pass, then actual code it's testing is safe/valid.
 *
 * It also means you (you being a tester, developer, whomever) can easily replicate the same tests as what are created here by just running the main
 * application and giving it the same (or different) responses
 *
 */
class MainTest extends AnyWordSpec with Matchers {

  "Main.run" should {
    "only allow power mode once in a game" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs =
        """2 4 1
          >POWER_MODE
          >INFLATE
          >INFLATE
          >BANK
          >INFLATE
          >BANK
          >POWER_MODE
          >INFLATE
          >BANK""".stripMargin('>')
      val expectedPrompts =
        """POWER_MODE ALREADY USED""".stripMargin('>')
      val expectedOutput =
        """SCORE: 6""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }

    "generate the expected output for game input '2 4 1'" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs =
        """2 4 1
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >BANK
          >INFLATE
          >BANK""".stripMargin('>')
      val expectedPrompts =
        """BURST
""".stripMargin('>')
      val expectedOutput =
        """SCORE: 2
""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }

    "generate the expected output for game input '0'" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs =
        """0
          >INFLATE""".stripMargin('>')
      val expectedPrompts = """""".stripMargin('>')
      val expectedOutput = """SCORE: 0""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }

    "display a usage message if given a blank input line" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs = """""".stripMargin('>')
      val expectedPrompts = """""".stripMargin('>')
      val expectedOutput =
        """Usage: Enter a space-delimited series of balloon sizes, or 'r' for a random game, or r<num> for set game (e.g. r4)
""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }


    "show an error if it can't parse the balloon sizes" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs =
        """1 two 3
""".stripMargin('>')
      val expectedPrompts = """""".stripMargin('>')
      val expectedOutput =
        """Invalid input - the first line should be a space-separated list of balloon sizes: java.lang.NumberFormatException: For input string: "two"
""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }

  }
  "Main.run - invalid input for 'random' tests" should {
    "generate the expected output for game input 'r10'" in {
      val settings = RandomGameSettings(minSize = 2, maxSize = 4, minBalloons = 1, maxBalloons = 5)
      val userInputs =
        """r10
          >INFLATE
          >invalid
          >INFLATE
          >BANK""".stripMargin('>')
      val expectedPrompts =
        """#1: size is 0
          >#1 : size is 1
          >Invalid input 'invalid', expected 'INFLATE', 'BANK' or 'POWER_MODE'
          >#1 : size is 2""".stripMargin('>')
      val expectedOutput =
        """
          >____________________________________
          >SCORE: 2, 0 burst out of 1 balloons:
          >____________________________________
          >Balloon 1 : Scored 2: 2 / 2""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }


    "generate the expected output for game input 'r4'" in {
      val settings = RandomGameSettings.fromEnv()
      val userInputs =
        """r4
          >BANK
          >BANK
          >INFLATE
          >BANK
          >INFLATE
          >BANK
          >INFLATE
          >BANK
          >INFLATE
          >INFLATE
          >INFLATE
          >BANK""".stripMargin('>')
      val expectedPrompts =
        """#1: size is 0
          >#1 BANKED 0!
          >#2 size is 0
          >#2 BANKED 0!
          >#3 size is 0
          >#3 : size is 1
          >#3 BANKED 1!
          >#4 size is 0
          >#4 : size is 1
          >#4 BANKED 1!
          >#5 size is 0
          >#5 : size is 1
          >#5 BANKED 1!
          >#6 size is 0
          >#6 : size is 1
          >#6 : size is 2
          >#6 : size is 3""".stripMargin('>')
      val expectedOutput =
        """
          >____________________________________
          >SCORE: 6, 0 burst out of 6 balloons:
          >____________________________________
          >Balloon 1 : Scored 0: 0 / 3
          >Balloon 2 : Scored 0: 0 / 20
          >Balloon 3 : Scored 1: 1 / 17
          >Balloon 4 : Scored 1: 1 / 16
          >Balloon 5 : Scored 1: 1 / 15
          >Balloon 6 : Scored 3: 3 / 12""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }

    "double the score when power mode is enabled for game 'r2'" in {
      val settings = RandomGameSettings(minSize = 1, maxSize = 20, minBalloons = 2, maxBalloons = 10)
      val userInputs =
        """r2
          >INFLATE
          >INFLATE
          >POWER_MODE
          >POWER_MODE
          >INFLATE
          >BANK
          >POWER_MODE
          >INFLATE
          >POWER_MODE
          >INFLATE
          >BANK
          >BANK
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >POWER_MODE
          >INVALID USER INPUT
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >MORE invalid input
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE
          >INFLATE""".stripMargin('>')
      val expectedPrompts =
        """#1: size is 0
          >#1 : size is 1
          >#1 : size is 2
          >Power Mode!
          >#1 : POWER_MODE ALREADY ENABLED
          >#1 : size is 3*
          >#1 BANKED 3!
          >#2 size is 0
          >#2 : POWER_MODE ALREADY USED
          >#2 : size is 1
          >#2 : POWER_MODE ALREADY USED
          >#2 : size is 2
          >#2 BANKED 2!
          >#3 size is 0
          >#3 BANKED 0!
          >#4 size is 0
          >#4 : size is 1
          >#4 : size is 2
          >#4 : size is 3
          >#4 : size is 4
          >#4 : size is 5
          >#4 : size is 6
          >#4 : size is 7
          >#4 : size is 8
          >#4 : size is 9
          >#4 : size is 10
          >#4 : size is 11
          >#4 : size is 12
          >#4 : POWER_MODE ALREADY USED
          >Invalid input 'INVALID USER INPUT', expected 'INFLATE', 'BANK' or 'POWER_MODE'
          >#4 : size is 13
          >#4 : size is 14
          >#4 : size is 15
          >#4 : size is 16
          >Invalid input 'MORE invalid input', expected 'INFLATE', 'BANK' or 'POWER_MODE'
          >#4 : size is 17
          >#4 : size is 18
          >#4 : size is 19
          >#4 : size is 20""".stripMargin('>')
      val expectedOutput =
        """
          >____________________________________
          >SCORE: 8, 1 burst out of 4 balloons:
          >____________________________________
          >Balloon 1 : Scored 6: 3 / 19 --> power mode!
          >Balloon 2 : Scored 2: 2 / 10
          >Balloon 3 : Scored 0: 0 / 5
          >Balloon 4 : Popped at size 20""".stripMargin('>')
      verify(settings, userInputs, expectedPrompts, expectedOutput)
    }
  }

  def verify(settings: RandomGameSettings, userInputs: String, expectedPrompts: String, expectedOutput: String) = {
    val recorder = new Recorder

    val userInputList: Iterator[String] = userInputs.linesIterator

    def nextInput(prompt: String) = {
      val value = if (userInputList.isEmpty) "" else userInputList.next()
      Task.now(value)
    }

    val actualResult = BalloonGame.forInput(settings, recorder.wrap(nextInput))

    // add some tolerance for blank lines
    def trimBlank(str: String) = trimBlankLines(str.linesIterator.toList)

    def trimBlankLines(lines: Iterable[String]) = lines.map(_.trim).filterNot(_.isEmpty).toList

    val actualPrompts = trimBlankLines(recorder.prompts.flatMap(_.linesIterator))

    val expOpts: Iterator[Option[String]] = expectedPrompts.linesIterator.map(Option.apply)
    val actOpts = actualPrompts.map(Option.apply)
    actOpts.zipAll(expOpts.toSeq, None, None).zipWithIndex.foreach {
      case ((Some(e), Some(a)), _) if e == a => s"$e --> OK!"
      case ((Some(actual), Some(expected)), i) => withClue(s"Prompt $i") {
        actual shouldBe expected
      }
      case ((Some(actual), None), i) => fail(s"No more prompts expected at $i, but got: '$actual'")
      case ((None, Some(expected)), i) => fail(s"prompt $i '$expected' is missing")
    }

    trimBlank(actualResult) shouldBe trimBlank(expectedOutput)
  }
}
