package entity

/**
 * Constructor for the class "Card"
 * @property suit Suit of Card
 * @property value Value of Card
 */

data class Card(val suit : CardSuit, val value : CardValue) {
    /**
     * override of toString in the format "SuitValue"
     */
    override fun toString() = "$suit$value"
}