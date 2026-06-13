package swiss.avm.poschti.ui.week

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.relation.EntryWithRecipe
import swiss.avm.poschti.ui.appViewModelFactory

@Composable
fun WeekScreen(onListGenerated: () -> Unit) {
    val vm: WeekViewModel = viewModel(factory = appViewModelFactory { WeekViewModel(it) })
    val state by vm.uiState.collectAsState()
    var showRecipePicker by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (state.entries.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { vm.generateList(onListGenerated) },
                        icon = { Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null) },
                        text = { Text("Liste erstellen") },
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
                FloatingActionButton(onClick = { showRecipePicker = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Rezept hinzufügen")
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            WeekHeader(
                weekLabel = state.weekLabel,
                dateRange = state.dateRange,
                onPrev = vm::previousWeek,
                onNext = vm::nextWeek,
                onToday = vm::today,
            )
            HorizontalDivider()

            if (state.entries.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Noch keine Rezepte für diese Woche.\nTippe auf +, um eines hinzuzufügen.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.entries, key = { it.entry.id }) { entry ->
                        PlannedRecipeRow(
                            entry = entry,
                            onMinus = { vm.changeServings(entry.entry, -1) },
                            onPlus = { vm.changeServings(entry.entry, +1) },
                            onRemove = { vm.removeEntry(entry.entry) },
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showRecipePicker) {
        RecipePickerSheet(
            recipes = state.availableRecipes,
            onPick = {
                vm.addRecipe(it)
                showRecipePicker = false
            },
            onDismiss = { showRecipePicker = false },
        )
    }
}

@Composable
private fun WeekHeader(
    weekLabel: String,
    dateRange: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrev) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Vorherige Woche")
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(weekLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onToday) { Text(dateRange) }
        }
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Nächste Woche")
        }
    }
}

@Composable
private fun PlannedRecipeRow(
    entry: EntryWithRecipe,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(entry.recipe.recipe.name) },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMinus) { Icon(Icons.Default.Remove, contentDescription = "Weniger Portionen") }
                Text("${entry.entry.servings} Portionen")
                IconButton(onClick = onPlus) { Icon(Icons.Default.Add, contentDescription = "Mehr Portionen") }
            }
        },
        trailingContent = {
            IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, contentDescription = "Entfernen") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipePickerSheet(
    recipes: List<RecipeEntity>,
    onPick: (RecipeEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "Rezept hinzufügen",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (recipes.isEmpty()) {
            Text(
                "Noch keine Rezepte vorhanden. Lege zuerst im Tab \"Rezepte\" eines an.",
                modifier = Modifier.padding(16.dp),
            )
        } else {
            LazyColumn(Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                items(recipes, key = { it.id }) { recipe ->
                    ListItem(
                        headlineContent = { Text(recipe.name) },
                        supportingContent = { Text("${recipe.defaultServings} Portionen") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(recipe) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
