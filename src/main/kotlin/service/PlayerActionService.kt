package service
import entity.*

/**
 * Player Actions Service is responsible for every action the player can directly do
 * while playing CombiDuel
 *
 * @param rootService the [RootService] has direct access to the entity layer and the other service methods
 */

data class PlayerActionService(private val rootService: RootService) {

    /**
     * Swap Cards is an action the player can choose to do once a turn
     *
     * It takes a hand card and a card from the trade area in the middle
     *
     * Conditions:
     * - a game has to be active
     * - [Action.SWAP] has not already been used in this turn
     * - [handCard] is part of the [Player.handCards]
     * - [tradeCard] is part of the [CombiDuel.tradeDeck]
     *
     * @throws IllegalStateException if any of the conditions are violated
     *
     * @param handCard the player's selected hand card
     * @param tradeCard the trade card the player selects
     */

    fun swapCards(handCard : Int, tradeCard : Int) {
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        check(curPlayer.lastAction != Action.SWAP) { "You can only swap once a turn" }
        check(handCard <= curPlayer.handCards.size) { "Your card has to be part of your hand cards" }
        check(tradeCard <= game.tradeDeck.size) { "The trade card has to be in the trade area" }

        // change the hand card with the trade card
        val tempHandCard = curPlayer.handCards[handCard]
        val tempTradeCard = game.tradeDeck[tradeCard]
        curPlayer.handCards.remove(curPlayer.handCards[handCard])
        curPlayer.handCards.add(tempTradeCard)
        game.tradeDeck[tradeCard] = tempHandCard

        // if the last action isn't NULL, the SWAP is the 2nd one and since one can play only 2 actions
        // in a turn, the turn of the player automatically ends
        if(curPlayer.lastAction != Action.NULL){
            rootService.gameService.endTurn()
        }
        curPlayer.lastAction = Action.SWAP
    }

    /**
     *  Draw Card is an action the player can choose to do once a turn
     *
     *  It takes the first card from the hidden draw stack and adds it to the player's hand cards
     *
     *  Conditions:
     * - a game has to be active
     * - [Action.DRAW] has not already been used in this turn
     * - a player can only have 10 cards at a time in their hand
     * - the [CombiDuel.drawStack] cannot be empty
     *
     * @throws IllegalStateException if any of the conditions are violated
     */

    fun drawCard(){
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        check(curPlayer.lastAction != Action.DRAW) { "You can only draw a card once a turn" }
        check(game.drawStack.isNotEmpty()) { "Draw stack is empty, no card can be drawn" }
        check(curPlayer.handCards.size < 10) {"You can't have more than 10 hand cards" }

        curPlayer.handCards.add(game.drawStack.removeFirst())

        // if the last action isn't NULL, the DRAW is the 2nd one and since one can play only 2 actions
        // in a turn, the turn of the player automatically ends
        if(curPlayer.lastAction != Action.NULL){
            rootService.gameService.endTurn()
        }
        curPlayer.lastAction = Action.DRAW
    }

    /**
     *  Play Combi is an action the player can choose to do once a turn but as often as the player wants to
     *
     *  The valid combis are: Triple ([Player.score] += 10), Quadruple ([Player.score] += 15) and Sequence
     *  ([Player.score] += 2*number of cards)
     *
     *  The combis are evaluated in the respective functions:
     *  - [playTriple]
     *  - [playQuadruple]
     *  - [playSequence]
     *
     *  Conditions:
     * - a game has to be active
     * - [Action.COMBI] has not already been used in this turn
     * - a combi must consist of at least 3 and at max 10 [Player.handCards]
     * - it has to be a valid combi
     *
     * @throws IllegalStateException if any of the conditions are violated
     * @param combi a list of the chosen cards
     */

    fun playCombi(combi : MutableList<Int>){
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        check(curPlayer.lastAction != Action.COMBI) { "You can only play a Combi once a turn" }
        check(combi.size > 2 && combi.size < 11) {"You have to choose at least 2 and at max 10 cards"}
        for (i in combi.indices){
            check(combi[i] < curPlayer.handCards.size){"The chosen cards are not part of your hand cards"}}

        // triple
        if (combi.size == 3) {playTriple(combi)}
        // quadruple
        else if (combi.size == 4) {playQuadruple(combi)}
        // sequence
        else if (combi.isNotEmpty()) {playSequence(combi)}
        // since we empty combi after receiving a valid combi if everything does not apply,
        // it was not a valid combi
        else {throw IllegalArgumentException("The cards you've chosen were not a valid combi")}
    }

