package com.codemetrictech.seed_go.courses

import com.codemetrictech.seed_go.R
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.section_expandable_data.view.*

class ExpandableDataSection(val week:String,
                            val filesHashMap: HashMap<String, String>,
                            val assignmentsHashMap: HashMap<String, String>)
    : Item<ViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup
    private val section = Section()


    override fun getLayout(): Int {
        return R.layout.section_expandable_data
    }

    fun getSection(): Section = this.section

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
        this.expandableGroup.add(section)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        itemView.expandableCardView_topic.setOnClickListener {
            expandableGroup.onToggleExpanded() }

//        itemView.imageView_expandWeek.setOnClickListener {
//            expandableGroup.onToggleExpanded()
//            it.scaleX = -1f
//        }
    }
}

