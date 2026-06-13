package swiss.avm.poschti.ui.recipes

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity
import swiss.avm.poschti.data.repository.PoschtiRepository
import swiss.avm.poschti.data.util.IngredientParser
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
     * Legt ein eigenes Rezept an. [ingredientLines] sind Textzeilen
     * (eine Zutat pro Zeile), z.B. "200 g Mehl". Sie werden via
     * [IngredientParser] in Menge/Einheit/Name zerlegt.
     */
    fun addRecipe(name: String, servings: Int, ingredientLines: List<String>) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val ingredients = ingredientLines
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapIndexed { index, line ->
                val parsed = IngredientParser.parse(line)
                RecipeIngredientEntity(
                    recipeId = 0,
                    rawText = line,
                    name = parsed.name,
                    quantity = parsed.quantity,
                    unit = parsed.unit,
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