    /**
     * PlayTriple evaluates if the played combi is a triple
     *
     * A triple is three cards of the same [CardValue] and brings the player 10 points which will be added
     * to the [Player.score]
     *
     * If the [combi] is a valid triple, the cards will be removed from [Player.handCards] and added to the
     * [Player.disposalArea], also the score will be changed and [combi] emptied of all cards so [playCombi]
     * knows it can't be a different combi
     *
     * If they have different values, it can still be a sequence so the function just returns with all cards still
     * in [combi] to [playCombi]
     *
     * @throws IllegalStateException if a game is not currently active
     * @param combi a list of the chosen cards
     */

    private fun playTriple(combi : MutableList<Int>){
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        for(i in 1..2){
            if(curPlayer.handCards[combi[i-1]].value != curPlayer.handCards[combi[i]].value)
            {return}
        }
        for(i in 0..2){
            curPlayer.disposalArea.add(curPlayer.handCards[combi[i]])
            curPlayer.handCards.removeAt(combi[i])
        }
        combi.removeAll(combi)
        curPlayer.score += 10
        return
    }

    /**
     * PlayQuadruple evaluates if the played combi is a quadruple
     *
     * A quadruple is four cards of the same [CardValue] and brings the player 15 points which will be added
     * to the [Player.score]
     *
     * If the [combi] is a valid quadruple, the cards will be removed from [Player.handCards] and added to the
     * [Player.disposalArea], also the score will be changed and [combi] emptied of all cards so [playCombi]
     * knows it can't be a different combi
     *
     * If they have different values, it can still be a sequence so the function just returns with all cards still
     * in [combi] to [playCombi]
     *
     * @throws IllegalStateException if a game is not currently active
     * @param combi a list of the chosen cards
     */

    private fun playQuadruple(combi : MutableList<Int>){
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        for(i in 1..3){
            if(curPlayer.handCards[combi[i-1]].value != curPlayer.handCards[combi[i]].value)
            {return}
        }
        for(i in 0..3){
            curPlayer.disposalArea.add(curPlayer.handCards[combi[i]])
            curPlayer.handCards.removeAt(combi[i])
        }
        combi.removeAll(combi)
        curPlayer.score += 15
        return
    }

    /**
     * PlaySequence evaluates if the played combi is a sequence
     *
     * A sequence is at least three and at max ten cards of the same [CardSuit] and must be in a direct order
     *
     * It brings the player (2*number of cards) points which will be added to the [Player.score]
     *
     * If the [combi] is a valid sequence, the cards will be removed from [Player.handCards] and added to the
     * [Player.disposalArea], also the score will be changed and [combi] emptied of all cards so [playCombi]
     * knows it can't be a different combi
     *
     * Since it was already checked that [combi] is not a triple or a quadruple, if it is also not a sequenced
     * the played cards were not a valid combi
     *
     * @throws IllegalStateException if a game is not currently active, and it is not a valid sequence
     * @param combi a list of the chosen cards
     */

    private fun playSequence(combi : MutableList<Int>){
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]
        // combi has to be sorted by values, so it can't be evaluated better
        val combiSorted = mutableListOf<Card>()
        for(i in combi.indices-1){
            combiSorted.add(curPlayer.handCards[combi[i]])
        }
        combiSorted.sortedBy(Card::value)

        for(i in combiSorted.indices-1) {
            if(combiSorted[i].suit != combiSorted[i+1].suit)
            {return}
            // if the next card is not exactly one value bigger than the other
            if((combiSorted[i+1].value.compareTo(combiSorted[i].value))!=1)
            {return}
        }
//        val combiSorted = combi.sortedBy(curPlayer.handCards[combi].value)
        /*for(i in combi.indices-1){
            if(curPlayer.handCards[combi[i]].suit != curPlayer.handCards[combi[i+1]].suit)
            {return}
            // if the next card is not exactly one value bigger than the other
            if((curPlayer.handCards[combi[i+1]].value.compareTo(curPlayer.handCards[combi[i]].value))!=1)
            {return}
        }*/
        for(i in combi.indices){
            curPlayer.disposalArea.add(curPlayer.handCards[combi[i]])
            curPlayer.handCards.removeAt(combi[i])
        }
        curPlayer.score += 2*combi.size
        combi.removeAll(combi)
        return
    }

    /**
     * Pass is being used if a player chooses to pass as an action
     *
     * If [CombiDuel.passCheck] is true, [GameService.endGame] is called and the game ends
     *
     * If [CombiDuel.passCheck] is false and [Action.PASS] is the first action of the player in this turn,
     * [CombiDuel.passCheck] will be set to true
     *
     * Regardless of the action count, [Action.PASS] will be set as the [Player.lastAction]
     *
     * @throws IllegalStateException if no game is currently active
     */

    fun pass() {
        val game = rootService.currentGame
        checkNotNull(game)
        val curPlayer = game.players[game.currentPlayer]

        if (game.passCheck) {rootService.gameService.endGame()}
        else {
            if (curPlayer.lastAction == Action.NULL) {game.passCheck}
            curPlayer.lastAction = Action.PASS
            rootService.gameService.endTurn()
        }
    }
}


