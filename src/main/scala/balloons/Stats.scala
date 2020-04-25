package balloons

import balloons.game.{Burst, GameState}

/**
 * Formatter for "random" game results
 */
object Stats {
  def apply(results: Seq[GameState]): String = {
    val reportByIndex = results.groupBy(_.currentBalloon.balloonIndex).view.mapValues { balloonStates =>
      val isBurst = balloonStates.exists(_.previousResult.isInstanceOf[Burst])
      val isPowerMode = balloonStates.exists(_.currentBalloon.powerModeEnabled)
      val maxSize = balloonStates.map(_.currentBalloon.currentSize).max
      val maxScore = balloonStates.map(_.currentBalloon.score).max
      val size = balloonStates.head.currentBalloon.threshold
      val powerMode = if (isPowerMode) " --> power mode!" else ""
      val title = if (isBurst) s"Popped at size $size" else s"Scored $maxScore: $maxSize / $size"
      s"$title$powerMode"
    }
    reportByIndex.toList.sortBy(_._1).map {
      case (i, r) => s"Balloon $i : $r"
    }.mkString("\n")
  }
}
