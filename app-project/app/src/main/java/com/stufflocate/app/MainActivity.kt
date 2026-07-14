package com.stufflocate.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.stufflocate.app.theme.StuffLocateTheme
import com.stufflocate.app.theme.ThemeManager

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    ThemeManager.init(this)
    enableEdgeToEdge()

    setContent {
      StuffLocateTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background,
        ) {
          MainNavigation()
        }
      }
    }
  }
}
