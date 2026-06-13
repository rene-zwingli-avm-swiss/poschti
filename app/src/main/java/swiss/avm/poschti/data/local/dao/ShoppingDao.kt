package swiss.avm.poschti.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import swiss.avm.poschti.data.local.entity.ShoppingItemEntity
import swiss.avm.poschti.data.local.entity.ShoppingListEntity

@Dao
interface ShoppingDao {

    @Query("SELECT * FROM shopping_lists ORDER BY generatedAt DESC LIMIT 1")
    fun observeLatestList(): Flow<ShoppingListEntity?>

    @Query("SELECT * FROM shopping_items WHERE shoppingListId = :listId ORDER BY checked ASC, category ASC, name COLLATE NOCASE ASC")
    fun observeItems(listId: Long): Flow<List<ShoppingItemEntity>>

    @Insert
    suspend fun insertList(list: ShoppingListEntity): Long

    @Insert
    suspend fun insertItems(items: List<ShoppingItemEntity>)

    @Insert
    suspend fun insertItem(item: ShoppingItemEntity): Long

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_lists")
    suspend fun clearAllLists()

    /** Ersetzt jede bestehende Liste durch eine neue mit den gegebenen Positionen. */
    @Transaction
    suspend fun replaceList(list: ShoppingListEntity, items: List<ShoppingItemEntity>): Long {
        clearAllLists()
        val listId = insertList(list)
        insertItems(items.map { it.copy(shoppingListId = listId) })
        return listId
    }
}
