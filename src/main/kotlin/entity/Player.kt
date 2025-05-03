package entity

/**
 * Constructor for "Player"
 * @property name player's name
 * @property disposalArea disposal area for played combis
 * @property handCards player's hand cards
 * @property score player's score
 * @property lastAction the last played action in the current turn
 */

data class Player(val name : String)
{
    val disposalArea : MutableList<Card> = mutableListOf()
    val handCards : MutableList<Card> = mutableListOf()
    var score : Int = 0
    val lastAction : Action = Action.NULL
}