package ppo.tabata.utility

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ppo.tabata.data.TabataEntity
import ppo.tabata.databinding.RecyclerviewItemBinding
import ppo.tabata.viewModels.EditTabataViewModel


class TabataListAdapter(private var tabataList: List<TabataEntity>?,
                        private val onPlayClickListener: (TabataEntity) -> Unit,
                        private val onDeleteClickListener: (TabataEntity) -> Unit,
                        private val onEditClickListener: (TabataEntity) -> Unit)
    : ListAdapter<TabataEntity, TabataListAdapter.TabataViewHolder>(TabataComparator()) {

    companion object {lateinit var binding: RecyclerviewItemBinding}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabataViewHolder {
        binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context))
        return TabataViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: TabataViewHolder, position: Int) {
        tabataList?.get(position)?.let { holder.bind(it, onPlayClickListener) }
    }

    override fun submitList(list: List<TabataEntity>?) {
        super.submitList(list)
        if (!list.isNullOrEmpty()) { tabataList = list }
    }

    inner class TabataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(tabata: TabataEntity, clickListener: (TabataEntity) -> Unit) {
            binding.textView.text = tabata.name
            binding.work.text = EditTabataViewModel.getTime(tabata.work)
            binding.rest.text = EditTabataViewModel.getTime(tabata.rest)
            binding.reps.text = tabata.repeats.toString()
            binding.cycles.text = tabata.cycles.toString()
            binding.itemColor.setBackgroundColor(Color.parseColor(tabata.color))
            itemView.setOnClickListener { clickListener(tabata) }
            binding.playButton.setOnClickListener {
                tabataList?.get(adapterPosition)?.let { it1 -> onEditClickListener(it1) }
            }
            binding.deleteButton.setOnClickListener{
                tabataList?.get(adapterPosition)?.let { it1 -> onDeleteClickListener(it1) }
            }
        }
    }

    class TabataComparator : DiffUtil.ItemCallback<TabataEntity>() {
        override fun areItemsTheSame(oldItem: TabataEntity, newItem: TabataEntity): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: TabataEntity, newItem: TabataEntity): Boolean =
            (oldItem.name == newItem.name && oldItem.color == newItem.color)
    }
}
