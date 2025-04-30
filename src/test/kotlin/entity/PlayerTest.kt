package entity
import kotlin.test.*

/**
 * Testklasse für die Klasse Player der Entity-Schicht
 */
class PlayerTest {
    /**
     * für den Test wird ein Beispiel-Spieler Max benutzt
     */
        val testPlayer = Player("Max")

    @Test
    fun testPlayer() {
        testPlayer.score += 51
        testPlayer.actions.add(Action.SWAP)

        /**
         * Überprüfen, ob Punkteanzahl richtig initialisiert wurde und ob die Liste actions vernünftig funktioniert
         */
       // Test, ob die Punkteanzahl richtig initialisiert wurde
        assertEquals(51, testPlayer.score)
        // Test, ob actions nur 1 Aktion besitzt
        assertEquals(1, testPlayer.actions.size)

        testPlayer.actions.remove(Action.SWAP)
        // Test, ob Aktionen aus der Liste gelöscht werden können
        assertEquals(0, testPlayer.actions.size)

    }

}