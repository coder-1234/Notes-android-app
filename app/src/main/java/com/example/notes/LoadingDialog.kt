package com.example.notes

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.Glide


class LoadingDialog(private val activity: Activity) {
    private lateinit var dialog:Dialog

    fun showDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.activity_loading, null)
        builder.setView(view)
        val imageView = view.findViewById<ImageView>(R.id.loading_img)
        builder.setCancelable(false)

        dialog = builder.create()
        Log.d("hello",imageView.toString())
        Glide.with(activity)
            .load(R.drawable.loading)
            .into(imageView)
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}