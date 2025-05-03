package entity
import kotlin.test.*

/**
 * Tests for [Player]
 */
class PlayerTest {
    /**
     * example player "Max" is used for the tests
     */
        val testPlayer = Player("Max")

    @Test
    fun testPlayer() {
        testPlayer.score += 51

        /**
         * checks if the [Player.score] are initialized correctly
         */
       // Test, ob die Punkteanzahl richtig initialisiert wurde
        assertEquals(51, testPlayer.score)

    }

}