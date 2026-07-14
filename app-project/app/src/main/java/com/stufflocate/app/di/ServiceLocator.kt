package com.stufflocate.app.di

import android.content.Context
import com.stufflocate.app.data.DataRepository
import com.stufflocate.app.data.DefaultDataRepository
import com.stufflocate.app.data.local.AppDatabase
import com.stufflocate.app.data.local.DatabaseSeeder
import com.stufflocate.app.theme.AppThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ServiceLocator {
  private var repository: DataRepository? = null
  private var database: AppDatabase? = null
  private var appThemeManager: AppThemeManager? = null

  fun init(context: Context) {
    if (database == null) {
      database = AppDatabase.getDatabase(context)
    }
    if (repository == null) {
      repository = DefaultDataRepository(database!!.homeDao())
    }
    if (appThemeManager == null) {
      appThemeManager = AppThemeManager(context.applicationContext)
    }

    CoroutineScope(Dispatchers.IO).launch {
      DatabaseSeeder(database!!.homeDao()).seed()
    }
  }

  fun getRepository(): DataRepository {
    return repository ?: throw IllegalStateException("ServiceLocator not initialized. Call init(context) first.")
  }

  fun getAppThemeManager(): AppThemeManager {
    return appThemeManager ?: throw IllegalStateException("ServiceLocator not initialized. Call init(context) first.")
  }
}
