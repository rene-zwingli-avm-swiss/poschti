package swiss.avm.poschti.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Wochenplan für eine ISO-Kalenderwoche. */
@Entity(
    tableName = "week_plans",
    indices = [Index(value = ["isoYear", "isoWeek"], unique = true)],
)
data class WeekPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val isoYear: Int,
    val isoWeek: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

/** Ein einem Wochenplan zugeordnetes Rezept mit Portionenzahl. */
@Entity(
    tableName = "week_plan_entries",
    foreignKeys = [
        ForeignKey(
            entity = WeekPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["weekPlanId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("weekPlanId"), Index("recipeId")],
)
data class WeekPlanEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekPlanId: Long,
    val recipeId: Long,
    val servings: Int = 4,
    /** Optionaler Wochentag 1 (Mo) .. 7 (So); null = ohne Tagesbezug. */
    val dayOfWeek: Int? = null,
)
