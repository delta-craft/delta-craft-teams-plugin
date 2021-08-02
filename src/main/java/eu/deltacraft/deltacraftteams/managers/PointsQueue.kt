package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.PointsList
import eu.deltacraft.deltacraftteams.types.getInt
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class PointsQueue(private val plugin: DeltaCraftTeams, private val clientManager: ClientManager) {

    private val configPayloadSize = plugin.config.getInt(Settings.PAYLOADSIZE)

    private val payloadSize = if (configPayloadSize > 2) configPayloadSize else 10

    private val timeStep = Constants.POINTS_SEND_TIME * 60 * 1000 //ms - x min*60s*1000ms
    private val points = PointsList()
    private val logger = plugin.logger

    private var taskId = 0
    private var timerId = 0
    private var lastSend: Long = 0

    private val isSending: Boolean
        get() = taskId > 0

    private val sizeReached: Boolean
        get() = points.size > payloadSize

    private val dateReached: Boolean
        get() = lastSend + timeStep < System.currentTimeMillis()

    private val shouldSend: Boolean
        get() = !isSending && (dateReached || sizeReached)

    private fun registerPoint(point: Point) {
        points.add(point)
        if (shouldSend) {
            trySendDefaultPoints()
        }
    }

    fun add(point: Point) {
        registerPoint(point)
    }

    fun add(points: Iterable<Point>) {
        for (point in points) {
            registerPoint(point)
        }
    }

    fun trySendAllPoints(initiator: Audience = Audience.empty()) {
        if (isSending) {
            initiator.sendMessage(TextHelper.infoText("Send is already pending", NamedTextColor.RED))
            return
        }
        if (!points.any()) {
            initiator.sendMessage(TextHelper.infoText("Nothing to send", NamedTextColor.YELLOW))
            return
        }
        val toSend = points.toList()
        points.clear()
        sendDefaultPoints(toSend, initiator)
        initiator.sendMessage(
            TextHelper.infoText("Send thread prepared. To send: ${toSend.count()} points", NamedTextColor.DARK_GREEN)
        )
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
        for (i in 1..payloadSize) {
            val point = points.poll() ?: break
            toSend.add(point)
        }
        sendDefaultPoints(toSend)
        return true
    }

    private fun sendDefaultPoints(toSend: Collection<Point>, initiator: Audience = Audience.empty()) {
        val task = Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {

                val chunked = toSend.chunked(payloadSize)

                for (points in chunked) {
                    sendPointsAsync(points, initiator)
                }

                taskId = 0
            }
        })
        taskId = task.taskId
    }

    private suspend fun sendPointsAsync(toSend: Collection<Point>, initiator: Audience = Audience.empty()): Boolean {
        if (toSend.isEmpty()) {
            return true
        }

        logger.info("Uploading ${toSend.size} point records....")
        initiator.sendMessage(TextHelper.infoText("Uploading ${toSend.size} point(s)", NamedTextColor.DARK_GREEN))

        val res = clientManager.uploadPoints(toSend)
        if (res.content) {
            logger.info("Points uploaded successfully")
            initiator.sendMessage(
                TextHelper.infoText("${toSend.size} point(s) uploaded successfully", NamedTextColor.GREEN)
            )
        } else {
            val resString = res.toString()
            logger.warning(resString)
            initiator.sendMessage(
                TextHelper.attentionText("Error sending points. $resString", NamedTextColor.RED)
            )
            this.add(toSend)
        }

        lastSend = System.currentTimeMillis()

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

    fun cancel(sender: CommandSender) {
        if (!isSending) {
            sender.sendMessage(TextHelper.infoText("No send is pending"))
            return
        }
        
        try {
            Bukkit.getScheduler().cancelTask(taskId)
            sender.sendMessage(TextHelper.infoText("Cancelled"))
        } catch (e: Exception) {
            sender.sendMessage(TextHelper.infoText(e.toString()))
        } finally {
            taskId = 0
        }
    }

}