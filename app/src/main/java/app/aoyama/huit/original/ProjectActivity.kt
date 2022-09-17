package app.aoyama.huit.original

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import app.aoyama.huit.original.databinding.ActivityProjectBinding

class ProjectActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityProjectBinding

    //Databaseの変数を用意
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //データベースの初期化
        db = AppDatabase.getInstance(this.applicationContext)!!

        //toolbarの設定をする
        binding.toolBar.setTitle("Projects")
        setSupportActionBar(binding.toolBar)
        //toolbarに戻るボタンをセットする
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        saveButtonが押された時の処理を書く
        binding.saveButton.setOnClickListener {
            val projectName = binding.projectNameEditText.text.toString()
            val dueDate = binding.dueDateText.text.toString()

            if (projectName == ""){
                Toast.makeText(applicationContext,"入力されていません",Toast.LENGTH_SHORT).show()
            }else{
                //プロジェクトの名前と期限をデータベースに保存する
                val project:Project = Project(
                    name = projectName,
                    due = dueDate,
                )
                db.projectDao().insert(project)

            }
        }

        //MainActivityに遷移する準備をする
        val homeIntent: Intent = Intent(this, MainActivity::class.java)

        //戻るボタンが押された時の処理を書く
        val callback = this.onBackPressedDispatcher.addCallback(this) {
            startActivity(homeIntent)
        }
    }

    //DatePickerの処理を書く
    override fun onDateSet(p0: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dueDate: String = getString(R.string.string_format, year, monthOfYear + 1, dayOfMonth)
        binding.dueDateText.text = dueDate
    }

    //datePickIconが押された時に呼び出される関数を作る
    fun showDatePickerDialog(v: View) {
        val newFragment = DatePick()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    //menuにあるproject用のtoolbarをinflateする
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolBar.inflateMenu(R.menu.projects_top_app_bar)
        return true
    }

    //toolbarのクリック処理を書く
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //戻るボタンが押されたときの処理を書く
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            println("あいうえお")
        } else {
            //ゴミ箱ボタンが押された時の処理を書く
            println("やったぜ")
        }
        return true
    }

}