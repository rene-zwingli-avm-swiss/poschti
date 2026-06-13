package swiss.avm.poschti.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.viewModelFactory
import swiss.avm.poschti.PoschtiApplication
import swiss.avm.poschti.data.repository.PoschtiRepository

/** Basis-ViewModel mit Zugriff auf das Repository. */
abstract class AppViewModel(protected val repository: PoschtiRepository) : ViewModel()

/**
 * Baut eine ViewModel-Factory aus einer Funktion, die das Repository erhält.
 * Verwendung: `viewModel(factory = appViewModelFactory { ProductsViewModel(it) })`
 */
inline fun <VM : ViewModel> appViewModelFactory(
    crossinline create: (PoschtiRepository) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[APPLICATION_KEY] as PoschtiApplication
        create(app.repository)
    }
}
