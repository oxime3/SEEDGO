package com.codemetrictech.seed_go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemetrictech.seed_go.R
import com.codemetrictech.seed_go.courses.CourseCard
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class CoursesFragment: Fragment(){
    val TAG = "Courses"

    private lateinit var coursesRecyclerView : RecyclerView
    val courseGroupAdapter = GroupAdapter<ViewHolder>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         coursesRecyclerView = view.findViewById(R.id.recyclerView_fragment_courses)

        coursesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courseGroupAdapter
        }

//        addCourse("SWEN3000: IOS Development")
//        addCourse("SWEN3004: Web and Mobile II")
//        addCourse("SWEN3156: Software Testing")
//        addCourse("SWEN3002: Android Development")

        //CourseContentGrabber()
    }

    fun addCourse(courseCard: CourseCard){
        courseGroupAdapter.add(courseCard)
    }

    companion object courseFragmentCompanion{
        private val fragmentTitle = "CourseFragment"

        fun newInstance():CoursesFragment{
            val args = Bundle()
            val fragment = CoursesFragment()
            fragment.arguments = args
            return fragment
        }

        fun getFragmentTitle():String{
            return fragmentTitle
        }
    }

}