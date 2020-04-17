package balloons

import balloons.fp.Seed

package object game {

  type BalloonThreshold = Int
  type Score = Int


  def parseBalloons(userInput: String): List[BalloonThreshold] = {
    userInput.trim.split(" ", -1).map(_.trim).filterNot(_.isEmpty).map(_.toInt.ensuring(_ >= 0)).toList
  }

  def randomBalloons(seed: Seed, minBalloons: Int, maxBalloons: Int, minSize: Int, maxSize: Int): List[BalloonThreshold] = {
    val sizeRange = maxSize - minSize

    def fill(rand: Seed, remaining: Int, balloons: List[BalloonThreshold]): List[BalloonThreshold] = {
      if (remaining == 0) balloons else {
        val (rand2, size) = Seed.nextInt(sizeRange).map(minSize + _).run(rand)
        fill(rand2, remaining - 1, size :: balloons)
      }
    }

    val countRange = maxBalloons - minBalloons
    val (s, numBalloons) = Seed.nextInt(countRange).run(seed)
    fill(s, minBalloons + numBalloons, Nil)
  }
}
