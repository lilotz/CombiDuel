package service

import kotlin.test.*
import entity.*

/**
 * Test for the [PlayerActionService] from the Service-Layer
 * @param rootService direct access to the [PlayerActionService] functions
 * @param cardsForTriple a triple of tens to test playCombi
 * @param cardsForQuadruple a quadruple of tens to test playCombi
 * @param cardsForSequence1 a sequence to test playCombi
 * @param cardsForSequence2 a sequence to test playCombi
 * */

class PlayerActionServiceTest {

    private lateinit var rootService: RootService

    private val cardsForTriple = mutableListOf(
        Card(CardSuit.HEARTS, CardValue.TEN),
        Card(CardSuit.SPADES, CardValue.TEN),
        Card(CardSuit.DIAMONDS, CardValue.TEN))

    private val cardsForQuadruple = mutableListOf(
        Card(CardSuit.HEARTS, CardValue.JACK),
        Card(CardSuit.CLUBS, CardValue.TWO),
        Card(CardSuit.SPADES, CardValue.JACK),
        Card(CardSuit.CLUBS, CardValue.TEN),
        Card(CardSuit.DIAMONDS, CardValue.JACK),
        Card(CardSuit.CLUBS, CardValue.JACK))

    private val cardsForSequence1 = mutableListOf(
        Card(CardSuit.CLUBS, CardValue.THREE),
        Card(CardSuit.CLUBS, CardValue.FIVE),
        Card(CardSuit.CLUBS, CardValue.FOUR))

    private val cardsForSequence2 = mutableListOf(
        Card(CardSuit.CLUBS, CardValue.THREE),
        Card(CardSuit.CLUBS, CardValue.FOUR),
        Card(CardSuit.CLUBS, CardValue.FIVE),
        Card(CardSuit.CLUBS, CardValue.TWO),
        Card(CardSuit.CLUBS, CardValue.TEN))

