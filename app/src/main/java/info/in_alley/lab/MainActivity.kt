package info.in_alley.lab

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import info.in_alley.lab.db.MyDatabase
import info.in_alley.lab.db.OrderDao
import info.in_alley.lab.db.OrderProductDao
import info.in_alley.lab.db.ProductDao
import info.in_alley.lab.entity.Order
import info.in_alley.lab.entity.OrderProduct
import info.in_alley.lab.entity.Product
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var db: MyDatabase
    private lateinit var orderDao: OrderDao
    private lateinit var productDao: ProductDao
    private lateinit var orderProductDao: OrderProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MyDatabase.getInstance(this.applicationContext)
        orderDao = db.orderDao()
        productDao = db.productDao()
        orderProductDao = db.orderProductDao()

        reset_data_btn.setOnClickListener {
            Single
                .fromCallable {
                    initData()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }

        query_data_btn.setOnClickListener {
            val start = Date().time
            orderProductDao.findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val end = Date().time
                        Log.d("Lab", "Get data ${it.size} rows took ${end - start}ms.")
//                        val list = recyclerview
//                        list.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
//                        list.adapter =
                    },{}
                )
        }
    }

    private fun initData() {
        db.runInTransaction {
            db.query("DELETE FROM orders;", arrayOf())
//            db.query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'orders';", arrayOf())
            db.query("DELETE FROM products;", arrayOf())
//            db.query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'products';", arrayOf())
            db.query("DELETE FROM order_products;", arrayOf())
//            db.query("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'order_products';", arrayOf())
            db.query("VACUUM\n;", arrayOf())

            val start = Date().time
            val now = Date().time / 1000
            for (index in 1..10000) {
                orderDao.insert(
                    Order(
                        id = index.toLong(),
                        createAt = now
                    )
                )
            }

            for (index in 1..1000) {
                // 金額轉為 10^6 避免小數點
                val pricePower6 = ((Math.random() * 10000).toInt() * 1000000).toLong()
                productDao.insert(
                    Product(
                        id = index.toLong(),
                        name = "Product_$index",
                        pricePower6 = pricePower6
                    )
                )
            }

            for (index in 1..99999) {
                orderProductDao.insert(
                    OrderProduct(
                        id = index.toLong(),
                        orderId = index / 10.toLong() + 1,
                        productId = index / 100.toLong() + 1
                    )
                )
            }

            val end = Date().time
            Log.d("Lab", "Init data takes ${end - start}ms.")
        }
    }
}
