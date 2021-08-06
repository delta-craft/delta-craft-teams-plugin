package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.types.points.MiningPoint
import java.util.*

class PointsList : LinkedList<Point>(), List<Point> {

    fun add(element: MiningPoint): Boolean {
        val same = this.filterIsInstance<MiningPoint>().lastOrNull { x ->
            x.isSimilar(element)
        } ?: return super.add(element)

        val newPoints = element.points + same.points
        val newTotalDrops = element.totalDrops + same.totalDrops
        val newCount = element.count + same.count

        val newPoint = MiningPoint(
            newPoints,
            element.playerUid,
            element.material,
            element.tool,
            newTotalDrops,
            newCount,
            same.start
        )

        // Remove original
        this.remove(same)

        return super.add(newPoint)
    }

    override fun add(element: Point): Boolean {
        if (element is MiningPoint) {
            return this.add(element)
        }

        return super.add(element)
    }
}