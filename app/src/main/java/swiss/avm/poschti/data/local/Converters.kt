package swiss.avm.poschti.data.local

import androidx.room.TypeConverter
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.data.model.ProductSource
import swiss.avm.poschti.data.model.RecipeSource

/** Speichert Enums als ihren Namen (String) in der Datenbank. */
class Converters {
    @TypeConverter fun categoryToString(value: Category): String = value.name
    @TypeConverter fun stringToCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter fun unitToString(value: MeasureUnit): String = value.name
    @TypeConverter fun stringToUnit(value: String): MeasureUnit = MeasureUnit.valueOf(value)

    @TypeConverter fun productSourceToString(value: ProductSource): String = value.name
    @TypeConverter fun stringToProductSource(value: String): ProductSource = ProductSource.valueOf(value)

    @TypeConverter fun recipeSourceToString(value: RecipeSource): String = value.name
    @TypeConverter fun stringToRecipeSource(value: String): RecipeSource = RecipeSource.valueOf(value)
}
