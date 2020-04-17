package balloons.fp

/**
 * Here we've rolled out own IO instead of bringing in ZIO, cats-effect, monix, etc just for this
 *
 * @tparam A the result type
 */
trait Task[A] {
  self =>
  def run(): A

  def map[B](f: A => B): Task[B] = new Task[B] {
    override def run(): B = f(self.run())
  }
}

object Task {

  case class Now[A](override val run: A) extends Task[A]

  class Every[A](eval: => A) extends Task[A] {
    override def run(): A = eval
  }

  def now[A](value: A): Task[A] = Now(value)

  def eval[A](value: => A): Task[A] = new Every(value)
}