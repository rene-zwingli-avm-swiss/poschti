package swiss.avm.poschti.ui.products

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import swiss.avm.poschti.data.local.entity.ProductEntity
import swiss.avm.poschti.data.model.Category
import swiss.avm.poschti.data.model.MeasureUnit
import swiss.avm.poschti.data.repository.PoschtiRepository
import swiss.avm.poschti.ui.AppViewModel

data class ProductsUiState(
    val products: List<ProductEntity> = emptyList(),
)

class ProductsViewModel(repository: PoschtiRepository) : AppViewModel(repository) {

    val uiState: StateFlow<ProductsUiState> =
        repository.products
            .map { ProductsUiState(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProductsUiState())

    fun addProduct(name: String, category: Category, unit: MeasureUnit) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            repository.saveProduct(
                ProductEntity(name = trimmed, category = category, defaultUnit = unit),
            )
        }
    }

    fun toggleStaple(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product.copy(isStaple = !product.isStaple))
        }
    }

    fun delete(product: ProductEntity) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }
}
