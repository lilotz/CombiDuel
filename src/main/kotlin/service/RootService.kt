package service
import entity.*

/**
 * The root service class is responsible for managing services and the entity layer reference.
 * This class acts as a central hub for every other service within the application.
 *
 */
class RootService {

    val gameService = GameService(this)
    val playerActionService = PlayerActionService(this)

    var currentGame : CombiDuel? = null

    /**
    * adds Refreshables to GameService and PlayerActionService
    *
    * @param newRefreshable the [Refreshable] that should be added
     */

    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
    }

    /**
     * adds each of the given [Refreshable]s to the connected services
     *
     * @param newRefreshables the [Refreshable]s that should be added
     */

    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}