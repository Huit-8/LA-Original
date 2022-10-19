package app.aoyama.huit.original

import android.content.Context
import android.content.SharedPreferences
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

        //SharedPrefの変数を作る
        val pref: SharedPreferences? = activity?.getSharedPreferences("SharedPref",Context.MODE_PRIVATE)

        val activeHour1 = pref?.getString("hour1","NoData")
        val activeMinute1 = pref?.getString("minute1","NoData")
        val activeHour2 = pref?.getString("hour2","NoData")
        val activeMinute2 = pref?.getString("minute2","NoData")

        //ActiveTimeが設定されている場合はその時間を表示する
        if (activeMinute1 != null){
            binding.hourText1.setText(activeHour1)
            binding.minuteText1.setText(activeMinute1)
            binding.hourText2.setText(activeHour2)
            binding.minuteText2.setText(activeMinute2)
        }

        binding.saveSettingButton.setOnClickListener {
            //ActiveTimeを保存する処理を書く
            val hour1 = binding.hourText1.text.toString()
            val minute1 = binding.minuteText1.text.toString()
            val hour2 = binding.hourText2.text.toString()
            val minute2 = binding.minuteText2.text.toString()


            val editor = pref?.edit()
            editor?.putString("hour1",hour1)
            editor?.putString("minute1",minute1)
            editor?.putString("hour2",hour2)
            editor?.putString("minute2",minute2)
            editor?.apply()
        }

        return binding.root
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // HomeFragmentに遷移する
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

    //toolbarから歯車アイコンを消す
    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.setting)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.back)
        item2.isVisible = true

    }




}