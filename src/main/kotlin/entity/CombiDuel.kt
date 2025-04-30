package entity

/**
 * Konstruktor für die Klasse "CombiDuel" mit:
 * @property player1 der 1. Spieler
 * @property player2 der 2. Spieler
 * @property passCheck Boolean, um 2 zweimal passen hintereinander festzuhalten
 * @property done Boolean, um zu überprüfen, ob Spielzug zu Ende
 * @property currentPlayer Int für aktuellen Spieler
 * @property drawStack Deque für den Nachziehstapel
 * @property tradeDeck Liste mit den 3 Karten zum Tauschen
 */

class CombiDuel(val player1 : Player, val player2 : Player)
{
    var passCheck : Boolean = false
    var done : Boolean = false
    var currentPlayer : Int = 0
    // Nachziehstapel besteht bei Beginn des Spieles aus 35 Karten und deswegen Initialisierung mit 35 Null-Elementen
    var drawStack : ArrayDeque<Card> = ArrayDeque<Card>(35)
    var tradeDeck : MutableList<Card> = emptyList<Card>().toMutableList()
}