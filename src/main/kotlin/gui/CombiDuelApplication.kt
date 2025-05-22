package gui

import tools.aqua.bgw.core.*
import service.Refreshable
import service.RootService
import tools.aqua.bgw.util.Font

/**
 * Main class of the Combi Duel Application
 */

object CombiDuelApplication : BoardGameApplication("CombiDuel"), Refreshable {

    private val rootService = RootService()

    private val gameScene = CombiDuelScene(rootService)

    private val gameFinishedMenuScene = GameFinishedMenuScene(rootService)

    private val newGameMenuScene = NewGameMenuScene(rootService)

    init {
        loadFont("IBMPlexSerif-Medium.ttf", "IBMPlex Serif Medium", Font.FontWeight.MEDIUM)

        rootService.addRefreshables(this,
            gameScene,
            gameFinishedMenuScene,
            newGameMenuScene)

        this.showGameScene(gameScene)
        this.showMenuScene(newGameMenuScene, 0)
    }

    /**
     * Is called by the service layer after a new game was started.
     * It hides the Menu Scene after a specific amount of time.
     */

    override fun refreshAfterStartNewGame() {
        hideMenuScene(500)
    }

    /**
     * Is called by the service layer after a game was finished.
     * It calls the gameFinishedMenuScene.
     */

    override fun refreshAfterGameEnds() {
        showMenuScene(gameFinishedMenuScene)
    }

    /**
     * Is called by the service layer when the players decide after finishing one to start another.
     */

    override fun refreshAfterRestart() {
        showMenuScene(newGameMenuScene)
    }
}
