package info.in_alley.lab.db

import androidx.room.*
import info.in_alley.lab.entity.Order
import info.in_alley.lab.entity.OrderProduct
import info.in_alley.lab.entity.Product
import io.reactivex.Maybe

@Dao
interface OrderProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(orderProduct: OrderProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg orderProducts: OrderProduct): LongArray

    @Update
    fun update(orderProduct: OrderProduct)

    @Delete
    fun delete(orderProduct: OrderProduct)

    @Query("SELECT * FROM order_products")
    fun findAll(): Maybe<List<OrderProduct>>

    @Query("SELECT * FROM order_products WHERE order_id = :orderId")
    fun findOrderProductsByOrderId(orderId: Long): Maybe<List<OrderProduct>>
}