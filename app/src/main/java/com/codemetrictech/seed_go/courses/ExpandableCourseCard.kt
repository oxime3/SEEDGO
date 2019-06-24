package com.codemetrictech.seed_go.courses

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import com.codemetrictech.seed_go.R
import com.codemetrictech.seed_go.fragments.CoursesFragment
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.card_course.view.*
import org.json.JSONObject


class ExpandableCourseCard(val hostFrag:CoursesFragment,
                           val context: Context,
                           val credentials: HashMap<String, String>,
                           val filesHashMap: HashMap<String, String>,
                           val assignmentsHashMap: HashMap<String, String>,
                           private val numOfTopics: Int ,
                           private val courseTitle:String = "Title",
                           private val numOfFiles:Int = filesHashMap.size,
                           private val numOfAssignments:Int = assignmentsHashMap.size
                           )
    : Item<ViewHolder>(), ExpandableItem
{

    private val TAG = "courseCard"

    private lateinit var expandableGroup: ExpandableGroup
    val expandableDataSectionList = ArrayList<ExpandableDataSection>()
    private val weekSection = Section()

    fun toJson(): JSONObject{
         var json = JSONObject()
        json.put("courseTitle", courseTitle)
        json.put("numOfTopics", numOfTopics)
        json.put("numOfFiles", numOfFiles)
        json.put("numOfAssignments", numOfAssignments)
        return json

    }

    override fun notifyChanged() {
        setUpCourse()
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
        hostFrag.coursesRecyclerView.post {
            setUpCourse()
        }
        Log.d(TAG, "bind()")

    }

    private fun setUpCourse(){
        Log.d(TAG, "setUpCourse()")
        val fileObjList = ArrayList<FileObj>()
        filesHashMap.entries.forEach {
            fileObjList.add(FileObj(context, credentials, it.key, it.value))
        }


        for(i in 1 .. numOfTopics){

            var topicString = "Topic $i"
            val expDataSection = ExpandableDataSection(context, credentials, topicString, filesHashMap, assignmentsHashMap, numOfTopics )
            expandableDataSectionList.add(expDataSection)
        }


        var k = 0
        expandableDataSectionList.forEach {
            if(k < fileObjList.size-1){
                it.getSection().add(fileObjList[k])
                it.getSection().add(fileObjList[k+1])
                it.notifyChanged()
                k+=2
            }
        }
        println("LIST: ${expandableDataSectionList.size}")
        expandableDataSectionList.forEach {
//            println("IT IS: $it")

            this.weekSection.add(ExpandableGroup(it))
            this.weekSection.notifyChanged()

            //notifyChanged()
        }

    }

    private fun initClickerListeners(itemView:View){
        itemView.textview_course_title
                .setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "View Course Onclick()")
                })
        itemView.textview_course_files.setOnClickListener {
            this.expandableGroup.onToggleExpanded()
            Log.d(TAG, "OnToggleExpanded()")
        }

        itemView.textview_course_assignments.setOnClickListener {
            this.expandableGroup.onToggleExpanded()
            println(this.expandableGroup.itemCount)
            println(this.weekSection.itemCount)
            println(this.weekSection.groupCount)
            Log.d(TAG, "OnToggleExpanded()")
        }
    }



}
