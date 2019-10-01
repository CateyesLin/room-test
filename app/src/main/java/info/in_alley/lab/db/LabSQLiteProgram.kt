package info.in_alley.lab.db

import android.database.sqlite.SQLiteProgram
import androidx.sqlite.db.SupportSQLiteProgram

open class LabSQLiteProgram(private val mDelegate: SQLiteProgram) : SupportSQLiteProgram {

    override fun bindNull(index: Int) {
        mDelegate.bindNull(index)
    }

    override fun bindLong(index: Int, value: Long) {
        mDelegate.bindLong(index, value)
    }

    override fun bindDouble(index: Int, value: Double) {
        mDelegate.bindDouble(index, value)
    }

    override fun bindString(index: Int, value: String) {
        mDelegate.bindString(index, value)
    }

    override fun bindBlob(index: Int, value: ByteArray) {
        mDelegate.bindBlob(index, value)
    }

    override fun clearBindings() {
        mDelegate.clearBindings()
    }

    override fun close() {
        mDelegate.close()
    }
}