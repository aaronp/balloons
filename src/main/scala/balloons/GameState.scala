package balloons

import balloons.GameState.{Done, Next}

/**
 *
 * @param currentSize             the counter for how many times the player has inflated the balloon
 * @param currentBalloonThreshold the max threshold for the current balloon being inflated before it pops
 * @param remainingBalloons       the other balloons in the game
 * @param score                   the current banked score, accumulating all previous balloons' scores
 */
private[balloons] final case class GameState private(currentSize: Int, currentBalloonThreshold: BalloonThreshold, remainingBalloons: List[BalloonThreshold], score: Int) {
  require(currentSize >= 0)
  require(currentBalloonThreshold >= 0)

  def apply(input: UserInput): GameState.Result = input match {
    case UserInput.Inflate => inflate()
    case UserInput.Bank => bank()
  }

  def bank(): GameState.Result = nextBalloon(currentSize)

  // advance to the next balloon or end the game
  private def nextBalloon(scoreToAdd: Int) = {
    remainingBalloons match {
      case Nil => Done(GameState(0, 0, Nil, score + scoreToAdd))
      case head :: tail => Next(GameState(0, head, tail, score + scoreToAdd))
    }
  }

  def inflate(): GameState.Result = {
    currentSize + 1 match {
      case `currentBalloonThreshold` =>
        // bang! don't add any score
        nextBalloon(0)
      case newSize =>
        require(newSize < currentBalloonThreshold, s"bug: we overinflated the balloon. current state=$this")
        Next(copy(currentSize = newSize))
    }
  }
}

private[balloons] object GameState {
  def apply(balloonThresholds: List[BalloonThreshold]): Option[GameState] = {
    balloonThresholds match {
      case Nil =>
        // weird, but ok ... we're apparently not going to play
        None
      case head :: tail => GameState(0, 0, Nil, 0)
        Option(GameState(0, head, tail, 0))
    }
  }

  def run(balloonThresholds: List[BalloonThreshold], userInputs: Seq[UserInput]): Option[Score] = {
    GameState(balloonThresholds).flatMap(run(_, userInputs))
  }

  def run(game: GameState, remainingInputs: Seq[UserInput]): Option[Score] = {
    val results = remainingInputs.scanLeft[GameState.Result](GameState.Next(game)) {
      case (Next(state), nextInput) => state.apply(nextInput)
      case (done, _) => done
    }
    results.collectFirst {
      case GameState.Done(finalState) => finalState.score
    }
  }

  sealed trait Result

  case class Next(state: GameState) extends Result

  case class Done(state: GameState) extends Result

}