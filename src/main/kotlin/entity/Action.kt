package entity

/**
 * Enum for possible actions the players can play
 * which entails: Draw, Swap, Combi, Pass
 * and Null is being used to initialize [Player.lastAction] if no action was played in the current turn
 */

enum class Action {
    DRAW,
    SWAP,
    COMBI,
    PASS,
    NULL,
    ;
}