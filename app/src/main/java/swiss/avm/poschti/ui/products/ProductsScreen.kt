package swiss.avm.poschti.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.viewmodel.compose.viewModel
import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.ui.appViewModelFactory

@Composable
fun ProductsScreen() {
    val vm: ProductsViewModel = viewModel(
        factory = appViewModelFactory { ProductsViewModel(it) },
    )
    val state by vm.uiState.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Produkt hinzufügen")
            }
        },
    ) { padding ->
        if (state.products.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(
                    "Noch keine Produkte.\nTippe auf +, um eines anzulegen.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(state.products, key = { it.id }) { product ->
                    ProductRow(
                        product = product,
                        onToggleStaple = { vm.toggleStaple(product) },
                        onDelete = { vm.delete(product) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (showAdd) {
        AddProductDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, category, unit ->
                vm.addProduct(name, category, unit)
                showAdd = false
            },
        )
    }
}

@Composable
private fun ProductRow(
    product: ProductEntity,
    onToggleStaple: () -> Unit,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(product.name) },
        supportingContent = { Text(product.category.displayName) },
        trailingContent = {
            Row {
                IconButton(onClick = onToggleStaple) {
                    if (product.isStaple) {
                        Icon(Icons.Default.Star, contentDescription = "Alltagsprodukt", tint = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(Icons.Outlined.StarBorder, contentDescription = "Als Alltagsprodukt markieren")
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Löschen")
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Category, MeasureUnit) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.SONSTIGES) }
    var unit by remember { mutableStateOf(MeasureUnit.STUECK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neues Produkt") },
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
                EnumDropdown(
                    label = "Kategorie",
                    options = Category.entries,
                    selected = category,
                    optionLabel = { it.displayName },
                    onSelected = { category = it },
                )
                EnumDropdown(
                    label = "Einheit",
                    options = MeasureUnit.entries,
                    selected = unit,
                    optionLabel = { it.displayName },
                    onSelected = { unit = it },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, category, unit) },
                enabled = name.isNotBlank(),
            ) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
