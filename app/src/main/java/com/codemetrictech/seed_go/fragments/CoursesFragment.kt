package com.codemetrictech.seed_go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codemetrictech.seed_go.R
import com.codemetrictech.seed_go.courses.ExpandableCourseCard
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_courses.view.*

class CoursesFragment: Fragment(){
    val TAG = "Courses"

    private lateinit var coursesRecyclerView : RecyclerView
    val courseGroupAdapter = GroupAdapter<ViewHolder>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.progress_bar.show()
        super.onViewCreated(view, savedInstanceState)
         coursesRecyclerView = view.findViewById(R.id.recyclerView_fragment_courses)

        coursesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courseGroupAdapter
        }
    }

    fun addCourse(expandableCourseCard: ExpandableCourseCard){
        courseGroupAdapter.add(ExpandableGroup(expandableCourseCard))
        view?.progress_bar?.hide()
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