package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.SerialName
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
    override val data: List<MaterialStats>,
    override val totalPoints: Int,
) : ITotalStats<MaterialStats>

@Serializable
data class MaterialStats(
    @SerialName("material")
    override val name: String,
    override val count: Int,
) : IStats

@Serializable
data class MobTotalStats(
    override val data: List<MobStats>,
    override val totalPoints: Int,
) : ITotalStats<MobStats>

@Serializable
data class MobStats(
    @SerialName("entity")
    override val name: String,
    override val count: Int,
) : IStats

interface IStats {
    val name: String
    val count: Int
}

interface ITotalStats<T : IStats> : ITotalStatsBase {
    val data: List<T>
}

interface ITotalStatsBase {
    val totalPoints: Int
}
