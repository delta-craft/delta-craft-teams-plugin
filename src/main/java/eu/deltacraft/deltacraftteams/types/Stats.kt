package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.Serializable

@Serializable
data class StatsContent(
    val success: Boolean,
    val player: String,
    val stats: Stats?,
)

@Serializable
data class Stats(
    val mining: TotalMaterialStats,
    val crafting: TotalMaterialStats,
    val mob: MobTotalStats,
)

@Serializable
data class TotalMaterialStats(
    val data: List<MaterialStats>,
    val totalPoints: Int,
)

@Serializable
data class MaterialStats(
    val material: String,
    val count: Int,
)

@Serializable
data class MobTotalStats(
    val data: List<MobStats>,
    val totalPoints: Int,
)

@Serializable
data class MobStats(
    val entity: String,
    val count: Int,
)
