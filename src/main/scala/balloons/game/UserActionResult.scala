package balloons.game

/**
 * A fixed set of possible input results in the game
 */
sealed trait UserActionResult {
  def balloonIndex: Int
}

case object InitialState extends UserActionResult {
  override val balloonIndex = 0
}

/** A successful result of inflating the balloon
 * @param balloon
 */
case class Ok(balloon: BalloonState) extends UserActionResult {
  override def balloonIndex: BalloonThreshold = balloon.balloonIndex
}

case class Burst(balloonIndex: Int, currentBalloonSize: Int) extends UserActionResult

case class Banked(balloonIndex: Int, currentBalloonSize: Int) extends UserActionResult

case class PowerModeEntered(balloonIndex: Int)  extends UserActionResult

case class InvalidUserInput(balloonIndex: Int, message : String)  extends UserActionResult
