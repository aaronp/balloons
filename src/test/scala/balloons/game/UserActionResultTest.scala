package balloons.game

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UserActionResultTest extends AnyWordSpec with Matchers {

  "Ok.balloonIndex" should {
    // this is really cheeky - at this point this simple function is hurting the test coverage, so I went all OCD
    // to ensure it's called, even though it's not technically reached in an integration test
    "return the index" in {
      Ok(BalloonState(2,3,4)).balloonIndex shouldBe 2
    }
  }
}
