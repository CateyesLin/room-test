package info.in_alley.lab.db

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import info.in_alley.lab.entity.Order
import info.in_alley.lab.entity.OrderProduct
import info.in_alley.lab.entity.Product

@Database(entities = [Order::class, Product::class, OrderProduct::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    companion object {
        private var instance: MyDatabase? = null
        private var DB_NAME = "my_database"

        fun getInstance(context: Context): MyDatabase {
            return instance ?: Room.databaseBuilder(context, MyDatabase::class.java, DB_NAME)
                .openHelperFactory(LabSQLiteOpenHelperFactory())
                .fallbackToDestructiveMigration()
                .build().also { instance = it }
        }
    }

    abstract fun orderDao(): OrderDao
    abstract fun productDao(): ProductDao
    abstract fun orderProductDao(): OrderProductDao
}