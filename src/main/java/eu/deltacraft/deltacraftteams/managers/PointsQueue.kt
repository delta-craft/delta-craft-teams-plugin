package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.Point
import kotlinx.coroutines.runBlocking
import org.apache.commons.lang.time.DateUtils
import org.bukkit.Bukkit
import java.util.*

class PointsQueue(private val plugin: DeltaCraftTeams, private val clientManager: ClientManager) {

    private val points = LinkedList<Point>()
    private val logger = plugin.logger

    private var taskId = 0
    private var timerId = 0
    private var lastSend = Date(Long.MIN_VALUE)

    private val isSending: Boolean
        get() = taskId > 0

    private val sizeReached: Boolean
        get() = points.size > Constants.POINTS_PAYLOAD_SIZE

    private val dateReached: Boolean
        get() = lastSend > DateUtils.addHours(Date(), Constants.POINTS_SEND_TIME)

    val shouldSend: Boolean
        get() = !isSending && (dateReached || sizeReached)

    fun registerPoint(point: Point) {
        points.add(point)
    }

    fun sendAllPoints(): Boolean {
        if (isSending) {
            return false
        }
        val toSend = points.toList()
        points.clear()
        return sendPoints(toSend)
    }

    suspend fun sendAllPointsAsync(): Boolean {
        if (isSending) {
            return false
        }
        val toSend = points.toList()
        points.clear()
        return sendPointsAsync(toSend)
    }

    fun sendPoints(count: Int): Boolean {
        if (isSending) {
            return false
        }
        val toSend = mutableListOf<Point>()
        for (i in 1..count) {
            val point = points.poll() ?: break
            toSend.add(point)
        }
        return sendPoints(toSend)
    }

    private fun sendPoints(toSend: Collection<Point>): Boolean {
        val task = Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                sendPointsAsync(toSend)
            }
        })
        taskId = task.taskId
        return true
    }

    private suspend fun sendPointsAsync(toSend: Collection<Point>): Boolean {
        if (toSend.isNotEmpty()) {
            return true
        }

        logger.info("Uploading ${toSend.size} point records....")

        val res = clientManager.uploadPoints(toSend)
        if (res.content) {
            logger.info("Points uploaded successfully")
        } else {
            logger.warning(res.toString())
        }

        lastSend = Date()

        return res.content
    }

    fun startTimer() {
        require(timerId < 1)
        val task = Bukkit.getScheduler().runTaskTimer(
            plugin,
            Runnable {
                sendAllPoints()
            },
            0L,
            Constants.POINTS_SEND_TIME * 60 * 60 * 20L
        )
        timerId = task.taskId
    }

}