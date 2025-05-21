package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import entity.*
import tools.aqua.bgw.animation.MovementAnimation
import tools.aqua.bgw.components.container.*
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.util.*
import tools.aqua.bgw.visual.*
import tools.aqua.bgw.components.uicomponents.*

/**
 * CombiDuelScene is the main scene where the game is being played.
 * It shows the current player's hand cards, the trade area, the draw stack and the discard area for played combis.
 * The opponent's hand cards are hidden at the top of the scene.
 * Next to the player's name is also their current score
 *
 * @param rootService the [RootService] that manages the state of the game
 */

class CombiDuelScene(private val rootService: RootService) :
    BoardGameScene(1920, 1080, background = ColorVisual(Color(132, 153,99))),
    Refreshable {

    //TODO: Fehlermeldungen mit try {} catch {}; ActionsLeft-Button

    private val cards: BidirectionalMap<Card, CardView> = BidirectionalMap()

    private val cardImageLoader : CardImageLoader = CardImageLoader()

    private var indexSelectedTradeCard : MutableList<Int> = mutableListOf()

    private var indexSelectedHandCards : MutableList<Int> = mutableListOf()

    // LinearLayout that contains of the player's hand cards
    private val playerHand = LinearLayout<CardView>(
        posX = 100,
        posY = 780,
        width = 1620,
        height = 250,
        alignment = Alignment.BOTTOM_CENTER,
        spacing = -60,
    ).apply{
        onMouseClicked={
            if(indexSelectedHandCards.size == 1 && indexSelectedTradeCard.size == 1){
                rootService.playerActionService.swapCards(indexSelectedHandCards[0], indexSelectedTradeCard[0])}
            }
        }

    // label with the player's name and their score
    private val playerName = Label(
        posX = 1920 - 250,
        posY = 1080 - 100,
        width = 200,
        height = 50,
        text = "Spieler",
        alignment = Alignment.CENTER,
        visual = ColorVisual(Color(82, 95, 61)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    )

    // LinearLayout that contains of the opponent's hand cards which are hidden
    private val opponentHand = LinearLayout<CardView>(
        posX = 0,
        posY = 75,
        width = 1920,
        height = 200,
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
        visual = ColorVisual(Color(82, 95, 61)),
        font = Font(22, Color(0xFFFFFFF), "JetBrains Mono ExtraBold")
    ).apply {
        rotation = 0.0
    }

    // stack whit cards that can be drawn
    private val drawStack = CardView(
        posX = 1500,
        posY = 415,
        width = 162,
        height = 250,
        front = ImageVisual("card_back.jpg")
    ).apply{
        onMouseClicked = {
            rootService.playerActionService.drawCard()
        }
    }

    // LinearLayout with three cards that can be traded
    private val tradeArea = LinearLayout<CardView>(
        posX = 700,
        posY = 415,
        width = 520,
        height = 250,
        alignment = Alignment.BOTTOM_CENTER,
        spacing = 30
    ).apply{
        onMouseClicked ={
        if(indexSelectedHandCards.size == 1 && indexSelectedTradeCard.size == 1){
       rootService.playerActionService.swapCards(indexSelectedHandCards[0], indexSelectedTradeCard[0])}
       }
    }

    // This CardStack is used to display the top card of the play stack
    private val discardArea = CardStack<CardView>(
        posX = 100,
        posY = 750,
        width = 162,
        height = 250,
        alignment = Alignment.CENTER
    )

    // label with "Drop Area"
    private val dropAreaLabel = Label(
        posX = 50,
        posY = 700,
        width = 262,
        height = 350,
        text = "DROP \n AREA",
        font = Font(30, Color(1, 1, 1))
    )

    // area to where the chosen cards which should form a combi can be delivered
    private val dropArea = Area<CardView>(
        posX = 50,
        posY = 700,
        width = 262,
        height = 350,
        visual = ColorVisual(
            color = Color(103, 119, 77, 180)
        )
    ).apply {
        onMouseClicked = {
            if(indexSelectedHandCards.size >2 && indexSelectedHandCards.size <11){
                rootService.playerActionService.playCombi(indexSelectedHandCards)
            }
        }
    }

    // button with which the current player can pass
    private val passButton = Button(
        posX = 1710,
        posY = 880,
        width = 120,
        height = 50,
        text = "Pass",
        font = Font(
            size = 12,
            color = Color(255, 255, 255),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        ),
        alignment = Alignment.CENTER,
        visual = ColorVisual(color = Color(82, 95, 61))
    ).apply{
        isVisible = false
        onMouseClicked = {
            rootService.playerActionService.pass()
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
        font = Font(
            size = 70,
            color = Color(255, 255, 255),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        ),
        visual = ColorVisual(
            color = Color(82, 95, 61)
        )
    ).apply{
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
        visual = ColorVisual(
            color = Color(103, 119, 77, 180)
        )
    ).apply{
        isVisible = false
    }

    // button with which the players can say they have changed their seats
    private val changeButton = Button(
        posX = 900,
        posY = 720,
        width = 120,
        height = 45,
        text = "Okay",
        font = Font(
            size = 30,
            color = Color(255,255,255),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        ),
        alignment = Alignment.CENTER,
        visual = ColorVisual(color = Color(82, 95, 61))
    ).apply {
        isVisible = false
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
            discardArea,
            dropAreaLabel,
            dropArea,
            passButton,
            changePlane2,
            changePlane1,
            changeButton
        )
    }

    private fun applyHoverEffect(cardView: CardView) {
        cardView.onMouseEntered = {
            cardView.posY -= 25
        }
        cardView.onMouseExited = {
            cardView.posY += 25
        }
        cardView.width = 162.0
        cardView.height = 250.0
        cardView.rotation = 0.0
        cardView.showFront()
        cardView.isDraggable = true
    }

    private fun removeHoverEffect(cardView: CardView) {
        cardView.onMouseEntered = null
        cardView.onMouseExited = null
        cardView.width = 130.0
        cardView.height = 200.0
        cardView.rotation = 0.0
        cardView.showBack()
        cardView.isDraggable = false
    }

    private fun makeItClickableTrade(index : Int, cardView: CardView) {
        cardView.onMouseClicked = {
            if (indexSelectedTradeCard.contains(index)) {
                indexSelectedTradeCard.remove(index)
                playAnimation(
                    MovementAnimation(
                        componentView = cardView,
                        byX = 0,
                        byY = 0,
                        duration = 0
                    )/*.apply {
                        onFinished = {
                            cardView.posY += 50
                        }
                    }*/
                )
            } else {
                cardView.onMouseClicked = {
                    indexSelectedTradeCard.add(index)
                    playAnimation(
                        MovementAnimation(
                            componentView = cardView,
                            byX = 0,
                            byY = -50,
                            duration = 0
                        )/*.apply {
                            onFinished = {
                                cardView.posY -= 50
                            }
                        }*/
                    )
                }
            }
        }
    }

    private fun makeItClickableHand(index: Int, cardView: CardView){
        cardView.onMouseClicked = {
            if (indexSelectedHandCards.contains(index)) {
                indexSelectedHandCards.remove(index)
                playAnimation(
                        MovementAnimation(
                            componentView = cardView,
                            byX = 0,
                            byY = 0,
                            duration = 0
                    )/*.apply {
                        onFinished = {
                            cardView.posY += 50
                       }*/
                )
            } else {
                indexSelectedHandCards.add(index)
                playAnimation(
                    MovementAnimation(
                        componentView = cardView,
                        byX = 0,
                        byY = -50,
                        duration = 0
                    )/*.apply {
                        onFinished = {
                            cardView.posY -= 50
                        }
                    }*/
                )
            }
        }
    }


    override fun refreshAfterStartNewGame() {
        //val game = rootService.currentGame ?: return
        cards.clear()
        CardValue.entries.forEach{ value ->
            CardSuit.entries.forEach { suit ->
                cards[Card(suit, value)] = CardView(
                    height = 200,
                    width = 130,
                    front = cardImageLoader.frontImageFor(suit, value),
                    back = ImageVisual("card_back.jpg")
                )
            }
        }
    }

    override fun refreshAfterEvaluatingCombi(player: Player, playedCombi: List<Card>) {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]

        playerName.text = "${curPlayer.name} : ${curPlayer.score} Points"
        //playerName.text = "${curPlayer.name} : ${curPlayer.score.toString()} Points"

        playerHand.clear()
        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
                makeItClickableHand(index, this)
            })
        }

        playedCombi.forEach{ card ->
            discardArea.push(cards[card])
        }
    }

    override fun refreshAfterDrawCard(player: Player, card: Card) {
        val game = rootService.currentGame ?: return
        val curPlayer = game.players[game.currentPlayer]

        playerHand.clear()
        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
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
                makeItClickableTrade(index, this)
            })
        }

        curPlayer.handCards.forEachIndexed { index, card ->
            playerHand.add((cards[card]).apply {
                applyHoverEffect(this)
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

        changePlane1.text = "It is now ${curPlayer.name}'s turn!"

        playerName.text = "${curPlayer.name} : ${curPlayer.score} Points"
        //playerName.text = "${curPlayer.name} : ${curPlayer.score.toString()} Points"
        opponentName.text = "${opponent.name} : ${opponent.score} Points"
        //opponentName.text = "${opponent.name} : ${opponent.score.toString()} Points"

        playerHand.clear()
        opponentHand.clear()
        discardArea.clear()
        tradeArea.clear()

        game.tradeDeck.forEachIndexed { index, card ->
            tradeArea.add((cards[card]).apply {
                applyHoverEffect(this)
                makeItClickableTrade(index, this)
            })
        }

        curPlayer.handCards.forEach { card ->
            playerHand.add((cards[card]).apply {
                removeHoverEffect(this)
            })
        }

        curPlayer.disposalArea.forEach { card ->
            discardArea.push(cards[card])
        }

        opponent.handCards.forEach { card ->
            opponentHand.add((cards[card]).apply {
                removeHoverEffect(this)
            })
        }

        changeButton.onMouseClicked = {
            changePlane2.isVisible = false
            changePlane1.isVisible = false
            changeButton.isVisible = false
            passButton.isVisible = true

            playerHand.clear()
            curPlayer.handCards.forEachIndexed { index, card ->
                playerHand.add((cards[card]).apply {
                    applyHoverEffect(this)
                    makeItClickableHand(index, this)
                })
            }
        }
    }
}