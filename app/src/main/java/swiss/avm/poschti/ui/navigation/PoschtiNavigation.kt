package swiss.avm.poschti.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector

/** Die Hauptbereiche der App (Tabs in der Bottom-Navigation). */
enum class PoschtiDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    PRODUCTS("products", "Produkte", Icons.Default.ShoppingBasket),
    RECIPES("recipes", "Rezepte", Icons.Default.MenuBook),
    WEEK("week", "Woche", Icons.Default.CalendarMonth),
    LIST("list", "Liste", Icons.AutoMirrored.Filled.ListAlt);

    companion object {
        val startDestination = PRODUCTS
    }
}
