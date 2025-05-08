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
     * @throws IllegalStateException if a game is already running or if a string
     * with the [Player]'s name is empty
     */

    fun startGame(player1Name : String, player2Name : String) {
        if (rootService.currentGame != null) {
            throw IllegalStateException("Game is already running")
        }
        if (player1Name == ""){
            throw IllegalStateException("Player1's name is empty")
        }
        if (player2Name == ""){
            throw IllegalStateException("Player2's name is empty")
        }
        val players = listOf(Player(player1Name),Player(player2Name))
        val game = CombiDuel(players)

        rootService.currentGame = game

       val allCards = defaultRandomCardList()
        while(players[1].handCards.size <7){
            players[0].handCards.add(allCards.first())
            allCards.removeAt(0)
            players[1].handCards.add(allCards.first())
            allCards.removeAt(0)
        }

        while (game.tradeDeck.size < 3) {
            game.tradeDeck.add(allCards.first())
            allCards.removeAt(0)
        }
        game.tradeDeck.toList()

        game.drawStack.addAll(allCards)

        onAllRefreshables { refreshAfterStartNewGame() }
    }

    /**
     * EndGame is called after the games finishes which means after one player's hand cards are empty
     * or the two players passed as their only action after each other
     */

    fun endGame(){
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
        game.currentPlayer = (oldCurrentPlayer+1)%game.players.size

        onAllRefreshables { refreshAfterChangePlayer() }
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
