package entity

/**
 * Constructor for Class "CombiDuel"
 * @property players a list with the two players
 * @property passCheck to check if both the players passed after each other
 * @property done to check if the player is done
 * @property currentPlayer determines the current player
 * @property drawStack draw stack
 * @property tradeDeck trade deck with always 3 cards
 */

data class CombiDuel(val players : List<Player>)
{
    var passCheck : Boolean = false
    var done : Boolean = false
    var currentPlayer : Int = 0
    // Nachziehstapel besteht bei Beginn des Spieles aus 35 Karten und deswegen Initialisierung mit 35 Null-Elementen
    val drawStack : ArrayDeque<Card> = ArrayDeque<Card>(35)
    val tradeDeck : MutableList<Card> = mutableListOf()
}