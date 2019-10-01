package info.in_alley.lab.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Long,
    val name: String,

    @ColumnInfo(name = "price_power6")
    val pricePower6: Long
)