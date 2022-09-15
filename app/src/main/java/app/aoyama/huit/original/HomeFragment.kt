package app.aoyama.huit.original

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import app.aoyama.huit.original.databinding.FragmentHomeBinding

 class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Toolbarにタイトルをセットする
        activity?.setTitle("All Projects")

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        //ProjectActivityへ遷移する準備をする
        val projectIntent = Intent(activity,ProjectActivity::class.java)

        //FloatingActionButtonが押された時にProjectActivityへ遷移する
        binding.createProjectButton.setOnClickListener {
            startActivity(projectIntent)
        }

        return binding.root
    }

     //home用のtoolbarレイアウトファイルをinflateする
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_top_app_bar, menu)
    }

     //toolbarのクリック処理を書く
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // SettingFragmentに遷移する
        if (item.itemId == R.id.setting) {
            println("さしすせそ")
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val action = HomeFragmentDirections.actionHomeFragmentToSettingFragment()
            navController.navigate(action)
        }
        return true
    }

     //toolbarからHomeに戻るテキストを消す
     override fun onPrepareOptionsMenu(menu: Menu){
         super.onPrepareOptionsMenu(menu)
         val item = menu.findItem(R.id.back)
         item.isVisible = false
     }
}