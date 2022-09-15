package app.aoyama.huit.original

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import app.aoyama.huit.original.databinding.ActivityProjectBinding

class ProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toolbarの設定をする
        binding.toolBar.setTitle("Projects")
        setSupportActionBar(binding.toolBar)
        //toolbarに戻るボタンをセットする
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //menuにあるproject用のtoolbarをinflateする
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolBar.inflateMenu(R.menu.projects_top_app_bar)
        return true
    }

    //toolbarのクリック処理を書く
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            //戻るボタンが押されたときの処理を書く
            val mainIntent = Intent(this,MainActivity::class.java)
            startActivity(mainIntent)
            println("あいうえお")
        }else{
            //ゴミ箱ボタンが押された時の処理を書く
           println("やったぜ")
        }
        return true
    }

}