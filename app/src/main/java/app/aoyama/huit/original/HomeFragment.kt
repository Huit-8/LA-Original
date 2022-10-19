package app.aoyama.huit.original

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.NavHostFragment
import app.aoyama.huit.original.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    lateinit var db: AppDatabase

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        db = AppDatabase.getInstance(requireContext())!!

        val projectList = db.projectDao().getAll()

        val adapter = ProjectsRecyclerViewAdapter()

        binding.homeRecyclerView.adapter = adapter
        adapter.updateProject(projectList)

        //ProjectActivityへ遷移する準備をする
        val projectIntent = Intent(activity, ProjectActivity::class.java)

        //itemかFABのどちらが押されたかを判定する変数を作る
        var tapButton = 0

        //SharedPrefにActivityTimeのデータがあるか確認する
        val pref: SharedPreferences? =
            activity?.getSharedPreferences("SharedPref", Context.MODE_PRIVATE)
        val activeTime1 = pref?.getString("hour1", "NoData")
        val activeTime2 = pref?.getString("activeTime2", "NoData")
        if (activeTime1 == "NoData") {
            binding.createProjectButton.setOnClickListener {
                Toast.makeText(activity, "設定からActiveTimeを決めてください", Toast.LENGTH_LONG).show()
            }
        } else {
            binding.createProjectButton.setOnClickListener {
                tapButton = 2
                projectIntent.putExtra("tap", tapButton)
                println(tapButton)
                startActivity(projectIntent)
            }
        }


        //recyclerViewのアイテムが押されたときの処理
        adapter.setOnProjectCellClickListener(
            object : ProjectsRecyclerViewAdapter.OnProjectCellClickListener {
                override fun onItemClick(project: Project) {
                    println("ええ感じやん")

                    //recyclerViewのアイテムが押されたときは"1"とする
                    tapButton = 1

                    //プロジェクトのデータを渡す処理
                    projectIntent.putExtra("tap", tapButton)
                    val projectName = project.name
                    val projectDueDate = project.due
                    //TODO remainingTimeを定義する
                    val projectRemain = project.remain
                    val projectId = project.pid
                    projectIntent.putExtra("name", projectName)
                    projectIntent.putExtra("dueDate", projectDueDate)
                    projectIntent.putExtra("remainingTime", projectRemain)
                    //TODO　remainingTimeを渡す処理を書く
                    projectIntent.putExtra("id", projectId)
                    println("$projectName + $projectDueDate + $projectId + $tapButton")
                    startActivity(projectIntent)

                }
            }
        )

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
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.back)
        item.isVisible = false
    }

    override fun onResume() {
        super.onResume()

    }
}