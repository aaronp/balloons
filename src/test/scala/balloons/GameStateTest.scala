package balloons

import balloons.UserInput._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateTest extends AnyWordSpec with Matchers {

  import GameStateTest._

  "GameState.run" should {
    List(
      Scenario.of(10, Some(4), Inflate, Inflate, Inflate, Inflate)
    ) foreach {
      case Scenario(balloons, expectedScore, inputs) =>
        s"produce $expectedScore for game ${balloons.mkString(",")} and user inputs ${inputs.mkString(",")}" in {
          GameState.run(balloons, inputs) shouldBe expectedScore
        }
    }
  }
}

object GameStateTest {

  case class Scenario(balloons: List[BalloonThreshold], expected: Option[Int], inputs: List[UserInput])

  object Scenario {
    def of(size: BalloonThreshold, expected: Option[Int], inputs: UserInput*): Scenario = {
      new Scenario(List(size), expected, inputs.toList)
    }
  }

}