package swiss.avm.poschti.ui.shopping

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.ui.appViewModelFactory

@Composable
fun ShoppingScreen() {
    val vm: ShoppingViewModel = viewModel(factory = appViewModelFactory { ShoppingViewModel(it) })
    val state by vm.uiState.collectAsState()

    // FR-23: Solange dieser Screen sichtbar ist, bleibt der Bildschirm an.
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    if (!state.hasList) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                "Noch keine Einkaufsliste.\nErstelle sie im Tab \"Woche\".",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        ProgressHeader(checked = state.checkedCount, total = state.total)
        HorizontalDivider()

        val grouped = state.items
            .groupBy { it.category }
            .toSortedMap(compareBy { it.ordinal })

        LazyColumn(Modifier.fillMaxSize()) {
            grouped.forEach { (category, categoryItems) ->
                item(key = "header_${category.name}") {
                    Text(
                        category.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                val sorted = categoryItems.sortedWith(compareBy({ it.checked }, { it.name.lowercase() }))
                items(sorted, key = { it.id }) { item ->
                    ShoppingItemRow(item = item, onToggle = { vm.toggleChecked(item) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun ProgressHeader(checked: Int, total: Int) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "$checked von $total erledigt",
            style = MaterialTheme.typography.titleMedium,
        )
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else checked.toFloat() / total },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        )
    }
}

@Composable
private fun ShoppingItemRow(item: ShoppingItemEntity, onToggle: () -> Unit) {
    val decoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None
    val nameColor = if (item.checked) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    } else {
        Color.Unspecified
    }
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
        leadingContent = {
            Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
        },
        headlineContent = {
            Text(item.name, textDecoration = decoration, color = nameColor)
        },
        supportingContent = {
            val label = formatQuantity(item.quantity, item.unit)
            if (label.isNotEmpty()) Text(label, textDecoration = decoration)
        },
        colors = ListItemDefaults.colors(),
    )
}

private fun formatQuantity(quantity: Double?, unit: MeasureUnit): String {
    if (unit == MeasureUnit.NACH_BEDARF && quantity == null) return ""
    val qtyText = quantity?.let {
        if (it % 1.0 == 0.0) it.toInt().toString() else String.format("%.1f", it)
    }
    return listOfNotNull(qtyText, unit.displayName).joinToString(" ").trim()
}
