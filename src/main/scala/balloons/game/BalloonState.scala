package balloons.game

/**
 * Tracks the state of one of the balloons in the game
 * @param balloonIndex the balloon index in the game
 * @param currentSize the current size of the balloon (starting at 0)
 * @param threshold the size after which this balloon will pop
 */
case class BalloonState(balloonIndex: Int, currentSize: Int, threshold: BalloonThreshold) {
  override def toString = s"Balloon #$balloonIndex $currentSize/$threshold"

  /** Convenience method to produce the next balloon after this one in the game
   * @param size the next balloon's size
   * @return the next balloon
   */
  def next(size: Int) = copy(balloonIndex = balloonIndex + 1, 0, size)

  /**
   * @return either a burst or 'ok' depending on whether or not inflating this balloon is ok
   */
  def inflate(): Either[Burst, Ok] = if (currentSize == threshold) {
    Left(Burst(balloonIndex, currentSize))
  } else {
    Right(Ok(copy(currentSize = currentSize + 1)))
  }

  def bank() = Banked(balloonIndex, currentSize)
}
