package swiss.avm.poschti.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit

/** Eine generierte (oder manuell angelegte) Einkaufsliste. */
@Entity(
    tableName = "shopping_lists",
    foreignKeys = [
        ForeignKey(
            entity = WeekPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["weekPlanId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("weekPlanId")],
)
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekPlanId: Long? = null,
    val generatedAt: Long = System.currentTimeMillis(),
)

/** Eine Position auf der Einkaufsliste, abstreichbar via [checked]. */
@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["shoppingListId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("shoppingListId")],
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shoppingListId: Long,
    val productId: Long? = null,
    val name: String,
    val quantity: Double? = null,
    val unit: MeasureUnit = MeasureUnit.STUECK,
    val category: Category = Category.SONSTIGES,
    /** Im Warenkorb / abgestrichen. */
    val checked: Boolean = false,
    val manuallyAdded: Boolean = false,
)
