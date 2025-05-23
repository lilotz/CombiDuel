package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import entity.*
import tools.aqua.bgw.components.container.*
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.*
import tools.aqua.bgw.visual.*
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.style.*

// TODO: scale auf 1. für mouseCLicked, um Gegnerhände rauszunehmen; Actions Left-Button


/**
 * CombiDuelScene is the main scene where the game is being played.
 * It shows the current player's hand cards, the trade area, the draw stack and the discard area for played combis.
 * The opponent's hand cards are hidden at the top of the scene.
 * Next to the player's name is also their current score
 *
 * @param rootService the [RootService] that manages the state of the game
 */

class CombiDuelScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080, background = ImageVisual("backround.png")),
    Refreshable {

    private val cards: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private val cardImageLoader: CardImageLoader = CardImageLoader()

    private val indexSelectedTradeCard: MutableList<Int> = mutableListOf()

    private val indexSelectedHandCards: MutableList<Int> = mutableListOf()

    // LinearLayout that contains of the player's hand cards
    private val playerHand = LinearLayout<CardView>(
        posX = 0,
        posY = 755,
        width = 1920,
        height = 250,
        alignment = Alignment.CENTER,
        spacing = -60,
    )

    // label with the player's name and their score
    private val playerName = Label(
        posX = 1920 - 250,
        posY = 1080 - 100,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        }),
        font = Font(22, Color(0xFFFFFFF), "IBMPlex Serif Medium")
    )

    // LinearLayout that contains of the opponent's hand cards which are hidden
    private val opponentHand = LinearLayout<CardView>(
        posX = 0,
        posY = 45,
        width = 1920,
        height = 250,
        alignment = Alignment.TOP_CENTER,
        spacing = -60,

        ).apply {
        rotation = 180.0
    }

    // label with the opponent's name and their score
    private val opponentName = Label(
        posX = 50,
        posY = 20,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        }),
        font = Font(22, Color(0xFFFFFFF), "IBMPlex Serif Medium")
    ).apply {
        rotation = 0.0
    }

    // stack whit cards that can be drawn
    private val drawStack = CardView(
        posX = 1500,
        posY = 415,
        width = 162,
        height = 250,
        front = CompoundVisual(ImageVisual("card_back.png").apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        try {
            onMouseClicked = {
                rootService.playerActionService.drawCard()
            }
        } catch (exception: Exception) {
            errorWasThrown(exception.message)
        }
    }

    // LinearLayout with three cards that can be traded
    private val tradeArea = LinearLayout<CardView>(
        posX = 700,
        posY = 415,
        width = 520,
        height = 250,
        alignment = Alignment.CENTER,
        spacing = 30
    )

    // This CardStack is used to display the top card of the play stack
    private val discardStack = CardStack<CardView>(
        posX = 100,
        posY = 835,
        width = 162,
        height = 200,
        alignment = Alignment.CENTER
    )

    // button with "Play Combi"
    private val playCombiButton = Button(
        posX = 80,
        posY = 835,
        width = 202,
        height = 100,
        text = "Play Combi",
        font = Font(30, Color(255, 255, 255), "IBMPlex Serif Medium"),
        visual = CompoundVisual(ColorVisual(Color(61, 68, 47, 200)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        try {
            onMouseClicked = {
                if (indexSelectedHandCards.size > 2 && indexSelectedHandCards.size < 11) {
                    rootService.playerActionService.playCombi(indexSelectedHandCards)
                }
            }
        } catch (exception: Exception) {
            errorWasThrown(exception.message)
        }
    }

    // button with which the current player can pass
    private val passButton = Button(
        posX = 1690,
        posY = 870,
        width = 160,
        height = 70,
        text = "Pass",
        font = Font(30, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(color = Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        isVisible = false
        onMouseClicked = {
            rootService.playerActionService.pass()
        }
    }

    // button with which the current player can trade two cards
    private val tradeButton = Button(
        posX = 1690,
        posY = 790,
        width = 160,
        height = 70,
        text = "Trade",
        font = Font(30, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(color = Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        isVisible = false
        try {
            onMouseClicked = {
                if (indexSelectedHandCards.size == 1 && indexSelectedTradeCard.size == 1) {
                    rootService.playerActionService.swapCards(
                        indexSelectedHandCards[0],
                        indexSelectedTradeCard[0]
                    )
                }
            }
        } catch (exception: Exception) {
            errorWasThrown(exception.message)
        }
    }

    // plane that is being called when the player changes
    private val changePlane1 = Label(
        posX = 460,
        posY = 490,
        width = 1000,
        height = 100,
        text = "",
        alignment = Alignment.CENTER,
        font = Font(70, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        visual = CompoundVisual(
            ColorVisual(color = Color(82, 95, 61))
                .apply { style.borderRadius = BorderRadius(10) })
    ).apply {
        isVisible = false
    }

    // plane that is being called when the player changes
    // it displays the name of the next current player
    private val changePlane2 = Label(
        posX = 200,
        posY = 200,
        width = 1520,
        height = 680,
        alignment = Alignment.CENTER,
        visual = CompoundVisual(
            ColorVisual(color = Color(103, 119, 77, 180))
                .apply { style.borderRadius = BorderRadius(10) })
    ).apply {
        isVisible = false
    }

    // button with which the players can say they have changed their seats
    private val changeButton = Button(
        posX = 810,
        posY = 700,
        width = 300,
        height = 80,
        text = "Okay",
        font = Font(40, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(color = Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        isVisible = false
    }

    private val errorPlane1 = Label(
        posX = 460,
        posY = 490,
        width = 1000,
        height = 100,
        text = "",
        alignment = Alignment.CENTER,
        font = Font(70, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        visual = CompoundVisual(ColorVisual(color = Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        isVisible = false
    }
    private val errorPlane2 = Label(
        posX = 200,
        posY = 200,
        width = 1520,
        height = 680,
        alignment = Alignment.CENTER,
        visual = CompoundVisual(
            ColorVisual(color = Color(103, 119, 77, 180))
                .apply { style.borderRadius = BorderRadius(10) })
    ).apply {
        isVisible = false
    }

    private val errorButton = Button(
        posX = 900,
        posY = 720,
        width = 120,
        height = 45,
        text = "Okay",
        font = Font(30, Color(0xFFFFFFF), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(color = Color(82, 95, 61)).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        isVisible = false
        onMouseClicked = {
            errorPlane1.isVisible = false
            errorPlane2.isVisible = false
            isVisible = false
        }
    }

    init {
        // Add all components to the scene
        addComponents(
            playerHand,
            opponentHand,
            playerName,
            opponentName,
            drawStack,
            tradeArea,
            //discardArea,
            discardStack,
            playCombiButton,
            passButton,
            tradeButton,
            changePlane2,
            changePlane1,
            changeButton,
            errorPlane1,
            errorPlane2,
            errorButton
        )
    }

    private fun applyHoverEffect(cardView: CardView) {
        cardView.width = 162.0
        cardView.height = 250.0
        cardView.rotation = 0.0
        cardView.showFront()
    }

    private fun removeHoverEffect(cardView: CardView) {
        cardView.onMouseEntered = null
        cardView.onMouseExited = null
        cardView.width = 162.0
        cardView.height = 250.0
        cardView.rotation = 0.0
        cardView.showBack()
    }

    private fun makeItClickableTrade(index: Int, cardView: CardView) {
        cardView.onMouseClicked = {
            if (indexSelectedTradeCard.contains(index)) {
                indexSelectedTradeCard.remove(index)
                cardView.posY = 0.0
            } else {
                indexSelectedTradeCard.add(index)
                cardView.posY -= 50
            }
        }
    }

    private fun makeItClickableHand(index: Int, cardView: CardView) {
        cardView.onMouseClicked = {
            if (indexSelectedHandCards.contains(index)) {
                indexSelectedHandCards.remove(index)
                cardView.posY = 0.0
            } else {
                indexSelectedHandCards.add(index)
                cardView.posY -= 50
            }
        }
    }

    private fun errorWasThrown(exceptionMessage: String?){
        errorPlane1.isVisible = true
        errorPlane2.isVisible = true
        errorButton.isVisible = true
        checkNotNull(exceptionMessage)
        errorPlane1.text = exceptionMessage
    }

    /**
     * is called by the service layer after a new game with two players start
     */

    override fun refreshAfterStartNewGame() {
        //val game = rootService.currentGame ?: return
        cards.clear()
        CardValue.entries.forEach { value ->
            CardSuit.entries.forEach { suit ->
                cards[Card(suit, value)] = CardView(
                    height = 200,
                    width = 130,
                    front = CompoundVisual(cardImageLoader.frontImageFor(suit, value).apply {
                        style.borderRadius = BorderRadius(10)
                    }),
                    back = CompoundVisual(ImageVisual("card_back.png").apply {
                        style.borderRadius = BorderRadius(10)
                    })
                )
            }
        }
    }

    override fun refreshAfterEvaluatingCombi(player: Player, playedCombi: List<Card>) {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]

        playerName.text = "${curPlayer.name} : ${curPlayer.score} Points"

        playerHand.clear()
        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
                this.posY = 0.0
                makeItClickableHand(index, this)
            })
        }

        playedCombi.forEach { card ->
            discardStack.push(cards[card])
        }
    }

    override fun refreshAfterDrawCard(player: Player, card: Card) {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]

        playerHand.clear()
        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
                this.posY = 0.0
                makeItClickableHand(index, this)
            })
        }
        indexSelectedHandCards.clear()
        indexSelectedTradeCard.clear()
    }

    override fun refreshAfterSwapCard(player: Player, oldHandCard: Card, oldTradeCards: Card) {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]

        tradeArea.clear()
        playerHand.clear()

        game.tradeDeck.forEachIndexed { index, card ->
            tradeArea.add((cards[card]).apply {
                applyHoverEffect(this)
                this.posY = 0.0
                makeItClickableTrade(index, this)
            })
        }

        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
                this.posY = 0.0
                makeItClickableHand(index, this)
            })
        }
        indexSelectedHandCards.clear()
        indexSelectedTradeCard.clear()
    }

    override fun refreshAfterChangePlayer() {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]
        val opponent = game.players[(game.currentPlayer + 1) % game.players.size]

        indexSelectedHandCards.clear()
        indexSelectedTradeCard.clear()

        changePlane2.isVisible = true
        changePlane1.isVisible = true
        changeButton.isVisible = true
        passButton.isVisible = false
        tradeButton.isVisible = false

        changePlane1.text = "It is now ${curPlayer.name}'s turn!"

        playerName.text = "${curPlayer.name} : ${curPlayer.score} Points"
        opponentName.text = "${opponent.name} : ${opponent.score} Points"

        playerHand.clear()
        opponentHand.clear()
        discardStack.clear()
        tradeArea.clear()

        game.tradeDeck.forEachIndexed { index, card ->
            tradeArea.add((cards[card]).apply {
                applyHoverEffect(this)
                this.posY = 0.0
                makeItClickableTrade(index, this)
            })
        }

        curPlayer.handCards.forEach { card ->
            playerHand.add((cards[card]).apply {
                this.posY = 0.0
                removeHoverEffect(this)
            })
        }

        curPlayer.disposalArea.forEach { card ->
            discardStack.push(cards[card])
        }

        opponent.handCards.forEach { card ->
            opponentHand.add((cards[card]).apply {
                this.posY = 0.0
                removeHoverEffect(this)
            })
        }

        changeButton.onMouseClicked = {
            changePlane2.isVisible = false
            changePlane1.isVisible = false
            changeButton.isVisible = false
            passButton.isVisible = true
            tradeButton.isVisible = true

            playerHand.clear()
            curPlayer.handCards.forEachIndexed { index, card ->
                playerHand.add((cards[card]).apply {
                    applyHoverEffect(this)
                    this.posY = 0.0
                    makeItClickableHand(index, this)
                })
            }
        }
    }
}