package entity

import kotlin.test.*

/**
 * tests for [Card]
 */

class CardTest {

    // Beispielkarten, um die Ausgabe der Karten zu testen
    private val tenOfHeart = Card(CardSuit.HEARTS, CardValue.TEN)
    private val jackOfDiamonds = Card(CardSuit.DIAMONDS, CardValue.JACK)
    private val kingOfSpades = Card(CardSuit.SPADES, CardValue.KING)

    // Unicode-Zeichen für die Ausgabe der Suits
    private val heartsChar = '\u2665' // ♥
    private val diamondsChar = '\u2666' // ♦
    private val spadesChar = '\u2660' // ♠

    /**
     * checks if all example cards are displayed properly as strings
     */
    @Test
    fun testToString() {
        assertEquals(heartsChar + "10", tenOfHeart.toString())
        assertEquals(diamondsChar + "J", jackOfDiamonds.toString())
        assertEquals(spadesChar + "K", kingOfSpades.toString())
        assertEquals(1, jackOfDiamonds.value.compareTo(tenOfHeart.value))
        assertEquals(2, kingOfSpades.value.compareTo(jackOfDiamonds.value))
    }
}