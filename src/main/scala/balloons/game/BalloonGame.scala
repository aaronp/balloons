package balloons.game

import balloons.Main.Prompt
import balloons.Stats
import balloons.fp.Task

import scala.util.{Failure, Success, Try}

/** Contains the main game logic.
 */
object BalloonGame {

  /**
   * Runs the game based in these settings (if it is a randomly generated game) and the user input function
   *
   * @param settings  the settings for random games
   * @param userInput the user input function
   * @return the last line of the game output - the game score message
   */
  def forInput(settings: RandomGameSettings, userInput: Prompt => Task[String]): String = {
    // the first line of the game is the game config
    val balloonConfig = userInput("").run()

    val verbose = balloonConfig.startsWith("r")
    apply(settings, balloonConfig, UserInput.interpretter(verbose, userInput)) match {
      case Left(msg) => msg
      case Right((game, msg)) =>
        // if we run a 'random' game then display some advanced stats, otherwise just report the score
        if (balloonConfig.startsWith("r")) {
          msg
        } else {
          s"SCORE: ${game.totalScore}"
        }
    }
  }

  /**
   * The main game logic
   *
   * @param settings           settings used for random games
   * @param balloonConfigInput the initial user config line
   * @param userInput          the user input function
   * @return the game result
   */
  def apply(settings: RandomGameSettings, balloonConfigInput: String, userInput: UserActionResult => Task[UserInput]): Either[String, (GameState, String)] = {
    Try(UserInput.balloonsFromInput(balloonConfigInput, settings)) match {
      case Success(sizes) =>
        val nextBalloon = BalloonGame.fixedBalloons(sizes.toIndexedSeq) _

        val results: Seq[GameState] = BalloonGame.runStates(userInput, nextBalloon)
        if (results.isEmpty) {
          Left("Usage: Enter a space-delimited series of balloon sizes, or 'r' for a random game, or r<num> for set game (e.g. r4)")
        } else {
          val last: GameState = results.last
          val header = s"SCORE: ${last.totalScore}, ${last.bursts} burst out of ${last.currentBalloon.balloonIndex} balloons:"
          val sep = header.map(_ => '_')
          val report = s"\n$sep\n$header\n$sep\n${Stats(results)}"
          Right((last, report))
        }
      case Failure(err) =>
        val msg = s"Invalid input - the first line should be a space-separated list of balloon sizes: $err"
        Left(msg)
    }
  }


  def fixedBalloons(balloons: IndexedSeq[BalloonThreshold])(previousResult: UserActionResult) = {
    balloons.lift(previousResult.balloonIndex)
  }

  def run(userInput: UserActionResult => Task[UserInput], nextBalloon: UserActionResult => Option[BalloonThreshold]): GameState = {
    runStates(userInput, nextBalloon).last
  }

  def runStates(userInput: UserActionResult => Task[UserInput], nextBalloon: UserActionResult => Option[BalloonThreshold]): LazyList[GameState] = {
    LazyList.unfold(GameState(BalloonState(0, 0, 0), InitialState, 0, 0)) { inputState =>
      inputState.advance(userInput, nextBalloon).map { nextState =>
        (nextState, nextState)
      }
    }
  }
}
