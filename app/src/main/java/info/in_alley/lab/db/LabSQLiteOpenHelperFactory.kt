package info.in_alley.lab.db

import androidx.sqlite.db.SupportSQLiteOpenHelper

class LabSQLiteOpenHelperFactory : SupportSQLiteOpenHelper.Factory {
    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        return LabSQLiteOpenHelper(configuration.context, configuration.name!!, configuration.callback)
    }

}