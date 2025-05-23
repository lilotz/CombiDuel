package service
import entity.*

/**
 * Game Service is located in the Service Layer and responsible for
 * everything the player is not allowed to do
 *
 * @param rootService the [RootService] has direct access to the entity layer and the other service methods
 */

data class GameService(private val rootService: RootService): AbstractRefreshingService() {

    /**
     * StartGame is responsible for starting a new game
     *
     * The given names are used to initialize the two players
     *
     * @param player1Name name of player 1
     * @param player2Name name of player 2
     *
     * @throws IllegalArgumentException if f a game is already running
     * or if a string with the [Player]'s name is empty
     */

    fun startGame(player1Name : String, player2Name : String) {
        require(rootService.currentGame == null) {"Game is already running"}
        require(player1Name != ""){"Player1's name is empty"}
        require(player2Name != ""){"Player2's name is empty"}

        val players = listOf(Player(player1Name),Player(player2Name))
        val game = CombiDuel(players)

        rootService.currentGame = game

       val allCards = defaultRandomCardList()
        repeat(7) {
            players[0].handCards.add(allCards.first())
            allCards.removeAt(0)
            players[1].handCards.add(allCards.first())
            allCards.removeAt(0)
        }

        repeat(3){
            game.tradeDeck.add(allCards.first())
            allCards.removeAt(0)
        }
        game.tradeDeck.toList()

        game.drawStack.addAll(allCards)

        game.currentPlayer = (0..1).random()

        onAllRefreshables { refreshAfterStartNewGame() }
        endTurn()
    }

    /**
     * EndGame is called after the games finishes which means after one player's hand cards are empty
     * or the two players passed as their only action after each other
     * @throws IllegalStateException if no game is active
     * @throws IllegalArgumentException if endGame has been incorrectly called
     */

    fun endGame(){
        val game = rootService.currentGame
        checkNotNull(game)
        require(game.players[game.currentPlayer].handCards.isEmpty() || game.passCheck)
        {"The conditions for ending the games are not met" }
        onAllRefreshables { refreshAfterGameEnds() }
    }

    /**
     * EndTurn is called after a player played two actions or passed
     *
     * For the old player the last action is changed to NULL for the next turn
     *
     * It also changes the current player to the other player
     *
     * @throws IllegalStateException if no game is currently active
     */

    fun endTurn(){
        val game = rootService.currentGame
        checkNotNull(game)
        val oldCurrentPlayer = game.currentPlayer
        game.players[oldCurrentPlayer].lastAction = Action.NULL
        game.players[oldCurrentPlayer].secondActionCombi = false
        game.currentPlayer = (oldCurrentPlayer+1)%game.players.size

        onAllRefreshables { refreshAfterChangePlayer() }
    }

    /**
     * Function for restarting a new game
     *
     * Sets the rootService.currentGame back to null
     */
    fun restartNewGame(){
        rootService.currentGame = null
        onAllRefreshables { refreshAfterRestart() }
    }

    /**
     * DefaultRandomCardLists shuffles the necessary 52 cards for the start of the game
     */

    private fun defaultRandomCardList() = List(52) {
        index ->
            Card(
                CardSuit.entries[index / 13],
                CardValue.entries[index % 13]
            )
        }.shuffled().toMutableList()
}
