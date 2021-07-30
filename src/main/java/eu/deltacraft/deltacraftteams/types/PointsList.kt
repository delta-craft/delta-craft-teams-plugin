package eu.deltacraft.deltacraftteams.types

import java.util.*

class PointsList : LinkedList<Point>(), List<Point> {

    override fun add(element: Point): Boolean {
        return super.add(element)
    }
}