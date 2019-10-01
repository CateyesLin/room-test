package info.in_alley.lab.db

import android.database.sqlite.SQLiteStatement
import android.util.Log
import androidx.sqlite.db.SupportSQLiteStatement

/**
 * Creates a wrapper around a framework [SQLiteStatement].
 *
 * @param delegate The SQLiteStatement to delegate calls to.
 */
internal class LabSQLiteStatement(private val mDelegate: SQLiteStatement) : LabSQLiteProgram(mDelegate), SupportSQLiteStatement {

    override fun execute() {
        Log.d("Lab", mDelegate.toString())
        mDelegate.execute()
    }

    override fun executeUpdateDelete(): Int {
        Log.d("Lab", mDelegate.toString())
        return mDelegate.executeUpdateDelete()
    }

    override fun executeInsert(): Long {
        Log.d("Lab", mDelegate.toString())
        return mDelegate.executeInsert()
    }

    override fun simpleQueryForLong(): Long {
        Log.d("Lab", mDelegate.toString())
        return mDelegate.simpleQueryForLong()
    }

    override fun simpleQueryForString(): String {
        Log.d("Lab", mDelegate.toString())
        return mDelegate.simpleQueryForString()
    }
}