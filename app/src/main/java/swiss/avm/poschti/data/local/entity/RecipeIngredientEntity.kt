package swiss.avm.poschti.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import swiss.avm.poschti.data.model.MeasureUnit

/**
 * Eine Zutat innerhalb eines Rezepts. Optional auf ein Katalog-[ProductEntity]
 * gemappt; [rawText] hält den ursprünglichen Text (z.B. aus dem Import).
 */
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("recipeId"), Index("productId")],
)
data class RecipeIngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val productId: Long? = null,
    val rawText: String,
    val quantity: Double? = null,
    val unit: MeasureUnit = MeasureUnit.NACH_BEDARF,
    /** Reihenfolge innerhalb des Rezepts. */
    val position: Int = 0,
)
