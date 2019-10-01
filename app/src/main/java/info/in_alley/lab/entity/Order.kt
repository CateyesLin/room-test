package info.in_alley.lab.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "create_at")
    val createAt: Long
)

