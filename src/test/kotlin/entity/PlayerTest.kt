package entity
import kotlin.test.*

/**
 * Tests for [Player]
 *
 * @param testPlayer [Player] with the name "Max" who will be used for the tests
 */

class PlayerTest {
    val testPlayer = Player("Max")

    /**
     * checks if the [Player.score] are initialized correctly
     */

    @Test
    fun testPlayer() {
        testPlayer.score += 51
        assertEquals(51, testPlayer.score)
    }
}