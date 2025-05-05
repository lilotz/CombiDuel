package service
import entity.*

/**
 * Game Service is located in the Service Layer and responsible for
 * everything the player is not allowed to do
 *
 * @param rootService the [RootService] has direct access to the entity layer and the other service methods
 */

data class GameService(private val rootService: RootService) {

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
        createDrawStack()
        createTradeDeck()
    }

    private fun createDrawStack(){

    }

    private fun createTradeDeck(){
        val game = rootService.currentGame
        checkNotNull(game)

        //TODO: tradeDeck immutable machen (mutableList.toList())
    }

    fun endGame(){

    }

    fun endTurn(){


    }

}
