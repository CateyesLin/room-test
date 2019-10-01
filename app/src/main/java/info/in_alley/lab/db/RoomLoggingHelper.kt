package info.in_alley.lab.db

import android.annotation.SuppressLint
import androidx.room.RoomSQLiteQuery

private const val NULL = 1
private const val LONG = 2
private const val DOUBLE = 3
private const val STRING = 4
private const val BLOB = 5
private const val NULL_QUERY = "NULL"

const val ROOM_LOGGING_TAG = "roomQueryLog"

object RoomLoggingHelper {

    @SuppressLint("RestrictedApi")
    fun getStringSql(query: RoomSQLiteQuery): String {
        val argList = arrayListOf<String>()
        val bindingTypes = query.getBindingTypes()
        var i = 0

        while (i < bindingTypes.size) {
            val bindingType = bindingTypes[i]

            when (bindingType) {
                NULL -> argList.add(NULL_QUERY)
                LONG -> argList.add(query.getLongBindings()[i].toString())
                DOUBLE -> argList.add(query.getDoubleBindings()[i].toString())
                STRING -> argList.add(query.getStringBindings()[i].toString())
            }
            i++
        }

        return String.format(query.sql.replace("?", "%s"), *argList.toArray())
    }

    fun getStringSql(query: String?, args: Array<out Any>?): String? {
        return if (query != null && args != null) {
            String.format(query.replace("?", "%s"), *args)
        } else
            ""
    }
}

private fun RoomSQLiteQuery.getBindingTypes(): IntArray {

    return javaClass.getDeclaredField("mBindingTypes").let { field ->
        field.isAccessible = true
        return@let field.get(this) as IntArray
    }
}

private fun RoomSQLiteQuery.getLongBindings(): LongArray {

    return javaClass.getDeclaredField("mLongBindings").let { field ->
        field.isAccessible = true
        return@let field.get(this) as LongArray
    }
}

private fun RoomSQLiteQuery.getStringBindings(): Array<String> {

    return javaClass.getDeclaredField("mStringBindings").let { field ->
        field.isAccessible = true
        return@let field.get(this) as Array<String>
    }
}

private fun RoomSQLiteQuery.getDoubleBindings(): DoubleArray {

    return javaClass.getDeclaredField("mDoubleBindings").let { field ->
        field.isAccessible = true
        return@let field.get(this) as DoubleArray
    }
}

private fun RoomSQLiteQuery.getIntBindings(): IntArray {

    return javaClass.getDeclaredField("mBindingTypes").let { field ->
        field.isAccessible = true
        return@let field.get(this) as IntArray
    }
}