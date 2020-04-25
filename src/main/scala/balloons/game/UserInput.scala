package balloons.game

import balloons.Main.Prompt
import balloons.fp.{Seed, Task}

/**
 * a fixed representation of the possible user inputs
 */
sealed trait UserInput

object UserInput {

  case object Inflate extends UserInput

  case object Bank extends UserInput

  case object PowerMode extends UserInput

  def interpretter(verbose: Boolean, promptUser: Prompt => Task[String]): UserActionResult => Task[UserInput] = {
    if (verbose) {
      UserInput.verboseInput(_, promptUser)
    } else {
      UserInput.quietInput(_, promptUser)
    }
  }

  /**
   * @param firstLine the user input for the balloon game config
   *                  one of:
   *                  $ space-separated list of values (e.g. 5 10 2)
   *                  $ an 'r' (for random) which uses the current time as a seed
   *                  $ an 'r<seed>' (for a fixed random game) which will create a consistent random game for the same <seed> (e.g. r123)
   * @param settings  the settings to use for random games (min/max sizes and balloons)
   * @return a list of balloon thresholds
   */
  def balloonsFromInput(firstLine: String, settings: RandomGameSettings): List[BalloonThreshold] = {
    val SeedR = "r(\\d*)".r
    import settings._
    firstLine match {
      case SeedR("") => randomBalloons(Seed(), minBalloons, maxBalloons, minSize, maxSize)
      case SeedR(seed) => randomBalloons(Seed(seed.toLong), minBalloons, maxBalloons, minSize, maxSize)
      case line => parseBalloons(line)
    }
  }

  def quietInput(previousResult: UserActionResult, readNext: Prompt => Task[String]): Task[UserInput] = {
    val prompt = previousResult match {
      case Burst(_, _) => "BURST\n"
      case InvalidUserInput(_, err) => s"$err\n"
      case _ => ""
    }
    Task.eval(promptUser(prompt, readNext))
  }

  def verboseInput(previousResult: UserActionResult, readNext: Prompt => Task[String]): Task[UserInput] = {
    val prompt = previousResult match {
      case Ok(BalloonState(i, s, _, true)) => s"#$i : size is $s*"
      case Ok(BalloonState(i, s, _, false)) => s"#$i : size is $s"
      case InvalidUserInput(i, err) => s"#$i : $err\n"
      case PowerModeEntered(_) => s"Power Mode!\n"
      case Banked(i, s) => s"#$i BANKED $s!\n#${previousResult.balloonIndex + 1} size is 0"
      case Burst(i, s) => s"#$i : $s POPPED!\n#${previousResult.balloonIndex + 1} size is 0"
      case InitialState => "#1: size is 0"
    }
    Task.eval(promptUser(prompt, readNext))
  }

  /**
   * An unsafe side-effecting function which prompts the user until they enter a valid game command
   *
   * @return the user input for the given user
   */
  def promptUser(prompt: String, readLine: Prompt => Task[String]): UserInput = {
    readLine(prompt).run() match {
      case "INFLATE" | "" => UserInput.Inflate
      case "BANK" | "b" => UserInput.Bank
      case "POWER_MODE" | "p" => UserInput.PowerMode
      case other =>
        promptUser(s"Invalid input '$other', expected 'INFLATE', 'BANK' or 'POWER_MODE'", readLine)
    }
  }
}
