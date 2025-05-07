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

    fun endGame(){
        onAllRefreshables { refreshAfterGameEnds() }
    }

    fun endTurn(){
        val game = rootService.currentGame
        checkNotNull(game)
        val oldCurrentPlayer = game.currentPlayer
        game.currentPlayer = (oldCurrentPlayer+1)%game.players.size

        onAllRefreshables { refreshAfterChangePlayer() }
    }

    private fun defaultRandomCardList() = List(52) {
        index ->
            Card(
                CardSuit.entries[index / 13],
                CardValue.entries[index % 13]
            )
        }.shuffled().toMutableList()

}
