package com.codemetrictech.seed_go.courses

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import com.codemetrictech.seed_go.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.layout_fileobj.view.*
import java.io.File
import java.util.*

class FileObj(val context: Context,
              val credentialsHashMap: HashMap<String, String>,
              val fileName:String,
              val downloadURl: String)
    :Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_fileobj
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val itemView = viewHolder.itemView

        itemView.textView_fileName.text = fileName
        itemView.imageView_downloadFile.setOnClickListener {
            downloadFile()
        }
    }

    private fun downloadFile(){
        if (hasStoragePermission()) {
            val url = downloadURl

            val request = DownloadManager.Request(Uri.parse(url))

            // Manually adding the HTTP Authorization header
            request.addRequestHeader("Authorization", generateAuthValue())

            // Location i.e. where to download file in external directory
            val file = File(Environment.DIRECTORY_DOWNLOADS + "/" + "SEED-GO")
            if (!file.exists()) file.mkdirs()

            // This puts the download in the project directory
            request.setDestinationInExternalPublicDir(file.toString(), fileName)

            // Notify user when download is completed
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Start download
            val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        }
    }

    private fun hasStoragePermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            false
        }
    }

    private fun generateAuthValue(): String {
        /* https://en.wikipedia.org/wiki/Basic_access_authentication
         * basic access authentication is a method for an HTTP user agent (e.g. a web browser)
         * to provide a user name and password when making a request.
         * In basic HTTP authentication, a request contains a header field of the form
         * Authorization: Basic <credentials>, where credentials is the base64 encoding of
         * id and password joined by a single colon (:).
         */
        val username = credentialsHashMap["username"]
        val password = credentialsHashMap["password"]

        val credentials = String(StringBuilder().append(username).append(":").append(password))
        val encodedCredentials = String(Base64.getEncoder().encode(credentials.toByteArray()))

        return String(StringBuilder().append("Basic ").append(encodedCredentials))
    }


}