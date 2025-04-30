package entity

/**
 * Konstruktor für die Klasse "Player" mit:
 * @property name Name des Spielers
 * @property disposalArea Ablagestapel für gelegte Kombis
 * @property handCards Handkarten des Spielers
 * @property score Punktzahl des Spielers
 * @property actions Liste von Aktionen, die vom Spieler in diesem Spielzug getätigt wurden
 */

data class Player(val name : String)
{
    val disposalArea : MutableList<Card> = emptyList<Card>().toMutableList()
    val handCards : MutableList<Card> = emptyList<Card>().toMutableList()
    var score : Int = 0
    var actions : MutableList<Action> = emptyList<Action>().toMutableList()
}