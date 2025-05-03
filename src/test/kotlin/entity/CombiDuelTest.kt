package entity

import kotlin.test.*

/**
 * Tests for [CombiDuel]
 */

class CombiDuelTest {

    val testPlayer1 = Player("Max")
    val testPlayer2 = Player("Moritz")
    val testPlayers : MutableList<Player> = mutableListOf(testPlayer1, testPlayer2)

    val testGame = CombiDuel(testPlayers)

    @Test
    fun testCombiDuel() {

        /**
         * checks if player names are displayed properly and if [CombiDuel.passCheck] and [CombiDuel.done] are initialized correctly
         */

        assertEquals("Max", testGame.players[0].name)
        assertEquals("Moritz", testGame.players[1].name)

        assertFalse(testGame.passCheck)
        assertFalse(testGame.done)
    }

}