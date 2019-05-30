package com.codemetrictech.seed_go.courses

import android.content.res.Resources
import android.util.Log
import android.view.View
import com.codemetrictech.seed_go.R
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.card_course.view.*


class ExpandableCourseCard(private val courseTitle:String = "Title",
                           private val numOfFiles:Int,
                           private val numOfAssignments:Int,
                           private val numOfTopics: Int = 0)
    : Item<ViewHolder>(), ExpandableItem
{

    private val TAG = "courseCard"

    private lateinit var expandableGroup: ExpandableGroup
    private val weekSection = Section()

    override fun notifyChanged() {
        super.notifyChanged()
        Log.d(TAG, "notifyChanged()")
    }

    override fun getLayout(): Int {
        return R.layout.card_course
    }

    fun getSection():Section = this.weekSection

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
       this.expandableGroup = onToggleListener
        this.expandableGroup.add(weekSection)
    }


    override fun bind(viewHolder: ViewHolder, position: Int) {
        val res = Resources.getSystem()
        val itemView = viewHolder.itemView

        itemView.textview_course_title.text = courseTitle
        itemView.textview_course_files.text = "$numOfFiles NEW FILES"
        itemView.textview_course_assignments.text = "$numOfAssignments NEW ASSIGNMENTS"

        initClickerListeners(itemView)

//        itemView.textview_course_files.text = res
//                .getString(R.string.getString_numOfFiles, numOfFiles)
//
//        itemView.textview_course_assignments.text = res
//                .getString(R.string.getString_numOfAssignments, numOfAssignments)
    }


    private fun initClickerListeners(itemView:View){
        itemView.textview_course_title
                .setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "View Course Onclick()")
                })
        itemView.textview_course_files.setOnClickListener {
            this.expandableGroup.onToggleExpanded()
        }
    }



}
