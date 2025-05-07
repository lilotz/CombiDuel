package service

import entity.*

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the GUI classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * GUI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */
interface Refreshable

/**
 * the necessary refreshes after starting a new game
 */

fun refreshAfterStartNewGame() {}

/**
 * refreshes after a valid combi was played
 *
 * @param player the player who played the combi
 * @param playedCombi the played cards which form a combi
 */

fun refreshAfterEvaluatingCombi(player: Player, playedCombi : List<Card>) {}

/**
 * refreshes after a card was drawn from the draw stack
 *
 * @param player the player who had drawn a card
 */

fun refreshAfterDrawCard(player: Player) {}

/**
 * refreshes after the player passed
 *
 * @param player the player who passed
 */

fun refreshAfterPass(player: Player) {}

/**
 * refreshes after a player swapped a hand card with a trade card
 *
 * @param player the player who swaps the cards
 * @param oldHandCard the hand card the player chose
 * @param oldTradeCards the trade card the player chose
 */

fun refreshAfterSwapCard(player: Player, oldHandCard: Card, oldTradeCards: Card) {}

/**
 * refreshes after a turn ends and the player changes
 */

fun refreshAfterChangePlayer() {}

/**
 * the necessary refreshes when the game ends
 */

fun refreshAfterGameEnds() {}