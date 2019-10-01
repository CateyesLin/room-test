package info.in_alley.lab.db

import androidx.room.*
import info.in_alley.lab.entity.Product
import io.reactivex.Maybe

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg products: Product): LongArray

    @Update
    fun update(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("SELECT * FROM products WHERE id = :id")
    fun findProductById(id: Long): Maybe<List<Product>>

    @Query("SELECT * FROM products WHERE name = :name")
    fun findProductByName(name: String): Maybe<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%'")
    fun findProductByKeyword(keyword: String): Maybe<List<Product>>
}