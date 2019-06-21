package com.codemetrictech.seed_go.courses

import android.content.Context
import com.codemetrictech.seed_go.R
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.section_expandable_data.view.*

class ExpandableDataSection(val context: Context,
                            val credentials: HashMap<String, String>,
                            val topic:String,
                            val filesHashMap: HashMap<String, String>,
                            val assignmentsHashMap: HashMap<String, String>,
                            val numOfSections: Int)
    : Item<ViewHolder>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup
    private val section = Section()


    override fun getLayout(): Int {
        return R.layout.section_expandable_data
    }

    fun getSection(): Section = this.section
    fun getExpandableGroup(): ExpandableGroup = this.expandableGroup

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
        this.expandableGroup.add(section)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView
        itemView.textView_topic.text = topic
//        val sectionsList = ArrayList<Section>()
//        val fileObjList = ArrayList<FileObj>()

//        filesHashMap.entries.forEach {
//            fileObjList.add(FileObj(context, credentials, it.key, it.value))
//        }
//        for(i in 0 until numOfSections){
//            sectionsList.add(Section())
//        }
//
//        var index = 0
//
//        sectionsList.forEach {
//            if(index < fileObjList.size + 1){
//                it.add(fileObjList[index])
//                it.add(fileObjList[index + 1])
//                index += 2
//            }
//            this.expandableGroup.add(it)
//        }



        itemView.imageView_expandWeek.setOnClickListener {
            expandableGroup.onToggleExpanded() }

        itemView.imageView_expandWeek.setOnClickListener {
            expandableGroup.onToggleExpanded()
            itemView.imageView_expandWeek.scaleX = -1.0f
        }
    }
}

