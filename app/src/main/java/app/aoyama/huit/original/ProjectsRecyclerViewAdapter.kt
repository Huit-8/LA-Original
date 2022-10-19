package app.aoyama.huit.original

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.aoyama.huit.original.databinding.ItemProjectsCellBinding


class ProjectsRecyclerViewAdapter : RecyclerView.Adapter<ProjectListViewHolder>() {

    private val projectList: MutableList<Project> = mutableListOf()

    //リスナーを格納する変数を定義
    private lateinit var listener: OnProjectCellClickListener

    //インターフェースを作成
    interface OnProjectCellClickListener{
        fun onItemClick(project: Project)
    }

    //リスナーをセット
    fun setOnProjectCellClickListener(listener: OnProjectCellClickListener){
        this.listener = listener
    }

    //viewHolderの作成
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListViewHolder {
        val binding =
            ItemProjectsCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProjectListViewHolder(binding)
    }

    //ViewHolderの設定
    override fun onBindViewHolder(holder: ProjectListViewHolder, position: Int) {
        val project = projectList[position]
        holder.binding.itemTextView.text = project.name

        //セルのクリックイベントにリスナをセット
        holder.itemView.setOnClickListener {
            //セルがクリックされたときにインターフェースの処理が実行される
            listener.onItemClick(project)
        }
    }

    //ViewHolderの数の決定
    override fun getItemCount(): Int = projectList.size

    fun updateProject(newList: List<Project>){
        projectList.clear()
        projectList.addAll(newList)
        notifyDataSetChanged()
    }


}