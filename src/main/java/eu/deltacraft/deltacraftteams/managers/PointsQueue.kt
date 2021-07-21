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

    private val shouldSend: Boolean
        get() = !isSending && (dateReached || sizeReached)

    fun registerPoint(point: Point) {
        points.add(point)
        if (shouldSend) {
            trySendDefaultPoints()
        }
    }

    fun add(point: Point) {
        registerPoint(point)
    }

    fun trySendAllPoints(): Boolean {
        if (isSending) {
            return false
        }
        if (!points.any()) {
            return true
        }
        val toSend = points.toList()
        points.clear()
        return sendDefaultPoints(toSend)
    }

    suspend fun trySendAllPointsAsync(): Boolean {
        if (isSending) {
            return false
        }
        val toSend = points.toList()
        points.clear()
        return sendPointsAsync(toSend)
    }

    private fun trySendDefaultPoints(): Boolean {
        if (isSending) {
            return false
        }
        if (!points.any()) {
            return true
        }
        val toSend = mutableListOf<Point>()
        for (i in 1..Constants.POINTS_PAYLOAD_SIZE) {
            val point = points.poll() ?: break
            toSend.add(point)
        }
        return sendDefaultPoints(toSend)
    }

    private fun sendDefaultPoints(toSend: Collection<Point>): Boolean {
        val task = Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {

                val chunked = toSend.chunked(Constants.POINTS_PAYLOAD_SIZE)

                for (points in chunked) {
                    sendPointsAsync(points)
                }

                taskId = 0
            }
        })
        taskId = task.taskId
        return true
    }

    private suspend fun sendPointsAsync(toSend: Collection<Point>): Boolean {
        if (toSend.isEmpty()) {
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
                trySendAllPoints()
            },
            0L,
            Constants.POINTS_SEND_TIME * 60 * 60 * 20L
        )
        timerId = task.taskId
    }

}