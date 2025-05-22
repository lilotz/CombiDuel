package service

import entity.*

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 *
 * @constructor Creates a new [TestRefreshable] with the given [rootService]
 *
 * @param rootService The root service to which this service belongs
 */

class TestRefreshable(val rootService: RootService) : Refreshable {
    var refreshAfterStartNewGame: Boolean = false
        private set

    var refreshAfterEvaluatingCombi: Boolean = false
        private set

    var refreshAfterDrawCard: Boolean = false
        private set

    var refreshAfterSwapCard: Boolean = false
        private set

    var refreshAfterChangePlayer: Boolean = false
        private set

    var refreshAfterGameEnds: Boolean = false
        private set

    var refreshAfterRestart: Boolean = false
        private set

    /**
     * Resets all called properties to false
     */
    fun reset() {
        refreshAfterStartNewGame = false
        refreshAfterEvaluatingCombi = false
        refreshAfterDrawCard = false
        refreshAfterSwapCard = false
        refreshAfterChangePlayer = false
        refreshAfterGameEnds = false
        refreshAfterRestart = false
    }

    override fun refreshAfterStartNewGame() {
        refreshAfterStartNewGame = true
    }

    override fun refreshAfterEvaluatingCombi(player: Player, playedCombi: List<Card>) {
        refreshAfterEvaluatingCombi = true
    }

    override fun refreshAfterDrawCard(player: Player, card: Card) {
        refreshAfterDrawCard = true
    }

    override fun refreshAfterSwapCard(player: Player, oldHandCard: Card, oldTradeCards: Card) {
        refreshAfterSwapCard = true
    }

    override fun refreshAfterChangePlayer() {
        refreshAfterChangePlayer = true
    }

    override fun refreshAfterGameEnds() {
        refreshAfterGameEnds = true
    }

    override fun refreshAfterRestart() {
        refreshAfterRestart = true
    }
}