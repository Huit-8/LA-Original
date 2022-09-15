package app.aoyama.huit.original

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.enableSavedStateHandles
import androidx.navigation.fragment.NavHostFragment
import app.aoyama.huit.original.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //toolbarにタイトルをセットする
        activity?.setTitle("Setting")

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_top_app_bar, menu)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater,container,false)





        return binding.root
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // SettingFragmentに遷移する
        if (item.itemId == R.id.back) {
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val action = SettingFragmentDirections.actionSettingFragmentToHomeFragment()
            navController.navigate(action)
        }
        return true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val action = SettingFragmentDirections.actionSettingFragmentToHomeFragment()
            navController.navigate(action)
        }
    }



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//
//        val myToolbar =
//
//
//        binding.myToolbar.setNavigationIcon(R.drawable.ic_back)
//
//        myToolbar.setNavigationOnClickListener { view ->
//            // Navigate somewhere
//        }
//    }

    //toolbarから歯車アイコンを消す
    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.setting)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.back)
        item2.isVisible = true

    }



}