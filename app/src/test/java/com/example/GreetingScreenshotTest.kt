package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.FifaCard
import com.example.ui.components.FifaCardView
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleCard = FifaCard(
        playerName = "L. Messi",
        rating = 97,
        position = "CF",
        nationality = "Argentina",
        club = "miami",
        pace = 91,
        shooting = 96,
        passing = 98,
        dribbling = 99,
        defending = 45,
        physicality = 72,
        cardType = "Team of the Year"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        FifaCardView(card = sampleCard, scaleFactor = 1.0f)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
