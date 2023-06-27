package mb.safeEat.services.api.endpoints

import mb.safeEat.services.api.dto.IngredientDto
import mb.safeEat.services.api.models.Ingredient
import retrofit2.http.*

sealed interface IngredientEndpoint {
    @GET
    suspend fun findAll(): List<Ingredient>

    @GET("/{id}")
    suspend fun findById(@Path("id") id: String): Ingredient

    @POST
    suspend fun create(
        @Body ingredient: IngredientDto,
        @Query("restaurantId") restaurantId: String,
    ): Ingredient

    @POST("/many")
    suspend fun createMany(
        @Body ingredients: List<IngredientDto>,
        @Query("restaurantId") restaurantId: String,
    ): List<Ingredient>

    @PUT
    suspend fun update(@Body ingredient: IngredientDto): Ingredient

    @DELETE("/{id}")
    suspend fun delete(@Path("id") id: String, @Query("restaurantId") restaurantId: String)
}