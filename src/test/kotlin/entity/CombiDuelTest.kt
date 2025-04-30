package entity

import kotlin.test.*

/**
 * Testklasse für die Klasse CombiDuel der Entity-Schicht
 */

class CombiDuelTest {

    val testPlayer1 = Player("Max")
    val testPlayer2 = Player("Moritz")

    val testGame = CombiDuel(testPlayer1, testPlayer2)

    @Test
    fun testCombiDuel() {

        /**
         * Überprüfen, ob Playernamen vernünftig ausgegeben werden und ob die Booleans passCheck und done richtig initialisiert wurden
         */

        assertEquals("Max", testGame.player1.name)
        assertEquals("Moritz", testGame.player2.name)

        assertFalse(testGame.passCheck)
        assertFalse(testGame.done)
    }

}