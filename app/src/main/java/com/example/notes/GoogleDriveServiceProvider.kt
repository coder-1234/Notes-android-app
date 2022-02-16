package com.example.notes

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

class GoogleDriveServiceProvider(private val context: Context) {
    fun getService():Drive{
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_APPDATA))
            .setSelectedAccount(googleSignInAccount!!.account)

        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential)
            .setApplicationName(R.string.app_name.toString())
            .build()
    }
}