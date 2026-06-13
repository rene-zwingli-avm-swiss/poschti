package swiss.avm.poschti.ui.shopping

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.repository.PoschtiRepository
import swiss.avm.poschti.ui.AppViewModel

data class ShoppingUiState(
    val hasList: Boolean = false,
    val items: List<ShoppingItemEntity> = emptyList(),
) {
    val total: Int get() = items.size
    val checkedCount: Int get() = items.count { it.checked }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ShoppingViewModel(repository: PoschtiRepository) : AppViewModel(repository) {

    val uiState: StateFlow<ShoppingUiState> =
        repository.latestList
            .flatMapLatest { list ->
                if (list == null) {
                    flowOf(ShoppingUiState(hasList = false))
                } else {
                    repository.observeListItems(list.id)
                        .map { ShoppingUiState(hasList = true, items = it) }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShoppingUiState())

    fun toggleChecked(item: ShoppingItemEntity) {
        viewModelScope.launch { repository.setItemChecked(item, !item.checked) }
    }
}
