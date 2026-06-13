package swiss.avm.poschti.data.repository

import kotlinx.coroutines.flow.Flow
import swiss.avm.poschti.data.local.PoschtiDatabase
import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.local.entity.ShoppingListEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntryEntity
import swiss.avm.poschti.data.local.relation.EntryWithRecipe
import swiss.avm.poschti.data.local.relation.RecipeWithIngredients
import swiss.avm.poschti.data.util.ShoppingListGenerator

/**
 * Zentrale Datenzugriffsschicht. Kapselt die DAOs, damit ViewModels nicht
 * direkt auf Room zugreifen (siehe NFR-5: austauschbare Schicht).
 */
class PoschtiRepository(private val db: PoschtiDatabase) {

    private val productDao = db.productDao()
    private val recipeDao = db.recipeDao()
    private val weekPlanDao = db.weekPlanDao()
    private val shoppingDao = db.shoppingDao()

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

    // --- Wochenplan ---

    suspend fun getOrCreatePlan(year: Int, week: Int): WeekPlanEntity =
        weekPlanDao.getOrCreatePlan(year, week)

    fun observePlanEntries(planId: Long): Flow<List<EntryWithRecipe>> =
        weekPlanDao.observeEntries(planId)

    suspend fun addRecipeToPlan(planId: Long, recipeId: Long, servings: Int) {
        weekPlanDao.insertEntry(
            WeekPlanEntryEntity(weekPlanId = planId, recipeId = recipeId, servings = servings),
        )
    }

    suspend fun updateEntry(entry: WeekPlanEntryEntity) = weekPlanDao.updateEntry(entry)

    suspend fun removeEntry(entry: WeekPlanEntryEntity) = weekPlanDao.deleteEntry(entry)

    // --- Einkaufsliste ---

    val latestList: Flow<ShoppingListEntity?> = shoppingDao.observeLatestList()

    fun observeListItems(listId: Long): Flow<List<ShoppingItemEntity>> =
        shoppingDao.observeItems(listId)

    suspend fun setItemChecked(item: ShoppingItemEntity, checked: Boolean) =
        shoppingDao.updateItem(item.copy(checked = checked))

    /** Erzeugt aus aktiven Alltagsprodukten + Rezepten des Plans eine neue Liste. */
    suspend fun generateListForPlan(planId: Long): Long {
        val staples = productDao.getActiveStaples()
        val entries = weekPlanDao.getEntries(planId)
        val items = ShoppingListGenerator.generate(staples, entries)
        return shoppingDao.replaceList(ShoppingListEntity(weekPlanId = planId), items)
    }
}
