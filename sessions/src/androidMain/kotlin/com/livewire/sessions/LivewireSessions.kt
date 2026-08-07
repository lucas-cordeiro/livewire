package com.livewire.sessions

import android.app.Activity
import android.content.Context
import android.content.Intent

object LivewireSessions {

  fun launch(context: Context) {
    val intent = Intent(context, SessionsActivity::class.java)
    if (context !is Activity) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
  }
}
