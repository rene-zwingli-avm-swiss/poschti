package swiss.avm.poschti.ui.recipes

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.data.repository.PoschtiRepository
import swiss.avm.poschti.ui.AppViewModel

data class RecipesUiState(
    val recipes: List<RecipeEntity> = emptyList(),
)

class RecipesViewModel(repository: PoschtiRepository) : AppViewModel(repository) {

    val uiState: StateFlow<RecipesUiState> =
        repository.recipes
            .map { RecipesUiState(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecipesUiState())

    /**
     * Legt ein eigenes Rezept an. [ingredientLines] sind reine Textzeilen
     * (eine Zutat pro Zeile); das Mengen-/Produkt-Mapping folgt in Phase 1.5.
     */
    fun addRecipe(name: String, servings: Int, ingredientLines: List<String>) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val ingredients = ingredientLines
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, line ->
                RecipeIngredientEntity(
                    recipeId = 0,
                    rawText = line,
                    unit = MeasureUnit.NACH_BEDARF,
                    position = index,
                )
            }
        viewModelScope.launch {
            repository.saveRecipe(
                RecipeEntity(name = trimmed, defaultServings = servings.coerceAtLeast(1)),
                ingredients,
            )
        }
    }

    fun delete(recipe: RecipeEntity) {
        viewModelScope.launch { repository.deleteRecipe(recipe) }
    }
}
