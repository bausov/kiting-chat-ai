package com.github.bausov.kitingchatai.mcp.server.domain.core

data class Spot(
    val id: Long,
    val name: String,
    val location: Location,
    val windDirections: WindDirections
) {

    data class Location(
        val latitude: Double,
        val longitude: Double
    )

    data class WindDirections(
        val min: Double,
        val max: Double
    ) {

        // todo test
        fun accepted(direction: Double): Boolean {
            return if (min <= max) {
                direction in min..max
            } else {
                direction >= min || direction <= max
            }
        }
    }
}
