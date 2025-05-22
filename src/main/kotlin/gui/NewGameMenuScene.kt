package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*
import tools.aqua.bgw.style.*
import tools.aqua.bgw.visual.CompoundVisual

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
        font = Font(70, Color(0, 0, 0), "IBMPlex Serif Medium")
    )

    // label that displays a card just for the looks

    private val card1: Label = Label(
        posX = 500,
        posY = 250,
        width = 240,
        height = 360,
        text = "",
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ImageVisual(path = "AceOfSpades.jpg").apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
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
        visual = CompoundVisual(ImageVisual(path = "AceOfDiamonds.jpg").apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        rotation = 30.0
    }

    // TextField where you can write the name of Player 1

    private val p1Input: TextField = TextField(
        width = 500, height = 80,
        posX = 710, posY = 400,
        prompt = "Name of Player 1",
        font = Font(40, Color(0, 0, 0), "IBMPlex Serif Medium"),
        visual = CompoundVisual(ColorVisual(201, 201, 201).apply {
            style.borderRadius = BorderRadius(10)
        })
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
        font = Font(40, Color(0, 0, 0), "IBMPlex Serif Medium"),
        visual = CompoundVisual(ColorVisual(201, 201, 201).apply {
            style.borderRadius = BorderRadius(10)
        })
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
        font = Font(22, Color(255, 255, 255), "IBMPlex Serif Medium")
    ).apply {
        visual = CompoundVisual(ColorVisual(82, 95, 61).apply {
            style.borderRadius = BorderRadius(10)
        })
        try {
            onMouseClicked = {
                rootService.gameService.startGame(
                    p1Input.text.trim(),
                    p2Input.text.trim()
                )
            }
        } catch (exception: IllegalArgumentException) {
            errorWasThrown(exception)
        }
    }

    // Button that exits the game

    private val quitButton = Button(
        width = 35, height = 35,
        posX = 1885, posY = 0,
        text = "X",
        font = Font(18, Color(255, 255, 255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = CompoundVisual(ColorVisual(164, 18, 2).apply {
            style.borderRadius = BorderRadius(10)
        })
    ).apply {
        onMouseClicked = {
            CombiDuelApplication.exit()
        }
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

    // Initialize the scene by setting the background color and adding all components to the content pane
    init {
        addComponents(
            headlineLabel,
            card1, card2,
            p1Input, p2Input,
            startButton, quitButton,
            errorPlane1, errorPlane2,
            errorButton
        )
        background = ColorVisual(132, 153, 99)
    }

    private fun errorWasThrown(exception: Exception) {
        errorPlane1.isVisible = true
        errorPlane2.isVisible = true
        errorButton.isVisible = true
        errorPlane1.text = exception.toString()
    }
}
