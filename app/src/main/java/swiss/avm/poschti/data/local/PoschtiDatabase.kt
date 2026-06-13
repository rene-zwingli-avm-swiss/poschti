package swiss.avm.poschti.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import swiss.avm.poschti.data.local.dao.ProductDao
import swiss.avm.poschti.data.local.dao.RecipeDao
import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.RecipeIngredientEntity
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.local.entity.ShoppingListEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntryEntity

@Database(
    entities = [
        ProductEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        WeekPlanEntity::class,
        WeekPlanEntryEntity::class,
        ShoppingListEntity::class,
        ShoppingItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PoschtiDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: PoschtiDatabase? = null

        fun get(context: Context): PoschtiDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PoschtiDatabase::class.java,
                    "poschti.db",
                ).build().also { INSTANCE = it }
            }
    }
}
