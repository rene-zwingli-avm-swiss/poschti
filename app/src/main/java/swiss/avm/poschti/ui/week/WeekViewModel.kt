package swiss.avm.poschti.ui.week

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import swiss.avm.poschti.data.local.entity.RecipeEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntryEntity
import swiss.avm.poschti.data.local.relation.EntryWithRecipe
import swiss.avm.poschti.data.repository.PoschtiRepository
import swiss.avm.poschti.ui.AppViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields
import java.time.temporal.WeekFields

data class WeekUiState(
    val planId: Long? = null,
    val weekLabel: String = "",
    val dateRange: String = "",
    val entries: List<EntryWithRecipe> = emptyList(),
    val availableRecipes: List<RecipeEntity> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class WeekViewModel(repository: PoschtiRepository) : AppViewModel(repository) {

    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.")

    /** Referenzdatum innerhalb der gewählten Woche. */
    private val anchor = MutableStateFlow(LocalDate.now())

    private val planFlow: StateFlow<WeekPlanEntity?> =
        anchor.mapLatest { date ->
            repository.getOrCreatePlan(
                year = date.get(IsoFields.WEEK_BASED_YEAR),
                week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val entriesFlow =
        planFlow.filterNotNull().flatMapLatest { repository.observePlanEntries(it.id) }

    val uiState: StateFlow<WeekUiState> =
        combine(anchor, planFlow, entriesFlow, repository.recipes) { date, plan, entries, recipes ->
            val monday = date.with(WeekFields.ISO.dayOfWeek(), 1)
            val sunday = monday.plusDays(6)
            WeekUiState(
                planId = plan?.id,
                weekLabel = "KW ${date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)} · ${date.get(IsoFields.WEEK_BASED_YEAR)}",
                dateRange = "${monday.format(dateFormat)} – ${sunday.format(dateFormat)}",
                entries = entries,
                availableRecipes = recipes,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeekUiState())

    fun previousWeek() { anchor.value = anchor.value.minusWeeks(1) }
    fun nextWeek() { anchor.value = anchor.value.plusWeeks(1) }
    fun today() { anchor.value = LocalDate.now() }

    fun addRecipe(recipe: RecipeEntity) {
        val planId = planFlow.value?.id ?: return
        viewModelScope.launch {
            repository.addRecipeToPlan(planId, recipe.id, recipe.defaultServings)
        }
    }

    fun changeServings(entry: WeekPlanEntryEntity, delta: Int) {
        val newServings = (entry.servings + delta).coerceAtLeast(1)
        viewModelScope.launch { repository.updateEntry(entry.copy(servings = newServings)) }
    }

    fun removeEntry(entry: WeekPlanEntryEntity) {
        viewModelScope.launch { repository.removeEntry(entry) }
    }

    /** Erzeugt die Einkaufsliste und ruft [onDone] mit Erfolg auf. */
    fun generateList(onDone: () -> Unit) {
        val planId = planFlow.value?.id ?: return
        viewModelScope.launch {
            repository.generateListForPlan(planId)
            onDone()
        }
    }
}
