package app.aoyama.huit.original

import androidx.room.*

@Dao
interface ProjectDao {
    // データを追加
    @Insert
    fun insert(project: Project)

    // データを更新
    @Update
    fun update(project: Project)

    // データを削除
    @Delete
    fun delete(project: Project)

    // 全てのデータを取得
    @Query("select * from projects")
    fun getAll(): List<Project>

    // 全てのデータを削除
    @Query("delete from projects")
    fun deleteAll()

//    @Query("select name from projects")
//    fun getNameList(): List<Project>

}