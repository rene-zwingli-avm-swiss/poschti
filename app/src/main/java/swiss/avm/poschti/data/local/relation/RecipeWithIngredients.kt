package swiss.avm.poschti.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity

/** Ein Rezept samt seinen Zutaten. */
data class RecipeWithIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId",
    )
    val ingredients: List<RecipeIngredientEntity>,
)
