package com.sarvam.jarvis

import android.app.Application
import com.sarvam.jarvis.data.local.JarvisDatabase
import com.sarvam.jarvis.domain.JarvisBrain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class JarvisApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { JarvisDatabase.getDatabase(this, applicationScope) }
    val brain by lazy { JarvisBrain(this, database) }
}
