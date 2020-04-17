package balloons.game

case class RandomGameSettings(minSize: Int, maxSize: Int, minBalloons: Int, maxBalloons: Int) {
  override def toString = {
    s"RandomGameSettings(minSize = $minSize, maxSize = $maxSize, minBalloons = $minBalloons, maxBalloons = $maxBalloons)"
  }
}

object RandomGameSettings {
  def fromEnv() = {
    new RandomGameSettings(
      minSize = sys.env.getOrElse("minSize", 1.toString).toInt,
      maxSize = sys.env.getOrElse("maxSize", 20.toString).toInt,
      minBalloons = sys.env.getOrElse("minBalloons", 2.toString).toInt,
      maxBalloons = sys.env.getOrElse("maxBalloons", 10.toString).toInt
    )
  }
}
