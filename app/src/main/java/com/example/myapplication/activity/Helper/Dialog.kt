package com.example.myapplication.activity.Helper

import com.example.myapplication.activity.MainActivity

class Dialog {
    fun showDialog(mainActivity: MainActivity, title: String, msg: String) {
        val alertDialog = android.app.AlertDialog.Builder(mainActivity)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.show()
    }
}