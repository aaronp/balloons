package balloons.game

import balloons.fp.Seed
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class packageTest extends AnyWordSpec with Matchers {

  "randomBalloons" should {

    "produce [3,2] for seed 1" in {
      randomBalloons(Seed(1), 1, 4, 2, 5) shouldBe List(3, 2)
    }

    "produce [3,2,5] for seed 2" in {
      randomBalloons(Seed(2), 1, 4, 2, 5) shouldBe List(3, 2, 5)
    }

    "produce [5,2,3,4] for seed 3" in {
      randomBalloons(Seed(3), 1, 4, 2, 5) shouldBe List(5, 2, 3, 4)
    }

    "produce [5] for seed 4" in {
      randomBalloons(Seed(4), 1, 4, 2, 5) shouldBe List(5)
    }

    "produce [5,2] for seed 5" in {
      randomBalloons(Seed(5), 1, 4, 2, 5) shouldBe List(5, 2)
    }

    "produce [3,2,3] for seed 6" in {
      randomBalloons(Seed(6), 1, 4, 2, 5) shouldBe List(3, 2, 3)
    }

    "produce [5,2,5,4] for seed 7" in {
      randomBalloons(Seed(7), 1, 4, 2, 5) shouldBe List(5, 2, 5, 4)
    }

    "produce [3] for seed 8" in {
      randomBalloons(Seed(8), 1, 4, 2, 5) shouldBe List(3)
    }

    "produce [3,2] for seed 9" in {
      randomBalloons(Seed(9), 1, 4, 2, 5) shouldBe List(3, 2)
    }

    "produce [5,2,5] for seed 10" in {
      randomBalloons(Seed(10), 1, 4, 2, 5) shouldBe List(5, 2, 5)
    }
  }
}
