package com.codemetrictech.seed_go.courses

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.codemetrictech.seed_go.fragments.CoursesFragment
import com.xwray.groupie.ExpandableGroup
import org.jsoup.Connection
import org.jsoup.Jsoup

class CourseContentGrabber(val context: Context,
                           val hostFragment: CoursesFragment) : AsyncTask<Void, Bundle, Void>(){

    private val TAG = "Course Content Grabber"
    private var courseContentURL = "http://seed.gist-edu.cn/my/"
    private val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127"
    private var url_login = "http://seed.gist-edu.cn/login/index.php"
    private var username = "UWI180908"
    private var password = "A4034445@G!C"

    private var cookies = HashMap<String, String>()
    private var credentials = HashMap<String, String>()

    private var coursesList = ArrayList<ExpandableCourseCard>()


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
            val progressBundle = Bundle()
            val filesMap = HashMap<String, String>()
            val assignmentsMap = HashMap<String, String>()

            val courseInfoElement = courseElement.select("div[class=course_title]")
                    .select("h2[class=title]")
                    .select("a")[i]

            val courseInfo = courseInfoElement.attributes()

            val unformattedTitle = courseInfo.get("title")
            val formattedTitle = formatTitle(unformattedTitle)
            Log.d(TAG, "Course found: $formattedTitle")

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
                var elementURL = String()

                if(it.hasAttr("href")){
                    elementURL = it.attributes()["href"]
                }

                if (elementIdentity.contains("File")){
                    val fileName = elementIdentity.replace("File", "")
                    val fileURL = elementURL
                    filesMap[fileName] = fileURL

                } else if(elementIdentity.contains("Assignment", true)){
                    println("ELEMENT: $elementIdentity")
                    if(elementIdentity.contains("exercise", true)){
                        val assignmentName = "Weekly Exercise"
                        assignmentsMap[assignmentName] = it.attributes()["href"]
                        val test = it.attributes()["href"]
                        println("REF IS: $test")
                    }
                }
            }
            progressBundle.putString("courseTitle", formattedTitle)
            progressBundle.putInt("numOfTopics", numOfTopics)
            progressBundle.putSerializable("filesMap", filesMap)
            progressBundle.putSerializable("assignmentsMap", assignmentsMap)

            publishProgress(progressBundle)
            //coursesList.add(ExpandableCourseCard(formattedTitle, assignmentsMap, filesMap, numOfTopics))
        }

        Log.d(TAG, "$numOfCourses Courses found.")

        return null
    }

    override fun onProgressUpdate(vararg values: Bundle?) {
        val resultBundle = values.getOrNull(0)!!

        val courseTitle = resultBundle.getString("courseTitle")!!
        val assignmentMap = resultBundle.getSerializable("assignmentsMap") as HashMap<String, String>
        val filesMap = resultBundle.getSerializable("filesMap") as HashMap<String, String>
        val numOfTopics = resultBundle.getInt("numberOfTopics")

        val expandableDataSection = ExpandableDataSection("Week 1", filesMap, assignmentMap)
        filesMap.entries.forEach {
            println("IT IS: $it")
            println("key: ${it.key}\nvalue: ${it.value}\nContext is null?: $context")
            val fileObj = FileObj(context!!, credentials, it.key, it.value)
            expandableDataSection.getSection().add(fileObj)
        }

        val courseCard = ExpandableCourseCard(courseTitle, filesMap.size, assignmentMap.size, numOfTopics)
        courseCard.getSection().add(ExpandableGroup(expandableDataSection))

        hostFragment.addCourse(courseCard)

        Log.d(TAG, "onProgressUpdate()")

        super.onProgressUpdate(*values)
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
        //coursesList.forEach { hostFragment.addCourse(it) }
        super.onPostExecute(result)
    }


}