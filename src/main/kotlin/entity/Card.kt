package entity

/**
 * Konstruktor für die Klasse "Card" des CombiDuels mit:
 * @property suit Farbe der Karte
 * @property value Wert der Karte
 */

data class Card(val suit : CardSuit, val value : CardValue) {

    override fun toString() = "$suit$value"
}