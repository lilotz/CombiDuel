package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.style.*
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual

/**
 * GameFinishedMenuScene declares the winner after a game was finished.
 * You can start a new game and also quit the game.
 *
 * @param rootService the [RootService] that manages the state of the game
 * */

class GameFinishedMenuScene(private val rootService: RootService) : MenuScene(1920, 1080,
        background = ColorVisual(Color(132, 153,99))), Refreshable {

    // label that displays "Congratulations"
    private val congratulationsLabel = Label(
        posX = 460,
        posY = 100,
        width = 1000,
        height = 250,
        text = "Congratulations!",
        font =  Font(56, Color(255,255,255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(82, 95, 61).apply{
            style.borderRadius = BorderRadius(10) })
    )

    // label that displays the name of the winner
    private val nameLabel = Label(
        posX = 460,
        posY = 365,
        width = 1000,
        height = 250,
        text = "",
        font =  Font(56, Color(255,255,255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(82, 95, 61).apply{
            style.borderRadius = BorderRadius(10) })
    )

    // label that displays the score of the winner
    private val scoreLabel = Label(
        posX = 460,
        posY = 630,
        width = 1000,
        height = 250,
        text = "",
        font = Font(56, Color(255,255,255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(82, 95, 61).apply{
            style.borderRadius = BorderRadius(10) })
    )

    // button that when clicked starts a new game
    private val startNewGameButton = Button(
        posX = 860,
        posY = 950,
        width = 200,
        height = 60,
        text = "Start New Game",
        font = Font(56, Color(255, 255, 255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        visual = CompoundVisual(ColorVisual(82, 95, 61).apply{
            style.borderRadius = BorderRadius(10) })
    ).apply {
        onMouseClicked = { rootService.gameService.onAllRefreshables { refreshAfterRestart() } }
    }

    // button that when clicked closes the game
    private val quitButton = Button(
        width = 35, height = 35,
        posX = 1885, posY = 0,
        text = "X",
        font = Font(18, Color(255,255,255), "IBMPlex Serif Medium"),
        alignment = Alignment.CENTER,
        isWrapText = false,
        visual = CompoundVisual(ColorVisual(164, 18, 2).apply{
            style.borderRadius = BorderRadius(10) })
    ).apply {
        onMouseClicked = { CombiDuelApplication.exit() }
    }

    // Initialize the scene by setting the background color and adding all components to the content pane
    init{
        addComponents(
            congratulationsLabel,
            nameLabel,
            scoreLabel,
            startNewGameButton,
            quitButton)
    }

    /**
     * Is called by the service layer after a game ends.
     * It determines the winner and display their name with the related score.
     */

    override fun refreshAfterGameEnds() {
        val game = rootService.currentGame ?: return

        if(game.players[0].score > game.players[1].score) {
            nameLabel.text = "The winner is ${game.players[0].name}"
            scoreLabel.text = "with ${game.players[0].score} points!" }
        else if( game.players[1].score > game.players[0].score) {
            nameLabel.text = "The winner is ${game.players[1].name}"
            scoreLabel.text = "with ${game.players[1].score} points!"
        }
        else{
            nameLabel.text = "There is no winner!"
            scoreLabel.text ="You both have ${game.players[1].score} points."
        }
    }
}