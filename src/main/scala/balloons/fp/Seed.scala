package balloons.fp

/**
 * A purely-functional pseudo-random function ripped off from https://porpoiseltd.co.uk/countdown/
 *
 * @param long the seed
 */
final case class Seed(long: Long) {
  def next = Seed(long * 6364136223846793005L + 1442695040888963407L)

  private def int(maxSigned: Int): Int = {
    val max = maxSigned.abs
    if (max == 0) {
      0
    } else {
      (long.abs % (max + 1)).toInt
    }
  }
}

object Seed {

  case class State[S, A](run: S => (S, A)) {

    def map[B](f: A => B): State[S, B] = {
      State[S, B] { in =>
        val (s, a) = run(in)
        (s, f(a))
      }
    }
  }

  def apply(init: Long = System.currentTimeMillis): Seed = new Seed(init)

  def nextInt(max: Int): State[Seed, Int] =
    State(seed => (seed.next, seed.int(max)))

}
