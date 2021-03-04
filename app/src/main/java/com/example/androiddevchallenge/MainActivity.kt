/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MainScreen()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MainScreen() {
    val constantTime = 600000L
    var time by remember { mutableStateOf("10:00") }
    var totalTime by remember { mutableStateOf(constantTime) }
    var remainingTime = 0L

    var progress by remember { mutableStateOf(1.00f) }
    var progressValue: Float

    var meditateTextState by remember { mutableStateOf("Ready to meditate?") }
    var ctr = 0

    val timer = object : CountDownTimer(totalTime, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTime = millisUntilFinished
            val minutes = millisUntilFinished / 1000 / 60
            val seconds = millisUntilFinished / 1000 % 60
            progressValue = millisUntilFinished.toFloat() / constantTime.toFloat()
            progress = (String.format("%1.3f", progressValue).toFloat())
            time = "%02d".format(minutes) + ":" + "%02d".format(seconds)

            if (ctr < meditateTextArray.size && seconds % 5 == 0L) {
                meditateTextState = meditateTextArray[ctr]
                ctr++
            }
            if (ctr == meditateTextArray.size) {
                ctr = 0
            }
        }
        override fun onFinish() {
            totalTime = constantTime
            ctr = 0
            time = "10:00"
            progress = 1.0f
            meditateTextState = "Ready to meditate?"
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            ReusableImage(resource = R.drawable.bg_img, desc = "Background Image")
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Crossfade(
                            targetState = meditateTextState,
                            animationSpec = tween(
                                durationMillis = 1000,
                                delayMillis = 1000,
                                easing = LinearEasing
                            )
                        ) {
                            ReusableHeaderText(
                                text = it,
                                color = MaterialTheme.colors.primaryVariant
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(250.dp),
                                progress = progress,
                                color = MaterialTheme.colors.primary,
                                strokeWidth = 10.dp
                            )
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                ReusableHeaderText(
                                    text = time,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ReusableImage(resource = R.drawable.ic_stop, desc = "Stop Icon") {
                            totalTime = constantTime
                            ctr = 0
                            time = "10:00"
                            progress = 1.0f
                            meditateTextState = "Ready to meditate?"
                            timer.cancel()
                        }
                        ReusableImage(resource = R.drawable.ic_play, desc = "Play Icon") {
                            meditateTextState = "Let's begin."
                            timer.start()
                        }
                        ReusableImage(resource = R.drawable.ic_pause, desc = "Pause Icon") {
                            totalTime = remainingTime
                            timer.cancel()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReusableImage(resource: Int, desc: String) {
    val paintImage: Painter = painterResource(id = resource)
    Image(painter = paintImage, contentDescription = desc)
}

@Composable
fun ReusableImage(resource: Int, desc: String, clickAction: () -> Unit) {
    val paintImage: Painter = painterResource(id = resource)
    Image(painter = paintImage, contentDescription = desc, modifier = Modifier.clickable(onClick = clickAction))
}

@Composable
fun ReusableHeaderText(text: String, color: Color) {
    Text(text = text, style = MaterialTheme.typography.h1, color = color)
}

val meditateTextArray = arrayListOf(
    "Breathe in.",
    "Breathe out.",
    "You are important.",
    "You are validated.",
    "Breathe in.",
    "Breathe out.",
    "You are loved.",
    "You are safe.",
    "Breathe in.",
    "Breathe out.",
    "Think of your favorite place.",
    "Feel the breeze on your skin.",
    "No one can hurt you here.",
    "Let go of any bad feelings.",
    "Relax and calm your self down",
    "You are capable of doing things."
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTheme {
        MainScreen()
    }
}
