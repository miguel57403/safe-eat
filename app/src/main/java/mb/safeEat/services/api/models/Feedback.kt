package mb.safeEat.services.api.models

data class Feedback(
    val id: String,
    val rating: Int? = null,
    val comment: String? = null,
)
