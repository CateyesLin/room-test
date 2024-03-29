package info.in_alley.lab.db

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.*
import android.os.Build
import android.os.CancellationSignal
import android.text.TextUtils.isEmpty
import android.util.Log
import android.util.Pair
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteStatement
import java.io.IOException
import java.util.*

/**
 * Creates a wrapper around [SQLiteDatabase].
 *
 * @param delegate The delegate to receive all calls.
 */
class LabSQLiteDatabase(val mDelegate: SQLiteDatabase) : SupportSQLiteDatabase {
    private val CONFLICT_VALUES = arrayOf("", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE ")
    private val EMPTY_STRING_ARRAY = arrayOfNulls<String>(0)

    override fun compileStatement(sql: String): SupportSQLiteStatement {
        return LabSQLiteStatement(mDelegate.compileStatement(sql))
    }

    override fun beginTransaction() {
        mDelegate.beginTransaction()
    }

    override fun beginTransactionNonExclusive() {
        mDelegate.beginTransactionNonExclusive()
    }

    override fun beginTransactionWithListener(transactionListener: SQLiteTransactionListener) {
        mDelegate.beginTransactionWithListener(transactionListener)
    }

    override fun beginTransactionWithListenerNonExclusive(
        transactionListener: SQLiteTransactionListener
    ) {
        mDelegate.beginTransactionWithListenerNonExclusive(transactionListener)
    }

    override fun endTransaction() {
        mDelegate.endTransaction()
    }

    override fun setTransactionSuccessful() {
        mDelegate.setTransactionSuccessful()
    }

    override fun inTransaction(): Boolean {
        return mDelegate.inTransaction()
    }

    override fun isDbLockedByCurrentThread(): Boolean {
        return mDelegate.isDbLockedByCurrentThread
    }

    override fun yieldIfContendedSafely(): Boolean {
        return mDelegate.yieldIfContendedSafely()
    }

    override fun yieldIfContendedSafely(sleepAfterYieldDelay: Long): Boolean {
        return mDelegate.yieldIfContendedSafely(sleepAfterYieldDelay)
    }

    override fun getVersion(): Int {
        return mDelegate.version
    }

    override fun setVersion(version: Int) {
        mDelegate.version = version
    }

    override fun getMaximumSize(): Long {
        return mDelegate.maximumSize
    }

    override fun setMaximumSize(numBytes: Long): Long {
        return mDelegate.setMaximumSize(numBytes)
    }

    override fun getPageSize(): Long {
        return mDelegate.pageSize
    }

    override fun setPageSize(numBytes: Long) {
        mDelegate.pageSize = numBytes
    }

    override fun query(query: String): Cursor {
        return query(SimpleSQLiteQuery(query))
    }

    override fun query(query: String, bindArgs: Array<Any>): Cursor {
        return query(SimpleSQLiteQuery(query, bindArgs))
    }


    override fun query(supportQuery: SupportSQLiteQuery): Cursor {
        Log.d("Lab", supportQuery.sql)
        return mDelegate.rawQueryWithFactory({ db, masterQuery, editTable, query ->
            supportQuery.bindTo(LabSQLiteProgram(query))
            SQLiteCursor(masterQuery, editTable, query)
        }, supportQuery.sql, EMPTY_STRING_ARRAY, null)
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun query(
        supportQuery: SupportSQLiteQuery,
        cancellationSignal: CancellationSignal
    ): Cursor {
        return mDelegate.rawQueryWithFactory({ db, masterQuery, editTable, query ->
            supportQuery.bindTo(LabSQLiteProgram(query))
            SQLiteCursor(masterQuery, editTable, query)
        }, supportQuery.sql, EMPTY_STRING_ARRAY, null, cancellationSignal)
    }

    @Throws(SQLException::class)
    override fun insert(table: String, conflictAlgorithm: Int, values: ContentValues): Long {
        return mDelegate.insertWithOnConflict(
            table, null, values,
            conflictAlgorithm
        )
    }

    override fun delete(table: String, whereClause: String, whereArgs: Array<Any>): Int {
        val query = ("DELETE FROM " + table
                + if (isEmpty(whereClause)) "" else " WHERE $whereClause")
        val statement = compileStatement(query)
        SimpleSQLiteQuery.bind(statement, whereArgs)
        return statement.executeUpdateDelete()
    }


    override fun update(
        table: String, conflictAlgorithm: Int, values: ContentValues?, whereClause: String,
        whereArgs: Array<Any>?
    ): Int {
        // taken from SQLiteDatabase class.
        if (values == null || values.size() == 0) {
            throw IllegalArgumentException("Empty values")
        }
        val sql = StringBuilder(120)
        sql.append("UPDATE ")
        sql.append(CONFLICT_VALUES[conflictAlgorithm])
        sql.append(table)
        sql.append(" SET ")

        // move all bind args to one array
        val setValuesSize = values.size()
        val bindArgsSize = if (whereArgs == null) setValuesSize else setValuesSize + whereArgs.size
        val bindArgs = arrayOfNulls<Any>(bindArgsSize)
        var i = 0
        for (colName in values.keySet()) {
            sql.append(if (i > 0) "," else "")
            sql.append(colName)
            bindArgs[i++] = values.get(colName)
            sql.append("=?")
        }
        if (whereArgs != null) {
            i = setValuesSize
            while (i < bindArgsSize) {
                bindArgs[i] = whereArgs[i - setValuesSize]
                i++
            }
        }
        if (!isEmpty(whereClause)) {
            sql.append(" WHERE ")
            sql.append(whereClause)
        }
        val stmt = compileStatement(sql.toString())
        SimpleSQLiteQuery.bind(stmt, bindArgs)
        return stmt.executeUpdateDelete()
    }

    @Throws(SQLException::class)
    override fun execSQL(sql: String) {
        mDelegate.execSQL(sql)
    }

    @Throws(SQLException::class)
    override fun execSQL(sql: String, bindArgs: Array<Any>) {
        mDelegate.execSQL(sql, bindArgs)
    }

    override fun isReadOnly(): Boolean {
        return mDelegate.isReadOnly
    }

    override fun isOpen(): Boolean {
        return mDelegate.isOpen
    }

    override fun needUpgrade(newVersion: Int): Boolean {
        return mDelegate.needUpgrade(newVersion)
    }

    override fun getPath(): String {
        return mDelegate.path
    }

    override fun setLocale(locale: Locale) {
        mDelegate.setLocale(locale)
    }

    override fun setMaxSqlCacheSize(cacheSize: Int) {
        mDelegate.setMaxSqlCacheSize(cacheSize)
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun setForeignKeyConstraintsEnabled(enable: Boolean) {
        mDelegate.setForeignKeyConstraintsEnabled(enable)
    }

    override fun enableWriteAheadLogging(): Boolean {
        return mDelegate.enableWriteAheadLogging()
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun disableWriteAheadLogging() {
        mDelegate.disableWriteAheadLogging()
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun isWriteAheadLoggingEnabled(): Boolean {
        return mDelegate.isWriteAheadLoggingEnabled
    }

    override fun getAttachedDbs(): List<Pair<String, String>> {
        return mDelegate.attachedDbs
    }

    override fun isDatabaseIntegrityOk(): Boolean {
        return mDelegate.isDatabaseIntegrityOk
    }

    @Throws(IOException::class)
    override fun close() {
        mDelegate.close()
    }

    /**
     * Checks if this object delegates to the same given database reference.
     */
    fun isDelegate(sqLiteDatabase: SQLiteDatabase): Boolean {
        return mDelegate == sqLiteDatabase
    }
}