package swiss.avm.poschti.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntryEntity

/** Ein Wochenplan-Eintrag samt dem zugehörigen Rezept und dessen Zutaten. */
data class EntryWithRecipe(
    @Embedded val entry: WeekPlanEntryEntity,
    @Relation(
        entity = RecipeEntity::class,
        parentColumn = "recipeId",
        entityColumn = "id",
    )
    val recipe: RecipeWithIngredients,
)
