package swiss.avm.poschti.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import swiss.avm.poschti.data.model.RecipeSource

/** Ein Rezept (eigen oder importiert). Zutaten siehe [RecipeIngredientEntity]. */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val source: RecipeSource = RecipeSource.OWN,
    /** Quell-Link bei importierten Rezepten. */
    val sourceUrl: String? = null,
    val defaultServings: Int = 4,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
