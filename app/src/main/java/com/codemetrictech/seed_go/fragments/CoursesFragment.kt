package com.codemetrictech.seed_go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.codemetrictech.seed_go.R
import com.codemetrictech.seed_go.courses.ExpandableCourseCard
import com.google.gson.Gson
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_courses.view.*

class CoursesFragment: Fragment(){
    val TAG = "Courses"

    lateinit var coursesRecyclerView : RecyclerView
    val courseGroupAdapter = GroupAdapter<ViewHolder>()
    val coursesArrayList: ArrayList<ExpandableCourseCard> = ArrayList<ExpandableCourseCard>()


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
        coursesArrayList.add(expandableCourseCard)
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

    fun coursesServer(): Unit {
        //send lists of announcements to server
        //convert lists into JSON string
        val gson = Gson()

        val dataArrayCourses = gson.toJson(coursesArrayList)

        //send arrays to server using volley
        val server_url = "http://127.0.0.1:5000"

        val stringRequest = object : StringRequest(Request.Method.POST, server_url,
                Response.Listener { response ->
                    val result = response.toString()
                    println("response: $result")
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    error.message
                }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val param = HashMap<String, String>()
                param["courses"] = dataArrayCourses //courses is key for server side

                return param
            }
        }
        //VolleyConnection.getInstance(context).addRequestQue(stringRequest)

    }

}