package com.codemetrictech.seed_go.courses

import android.os.AsyncTask
import android.util.Log
import com.codemetrictech.seed_go.fragments.CoursesFragment
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

class CourseContentGrabber(val hostFragment: CoursesFragment) : AsyncTask<Void, Void, Void>(){

    private val TAG = "Course Content Grabber"

    private var courseContentURL = "http://seed.gist-edu.cn/my/"
    private val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127"
    private var url_login = "http://seed.gist-edu.cn/login/index.php"
    private var username = "UWI180908"
    private var password = "A4034445@G!C"

    private var cookies = HashMap<String, String>()
    private var credentials = HashMap<String, String>()

    private var coursesList = ArrayList<CourseCard>()


    init {
        Log.d(TAG, "Getting seed content...")
        this.execute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val loginForm = Jsoup
                .connect(url_login)
                .method(Connection.Method.GET)
                .userAgent(USER_AGENT)
                .execute()


        cookies.putAll(loginForm.cookies())
        credentials["username"] = username
        credentials["password"] = password

        val homepage = Jsoup
                .connect(url_login)
                .cookies(cookies)
                .data(credentials)
                .method(Connection.Method.POST)
                .userAgent(USER_AGENT)
                .execute()

        cookies.clear()
        cookies.putAll(homepage.cookies())


        // Connect to the website
        val courseElement = Jsoup
                .connect(courseContentURL)
                .cookies(cookies)
                .get()

//        println("ORIGIN: " + courseElement.select("div[class=course_title]"))
        val numOfCourses = courseElement.select("div[class=course_title]").size

        //fetch course titles and container URL.
        for (i in 0 until numOfCourses){
            val filesMap = HashMap<String, String>()
            val assignmentsMap = HashMap<String, String>()

            val courseInfoElement = courseElement.select("div[class=course_title]")
                    .select("h2[class=title]")
                    .select("a")[i]

            val courseInfo = courseInfoElement.attributes()

            val unformattedTitle = courseInfo.get("title")
            val formattedTitle = formatTitle(unformattedTitle)

            println("TITLE: $formattedTitle")

            val containerURL = courseInfo.get("href")

            val courseContainer = Jsoup
                    .connect(containerURL)
                    .cookies(cookies)
                    .get()

            val numOfTopics = courseContainer.select("h3[class=sectionname]").size

//            var hrefQuery = """a[class=""]"""
            val containerElement = courseContainer
                    .select("div[class=activityinstance]")
                    .select("""a[class=""]""")
//                    .select("span[class=instancename]")

            containerElement.forEach {
                val elementIdentity = it.text()

                if (elementIdentity.contains("File")){
                    var fileName = elementIdentity.replace("File", "")
                    var fileURL = it.attributes()["href"]
                    filesMap[fileName] = fileURL

                } else if(elementIdentity.contains("Assignment")){
                    if(elementIdentity.contains("exercise", ignoreCase = true)){
                        val assignmentName = "Weekly Exercise"
                        assignmentsMap[assignmentName] = it.attributes()["href"]
                        val test = it.attributes()["href"]
                        println("REF IS: $test")
                    }
                }
            }
            coursesList.add(CourseCard(formattedTitle, assignmentsMap, filesMap, numOfTopics))
        }


        Log.d(TAG, "$numOfCourses Courses found.")

        return null

    }

    private fun formatTitle(unformattedTitle: CharSequence): String{
        val prefixStart = 0
        val prefixEnd = 8

        var prefix = unformattedTitle
                .subSequence(prefixStart until prefixEnd)
                .trim()
        val postfix: CharSequence

        //Check for & remove white-spaces in prefix and adjust bounds accordingly
        if (prefix.contains("\\s".toRegex())){
            prefix = unformattedTitle.subSequence(prefixStart..prefixEnd).replace("\\s".toRegex(), "")
            postfix = unformattedTitle.subSequence(prefixEnd+1 until unformattedTitle.length)
        } else {
            postfix = unformattedTitle.subSequence(prefixEnd until unformattedTitle.length)
        }

        return StringBuilder(String())
                .append(prefix.trim())
                .append(": ")
                .append(postfix.trim())
                .toString() }


    override fun onCancelled(result: Void?) {
        Log.d(TAG, "onCancelled: $result")
        super.onCancelled(result)
    }

    override fun onPostExecute(result: Void?) {
        coursesList.forEach { hostFragment.addCourse(it) }
        super.onPostExecute(result)
    }
}