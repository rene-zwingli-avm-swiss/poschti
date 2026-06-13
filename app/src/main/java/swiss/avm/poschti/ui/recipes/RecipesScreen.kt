package swiss.avm.poschti.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.model.RecipeSource
import swiss.avm.poschti.ui.appViewModelFactory

@Composable
fun RecipesScreen() {
    val vm: RecipesViewModel = viewModel(
        factory = appViewModelFactory { RecipesViewModel(it) },
    )
    val state by vm.uiState.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Rezept hinzufügen")
            }
        },
    ) { padding ->
        if (state.recipes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(
                    "Noch keine Rezepte.\nTippe auf +, um ein eigenes anzulegen.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(state.recipes, key = { it.id }) { recipe ->
                    RecipeRow(recipe = recipe, onDelete = { vm.delete(recipe) })
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAdd) {
        AddRecipeDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, servings, ingredients ->
                vm.addRecipe(name, servings, ingredients)
                showAdd = false
            },
        )
    }
}

@Composable
private fun RecipeRow(recipe: RecipeEntity, onDelete: () -> Unit) {
    val sourceLabel = when (recipe.source) {
        RecipeSource.OWN -> "Eigen"
        RecipeSource.FOOBY -> "Fooby"
        RecipeSource.SWISSMILK -> "Swissmilk"
        RecipeSource.MIGUSTO -> "Migusto"
    }
    ListItem(
        headlineContent = { Text(recipe.name) },
        supportingContent = { Text("$sourceLabel · ${recipe.defaultServings} Portionen") },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen")
            }
        },
    )
}

@Composable
private fun AddRecipeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, List<String>) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var servingsText by remember { mutableStateOf("4") }
    var ingredientsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neues Rezept") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = servingsText,
                    onValueChange = { servingsText = it.filter { c -> c.isDigit() } },
                    label = { Text("Portionen") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = ingredientsText,
                    onValueChange = { ingredientsText = it },
                    label = { Text("Zutaten (eine pro Zeile)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name,
                        servingsText.toIntOrNull() ?: 1,
                        ingredientsText.split("\n"),
                    )
                },
                enabled = name.isNotBlank(),
            ) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}
