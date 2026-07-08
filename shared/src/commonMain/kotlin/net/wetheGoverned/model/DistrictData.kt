package net.wetheGoverned.model

import kotlinx.serialization.Serializable

@Serializable
data class DistrictIdentifier(
    val state: String,
    val district: Int,
    val isStateOnly: Boolean = false,
    val isFederal: Boolean = false
) {
    val id: String get() = when {
        isFederal -> "us"
        isStateOnly -> "us-${state.lowercase()}"
        else -> "us-${state.lowercase()}-${district.toString().padStart(2, '0')}"
    }
    val displayName: String get() = when {
        isFederal -> "United States (Federal)"
        isStateOnly -> "$state (Statewide)"
        else -> "$state District $district"
    }
}

@Serializable
data class DistrictRegistryData(
    val version: String,
    val districts: List<DistrictIdentifier>,
    val zipMappings: Map<String, String> = emptyMap(),
    val stateDefaults: Map<String, String> = emptyMap()
)

object DistrictRegistry {
    private var currentData: DistrictRegistryData? = null
    
    // Default static data for initial load/offline
    val defaultDistricts = listOf(
        DistrictIdentifier("US", 0, isFederal = true),
        DistrictIdentifier("AL", 1), DistrictIdentifier("AL", 2), DistrictIdentifier("AL", 3),
        DistrictIdentifier("AL", 4), DistrictIdentifier("AL", 5), DistrictIdentifier("AL", 6), DistrictIdentifier("AL", 7),
        DistrictIdentifier("FL", 4), DistrictIdentifier("FL", 6), DistrictIdentifier("FL", 27),
        DistrictIdentifier("WA", 7), DistrictIdentifier("WA", 1),
        DistrictIdentifier("GA", 5), DistrictIdentifier("GA", 6),
        DistrictIdentifier("TX", 1), DistrictIdentifier("TX", 10),
        DistrictIdentifier("NY", 10), DistrictIdentifier("NY", 22),
        DistrictIdentifier("CA", 1), DistrictIdentifier("CA", 36)
    )

    var allDistricts: List<DistrictIdentifier> = defaultDistricts
        get() = currentData?.districts ?: defaultDistricts
        private set

    fun update(data: DistrictRegistryData) {
        currentData = data
        println("DistrictRegistry: Updated to version ${data.version} with ${data.districts.size} districts")
    }

    fun getZipMapping(zip: String): String? = currentData?.zipMappings?.get(zip)
    fun getStateDefault(stateCode: String): String? = currentData?.stateDefaults?.get(stateCode.lowercase())
}
