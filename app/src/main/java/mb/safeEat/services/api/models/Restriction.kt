package mb.safeEat.services.api.models

data class Restriction(
    val id: String,
    val name: String? = null,
    val description: String? = null,
    val isRestricted: Boolean? = null,
)
