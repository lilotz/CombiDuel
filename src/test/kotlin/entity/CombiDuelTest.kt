package entity

import kotlin.test.*

/**
 * Tests for [CombiDuel]
 *
 * @param testPlayer1 [Player] with the name of the player 1
 * @param testPlayer2 [Player] with name of the player 2
 * @param testPlayers list with the two players
 * @param testGame [CombiDuel] with the two players
 *
 */

class CombiDuelTest {

    val testPlayer1 = Player("Max")
    val testPlayer2 = Player("Moritz")
    val testPlayers : MutableList<Player> = mutableListOf(testPlayer1, testPlayer2)
    val testGame = CombiDuel(testPlayers)

    /**
     * checks if player names are displayed properly and if [CombiDuel.passCheck]
     * is initialized correctly
     */

    @Test
    fun testCombiDuel() {
        assertEquals("Max", testGame.players[0].name)
        assertEquals("Moritz", testGame.players[1].name)
        assertFalse(testGame.passCheck)
    }
}