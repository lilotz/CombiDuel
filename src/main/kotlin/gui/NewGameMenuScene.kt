package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*

/**
 * NewGameMenuScene is the visual for starting a new game, where you can enter the name for the two players
 * and then start a new game.
 * You can also quit the game.
 *
 * @param rootService the [RootService] that manages the state of the game
 * */

class NewGameMenuScene(private val rootService: RootService) : MenuScene(1920, 1080), Refreshable {

    // Label that displays the name of the game.

    private val headlineLabel = Label(
        width = 700, height = 80,
        posX = 610, posY = 150,
        text = "Combi-Duel",
        font = Font(size = 60)
    )

    // label that displays a card just for the looks

    private val card1: Label = Label(
        posX = 500,
        posY = 250,
        width = 240,
        height = 360,
        text = "",
        alignment = Alignment.CENTER,
        visual = ImageVisual(
            path = "AceOfSpades.jpg"
        )
    ).apply{
        rotation = 330.0
    }

    // label that displays a card just for the looks

    private val card2: Label = Label(
        posX = 1200,
        posY = 250,
        width = 240,
        height = 360,
        text = "",
        alignment = Alignment.CENTER,
        visual = ImageVisual(
            path = "AceOfDiamonds.jpg"
        )
    ).apply{
        rotation = 30.0
    }

    // TextField where you can write the name of Player 1

    private val p1Input: TextField = TextField(
        width = 500, height = 80,
        posX = 710, posY = 400,
        prompt = "Name of Player 1",
        font = Font(
            size = 40,
            color = Color(0, 0, 0),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        ),
        visual = ColorVisual(201, 201, 201)
    ).apply {
        onKeyPressed = {
            startButton.isDisabled = this.prompt != "Name of Player 1" || p2Input.prompt != "Name of Player 2"
        }
    }

    // TextField where you can write the name of Player 2

    private val p2Input: TextField = TextField(
        width = 500, height = 80,
        posX = 710, posY = 500,
        prompt = "Name of Player 2",
        font = Font(
            size = 40,
            color = Color(0, 0, 0),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        ),
        visual = ColorVisual(201, 201, 201)
    ).apply {
        onKeyPressed = {
            startButton.isDisabled = this.prompt != "Name of Player 2" || p1Input.prompt != "Name of Player 1"
        }
    }

    // Button that starts a new game

    private val startButton = Button(
        width = 500, height = 80,
        posX = 710, posY = 650,
        text = "Start Game",
        font = Font(
            size = 40,
            color = Color(255, 255, 255),
            family = "Arial",
            fontWeight = Font.FontWeight.NORMAL,
            fontStyle = Font.FontStyle.NORMAL
        )
    ).apply {
        visual = ColorVisual(82, 95, 61)
        onMouseClicked = {
            rootService.gameService.startGame(
                p1Input.text.trim(),
                p2Input.text.trim()
            )
        }
    }

    // Button that exits the game

    private val quitButton = Button(
        width = 35, height = 35,
        posX = 1885, posY = 0,
        text = "X",
        font = Font(
            size = 18,
            color = Color(256, 256, 256),
            family = "Arial",
            fontWeight = Font.FontWeight.EXTRA_BOLD,
            fontStyle = Font.FontStyle.NORMAL
        ),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = ColorVisual(164, 18, 2)
    ).apply{
        onMouseClicked = {
            CombiDuelApplication.exit()
        }
    }

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        addComponents(
            headlineLabel,
            card1, card2,
            p1Input, p2Input,
            startButton, quitButton
        )
        background = ColorVisual(132, 153,99)
    }
}
