package balloons.game

import balloons.fp.Task
import balloons.game.UserInput.{Bank, Inflate}

/**
 * An immutable ADT representing the current state of the balloon game for a particular user.
 *
 * @param currentBalloon the current balloon in play
 * @param previousResult the result of the last user action
 * @param bursts         an aggregate of the total burst balloons in the game
 * @param totalScore     an aggregate of the balloon sizes in the game
 */
case class GameState(currentBalloon: BalloonState, previousResult: UserActionResult, bursts: Int, totalScore: Int) {

  /** @param input the user input
   * @return the new game state based on the user input
   */
  def update(input: UserInput): GameState = input match {
    case Inflate => inflate()
    case Bank => bank()
  }

  def bank() = copy(previousResult = currentBalloon.bank(), totalScore = totalScore + currentBalloon.currentSize)

  def inflate(): GameState = {
    currentBalloon.inflate() match {
      case Left(burst) => copy(previousResult = burst, bursts = bursts + 1)
      case Right(ok) => copy(currentBalloon = ok.balloon, previousResult = ok)
    }
  }

  /**
   * @param userInput a function which will prompt the user
   * @param nextBalloon the next balloon function, if required
   * @return the optional next state, or None if there are no more balloons
   */
  def advance(userInput: UserActionResult => Task[UserInput], nextBalloon: UserActionResult => Option[BalloonThreshold]): Option[GameState] = {
    GameState.advanceGame(this, userInput, nextBalloon)
  }
}

object GameState {

  /**
   * This is the game logic - advances the game based on the user input given
   *
   * @param inputState  the previous state
   * @param userInput   a function which will return the user input based on the previous state result
   * @param nextBalloon a function which will return the next balloon given the previous result if required
   * @return an optional state - None when there are no more balloons according to 'nextBalloon'
   */
  private def advanceGame(inputState: GameState, userInput: UserActionResult => Task[UserInput], nextBalloon: UserActionResult => Option[BalloonThreshold]): Option[GameState] = {
    val stateOpt = if (shouldPromptForNextBalloon(inputState.previousResult)) {
      nextBalloon(inputState.previousResult).map { nextBalloonThreshold =>
        val nextBalloon = inputState.currentBalloon.next(nextBalloonThreshold)
        inputState.copy(currentBalloon = nextBalloon)
      }
    } else {
      Option(inputState)
    }
    stateOpt.map { state =>
      val nextInput = userInput(state.previousResult).run()
      state.update(nextInput)
    }
  }

  private def shouldPromptForNextBalloon(lastState: UserActionResult): Boolean = {
    lastState match {
      case _: Ok => false
      case InitialState => true
      case _: Burst => true
      case _: Banked => true
    }
  }
}
