package com.codemetrictech.seed_go.courses

import android.content.res.Resources
import android.util.Log
import android.view.View
import com.codemetrictech.seed_go.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.card_course.view.*


class CourseCard(val courseTitle:String = "Title",
                 val assignmentHashMap: HashMap<String, String> = HashMap(),
                 val filesHashMap: HashMap<String, String> = HashMap(),
                 val numOfTopics: Int = 0,
                 var num_of_files:Int = filesHashMap.size,
                 var num_of_assignemnts:Int = assignmentHashMap.size)
    : Item<ViewHolder>()
{
    val TAG = "courseCard"

    override fun notifyChanged() {
        super.notifyChanged()
    }

    override fun getLayout(): Int {
        return R.layout.card_course
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val res = Resources.getSystem()

        val itemView = viewHolder.itemView

        itemView.textview_course_title.text = courseTitle
        itemView.textview_course_files.text = "$num_of_files NEW FILES"
        itemView.textview_course_assignments.text = "$num_of_assignemnts NEW ASSIGNMENTS"

//        itemView.textview_course_files.text = res
//                .getString(R.string.getString_numOfFiles, num_of_files)
//
//        itemView.textview_course_assignments.text = res
//                .getString(R.string.getString_numOfAssignments, num_of_assignemnts)
    }


    fun initClickerListeners(itemView:View){
        itemView.button_view_course
                .setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "View Course Onclick()")
                })
    }

}
