package swiss.avm.poschti.data.repository

import kotlinx.coroutines.flow.Flow
import swiss.avm.poschti.data.local.PoschtiDatabase
import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity
import swiss.avm.poschti.data.local.relation.RecipeWithIngredients

/**
 * Zentrale Datenzugriffsschicht. Kapselt die DAOs, damit ViewModels nicht
 * direkt auf Room zugreifen (siehe NFR-5: austauschbare Schicht).
 */
class PoschtiRepository(private val db: PoschtiDatabase) {

    private val productDao = db.productDao()
    private val recipeDao = db.recipeDao()

    // --- Produkte / Alltagsprodukte ---

    val products: Flow<List<ProductEntity>> = productDao.observeAll()
    val staples: Flow<List<ProductEntity>> = productDao.observeStaples()

    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.search(query)

    suspend fun getProduct(id: Long): ProductEntity? = productDao.getById(id)

    suspend fun saveProduct(product: ProductEntity): Long = productDao.upsert(product)

    suspend fun deleteProduct(product: ProductEntity) = productDao.delete(product)

    suspend fun getActiveStaples(): List<ProductEntity> = productDao.getActiveStaples()

    // --- Rezepte ---

    val recipes: Flow<List<RecipeEntity>> = recipeDao.observeAll()

    fun observeRecipe(id: Long): Flow<RecipeWithIngredients?> =
        recipeDao.observeWithIngredients(id)

    suspend fun getRecipe(id: Long): RecipeWithIngredients? =
        recipeDao.getWithIngredients(id)

    suspend fun saveRecipe(
        recipe: RecipeEntity,
        ingredients: List<RecipeIngredientEntity>,
    ): Long = recipeDao.upsertRecipeWithIngredients(recipe, ingredients)

    suspend fun deleteRecipe(recipe: RecipeEntity) = recipeDao.deleteRecipe(recipe)
}
