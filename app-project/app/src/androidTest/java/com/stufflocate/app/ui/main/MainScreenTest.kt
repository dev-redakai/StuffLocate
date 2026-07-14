package com.stufflocate.app.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.stufflocate.app.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun screenShowsAppTitle() {
    composeTestRule.setContent {
      MainScreen(
        onNavigate = {},
        onBack = {},
      )
    }
    // "Stuff Locate" appears in the top bar AND the drawer header
    composeTestRule.onAllNodesWithText("Stuff Locate").assertCountEquals(2)
  }

  @Test
  fun menuButton_exists() {
    composeTestRule.setContent {
      MainScreen(
        onNavigate = {},
        onBack = {},
      )
    }
    // Menu icon is always visible in the top bar regardless of state
    composeTestRule.onAllNodesWithContentDescription("Menu").assertCountEquals(1)
  }
}
