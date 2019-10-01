package info.in_alley.lab.db

import androidx.room.*
import info.in_alley.lab.entity.Order
import io.reactivex.Maybe

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg orders: Order): LongArray

    @Update
    fun update(order: Order)

    @Delete
    fun delete(order: Order)

    @Query("SELECT * FROM orders WHERE id = :id")
    fun findOrderById(id: Long): Maybe<Order>
}