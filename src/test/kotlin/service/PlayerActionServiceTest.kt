package service

import kotlin.test.*
import entity.*

class PlayerActionServiceTest {

    private lateinit var rootService: RootService

    @BeforeTest
    fun setUp() {
        rootService = RootService()
        rootService.gameService.startGame("Max", "Moritz")
    }


    @Test
    fun testSwapCards(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer = currentGame.currentPlayer
        val testHandCard = currentGame.players[curPlayer].handCards[3]
        val testTradeCard = currentGame.tradeDeck[1]
        rootService.playerActionService.swapCards(3,1)

        // the size of hand deck and trade deck did not change after swap
        assertEquals(7, currentGame.players[curPlayer].handCards.size)
        assertEquals(3, currentGame.tradeDeck.size)
        assertEquals(Action.SWAP, currentGame.players[curPlayer].lastAction)

        assertFailsWith(IllegalStateException::class, "You can only swap once a turn")
        { rootService.playerActionService.swapCards(2,1)}

        currentGame.players[curPlayer].lastAction = Action.NULL

        // the cards were actually traded
        assertTrue(currentGame.players[curPlayer].handCards.contains(testTradeCard))
        assertTrue(currentGame.tradeDeck.contains(testHandCard))

        // tradeCard index is out of range, so it should fail
        assertFails{rootService.playerActionService.swapCards(3,4)}

        // player has no hand cards left, so swap cards should not work
        currentGame.players[curPlayer].handCards.clear()
        assertFailsWith(IllegalStateException::class, "Your card has to be part of your hand cards")
        { rootService.playerActionService.swapCards(3,4) }
        //assertFails{rootService.playerActionService.swapCards(2,1)}
    }

    @Test
    fun testDrawCard(){
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        val curPlayer = currentGame.currentPlayer
        rootService.playerActionService.drawCard()

        // the player should have one more hand card than in the beginning (7)
        assertEquals(8, currentGame.players[curPlayer].handCards.size)
        // the draw stack should be one smaller than the initial 35
        assertEquals(34, currentGame.drawStack.size)

        assertFailsWith(IllegalStateException::class, "You can only draw a card once a turn")
        { rootService.playerActionService.drawCard()}

        currentGame.players[curPlayer].lastAction = Action.NULL

        // fill the player's hand cards up to 10 cards, so drawCard() should fail
        currentGame.players[curPlayer].handCards.add(currentGame.drawStack.removeFirst())
        currentGame.players[curPlayer].handCards.add(currentGame.drawStack.removeFirst())
        assertEquals(10, currentGame.players[curPlayer].handCards.size)
        assertFailsWith(IllegalStateException::class, "You can't have more than 10 hand cards")
        { rootService.playerActionService.drawCard() }

        // empty the draw stack so drawCard() should fail
        currentGame.drawStack.clear()
        assertFailsWith(IllegalStateException::class, "Draw stack is empty, no card can be drawn")
        { rootService.playerActionService.drawCard() }
    }


}