    /**
     * set up a game with two test players: Max and Moritz
     */

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        rootService.gameService.startGame("Max", "Moritz")
    }

    /**
     * tests swapCards by choosing a random card from the player's hand cards and trading it
     * with a random trade card from the middle
     *
     * @throws IllegalStateException if no game is active
     */

    @Test
    fun testStartGame(){
        assertFailsWith(IllegalStateException::class, "Player1's name is empty")
        {rootService.gameService.startGame("Max", "")}
        assertFailsWith(IllegalStateException::class, "Player2's name is empty")
        {rootService.gameService.startGame("", "Moritz")}

        assertFailsWith(IllegalStateException::class, "Game is already running")
        {rootService.gameService.startGame("Moritz", "Moritz")}
    }

    @Test
    fun testSwapCards(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer =  currentGame.players[currentGame.currentPlayer]
        val testHandCard = curPlayer.handCards[3]
        val testTradeCard = currentGame.tradeDeck[1]
        rootService.playerActionService.swapCards(3,1)

        // the size of hand deck and trade deck did not change after swap
        assertEquals(7, curPlayer.handCards.size)
        assertEquals(3, currentGame.tradeDeck.size)
        assertEquals(Action.SWAP, curPlayer.lastAction)

        //currentGame.currentPlayer = 0
        assertFailsWith(IllegalStateException::class, "You can only swap once a turn")
        { rootService.playerActionService.swapCards(2,1)}

        curPlayer.lastAction = Action.NULL

        // the cards were actually traded
        assertTrue(curPlayer.handCards.contains(testTradeCard))
        assertTrue(currentGame.tradeDeck.contains(testHandCard))

        // tradeCard index is out of range, so it should fail
        assertFails{rootService.playerActionService.swapCards(3,4)}

        // player has no hand cards left, so swap cards should not work
        curPlayer.handCards.clear()
        assertFailsWith(IllegalStateException::class, "Your card has to be part of your hand cards")
        { rootService.playerActionService.swapCards(3,4) }
        //assertFails{rootService.playerActionService.swapCards(2,1)}
    }

    /**
     * tests drawCard by taking the first card from the draw stack and adding it to
     * the player's hand cards
     *
     * @throws IllegalStateException if no game is active
     */

    @Test
    fun testDrawCard(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer =  currentGame.players[currentGame.currentPlayer]
        rootService.playerActionService.swapCards(1,1)
        rootService.playerActionService.drawCard()

        // the player should have one more hand card than in the beginning (7)
        assertEquals(8, curPlayer.handCards.size)
        // the draw stack should be one smaller than the initial 35
        assertEquals(34, currentGame.drawStack.size)
        assertEquals(Action.DRAW, curPlayer.lastAction)
        assertEquals(1, currentGame.currentPlayer)

        currentGame.currentPlayer = 0
        assertFailsWith(IllegalStateException::class, "You can only draw a card once a turn")
        { rootService.playerActionService.drawCard()}

        curPlayer.lastAction = Action.NULL

        // fill the player's hand cards up to 10 cards, so drawCard() should fail
        curPlayer.handCards.add(currentGame.drawStack.removeFirst())
        curPlayer.handCards.add(currentGame.drawStack.removeFirst())
        assertEquals(10, curPlayer.handCards.size)
        assertFailsWith(IllegalStateException::class, "You can't have more than 10 hand cards")
        { rootService.playerActionService.drawCard() }

        // empty the draw stack so drawCard() should fail
        currentGame.drawStack.clear()
        assertFailsWith(IllegalStateException::class, "Draw stack is empty, no card can be drawn")
        { rootService.playerActionService.drawCard() }
    }

    /**
     * tests playCombi by playing a triple, a quadruple and a sequence
     *
     * @throws IllegalStateException if no game is active
     */

    @Test
    fun testPlayCombi(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer =  currentGame.players[currentGame.currentPlayer]

        // test for triple
        curPlayer.handCards.clear()
        curPlayer.handCards.addAll(cardsForTriple)
        rootService.playerActionService.playCombi(mutableListOf(0,1,2))

        assertEquals(10, curPlayer.score)
        assertEquals(0, curPlayer.handCards.size)
        assertEquals(Action.COMBI, curPlayer.lastAction)

        //test for quadruple
        curPlayer.handCards.addAll(cardsForQuadruple)
        curPlayer.lastAction = Action.NULL
        curPlayer.secondActionCombi = false
        rootService.playerActionService.drawCard()
        rootService.playerActionService.playCombi(mutableListOf(0,2,4,5))

        assertEquals(10+15, curPlayer.score)
        assertEquals(3, curPlayer.handCards.size)
        assertEquals(Action.COMBI, curPlayer.lastAction)
        assertTrue { curPlayer.handCards.contains(Card(CardSuit.CLUBS, CardValue.TEN)) }
        assertTrue { curPlayer.handCards.contains(Card(CardSuit.CLUBS, CardValue.TWO)) }
        assertTrue(curPlayer.secondActionCombi)

        assertFailsWith(IllegalStateException::class, "You can only play another combi or pass")
        {rootService.playerActionService.drawCard()}

        // test for sequence
        curPlayer.handCards.addAll(cardsForSequence1)
        rootService.playerActionService.playCombi(mutableListOf(0,3,4,5))

        assertEquals(25+8, curPlayer.score)
        assertEquals(2, curPlayer.handCards.size)

       curPlayer.handCards.addAll(cardsForSequence2)

        assertFailsWith(IllegalArgumentException::class, "The cards you've chosen were not a valid combi" )
        { rootService.playerActionService.playCombi(mutableListOf(0,1,3,4))}

        rootService.playerActionService.pass()
    }

    /**
     * tests pass by calling it in different situations
     */

    @Test
    fun testPass(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer =  currentGame.players[currentGame.currentPlayer]

        curPlayer.lastAction = Action.DRAW
        rootService.playerActionService.pass()
        assertFalse(currentGame.passCheck)
        assertEquals(1, currentGame.currentPlayer)

        curPlayer.lastAction = Action.NULL
        rootService.playerActionService.pass()
        assertTrue(currentGame.passCheck)
    }
}