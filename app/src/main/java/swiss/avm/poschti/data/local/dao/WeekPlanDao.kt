package swiss.avm.poschti.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import swiss.avm.poschti.data.local.entity.WeekPlanEntity
import swiss.avm.poschti.data.local.entity.WeekPlanEntryEntity
import swiss.avm.poschti.data.local.relation.EntryWithRecipe

@Dao
interface WeekPlanDao {

    @Query("SELECT * FROM week_plans WHERE isoYear = :year AND isoWeek = :week LIMIT 1")
    suspend fun getPlan(year: Int, week: Int): WeekPlanEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlan(plan: WeekPlanEntity): Long

    /** Holt den Wochenplan für die Kalenderwoche oder legt ihn an. */
    @Transaction
    suspend fun getOrCreatePlan(year: Int, week: Int): WeekPlanEntity {
        getPlan(year, week)?.let { return it }
        insertPlan(WeekPlanEntity(isoYear = year, isoWeek = week))
        return getPlan(year, week)!!
    }

    @Transaction
    @Query("SELECT * FROM week_plan_entries WHERE weekPlanId = :planId ORDER BY id ASC")
    fun observeEntries(planId: Long): Flow<List<EntryWithRecipe>>

    @Transaction
    @Query("SELECT * FROM week_plan_entries WHERE weekPlanId = :planId ORDER BY id ASC")
    suspend fun getEntries(planId: Long): List<EntryWithRecipe>

    @Insert
    suspend fun insertEntry(entry: WeekPlanEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: WeekPlanEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: WeekPlanEntryEntity)
}
