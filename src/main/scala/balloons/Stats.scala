package balloons

import balloons.game.{Burst, GameState}

/**
 * Formatter for "random" game results
 */
object Stats {
  def apply(results: Seq[GameState]): String = {
    val reportByIndex = results.groupBy(_.currentBalloon.balloonIndex).view.mapValues { balloonStates =>
      val isBurst = balloonStates.exists(_.previousResult.isInstanceOf[Burst])
      val maxSize = balloonStates.map(_.currentBalloon.currentSize).max
      val size = balloonStates.head.currentBalloon.threshold
      if (isBurst) s"Popped at size $size" else s"Scored $maxSize / $size"
    }
    reportByIndex.toList.sortBy(_._1).map {
      case (i, r) => s"Balloon $i : $r"
    }.mkString("\n")
  }
}
