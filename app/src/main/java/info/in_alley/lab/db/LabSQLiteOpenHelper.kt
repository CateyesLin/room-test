package info.in_alley.lab.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

class LabSQLiteOpenHelper(context: Context, name: String, callback: SupportSQLiteOpenHelper.Callback) : SupportSQLiteOpenHelper {
    private val mDelegate: OpenHelper

    init {
        mDelegate = createDelegate(context, name, callback)
    }

    private fun createDelegate(context: Context, name: String, callback: SupportSQLiteOpenHelper.Callback): OpenHelper {
        val dbRef = arrayOfNulls<LabSQLiteDatabase>(1)
        return OpenHelper(context, name, dbRef, callback)
    }

    override fun getDatabaseName(): String {
        return mDelegate.databaseName
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        mDelegate.setWriteAheadLoggingEnabled(enabled)
    }

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        return mDelegate.writableSupportDatabase
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        return mDelegate.readableSupportDatabase
    }

    override fun close() {
        mDelegate.close()
    }

    internal class OpenHelper(
        context: Context, name: String,
        /**
         * This is used as an Object reference so that we can access the wrapped database inside
         * the constructor. SQLiteOpenHelper requires the error handler to be passed in the
         * constructor.
         */
        val dbRef: Array<LabSQLiteDatabase?>,
        val callback: SupportSQLiteOpenHelper.Callback
    ) :
        SQLiteOpenHelper(context, name, null, callback.version,
            DatabaseErrorHandler { dbObj -> callback.onCorruption(getWrappedDb(dbRef, dbObj)) }) {
        // see b/78359448
        private var mMigrated: Boolean = false

        // there might be a connection w/ stale structure, we should re-open.
        val writableSupportDatabase: SupportSQLiteDatabase
            @Synchronized get() {
                mMigrated = false
                val db = super.getWritableDatabase()
                if (mMigrated) {
                    close()
                    return writableSupportDatabase
                }
                return getWrappedDb(db)
            }

        // there might be a connection w/ stale structure, we should re-open.
        val readableSupportDatabase: SupportSQLiteDatabase
            @Synchronized get() {
                mMigrated = false
                val db = super.getReadableDatabase()
                if (mMigrated) {
                    close()
                    return readableSupportDatabase
                }
                return getWrappedDb(db)
            }

        fun getWrappedDb(sqLiteDatabase: SQLiteDatabase): LabSQLiteDatabase {
            return getWrappedDb(dbRef, sqLiteDatabase)
        }

        override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
            callback.onCreate(getWrappedDb(sqLiteDatabase))
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            mMigrated = true
            callback.onUpgrade(getWrappedDb(sqLiteDatabase), oldVersion, newVersion)
        }

        override fun onConfigure(db: SQLiteDatabase) {
            callback.onConfigure(getWrappedDb(db))
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            mMigrated = true
            callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion)
        }

        override fun onOpen(db: SQLiteDatabase) {
            if (!mMigrated) {
                // if we've migrated, we'll re-open the db so we should not call the callback.
                callback.onOpen(getWrappedDb(db))
            }
        }

        @Synchronized
        override fun close() {
            super.close()
            dbRef[0] = null
        }

        companion object {

            fun getWrappedDb(refHolder: Array<LabSQLiteDatabase?>, sqLiteDatabase: SQLiteDatabase): LabSQLiteDatabase {
                var dbRef = refHolder[0]
                if (dbRef == null || !dbRef.isDelegate(sqLiteDatabase)) {
                    dbRef = LabSQLiteDatabase(sqLiteDatabase)
                    refHolder[0] = dbRef
                }
                return dbRef
            }
        }
    }
}