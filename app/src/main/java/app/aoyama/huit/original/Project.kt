package app.aoyama.huit.original

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project (

    @PrimaryKey(autoGenerate = true)
    val pid: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "due")
    var due: String,

        )