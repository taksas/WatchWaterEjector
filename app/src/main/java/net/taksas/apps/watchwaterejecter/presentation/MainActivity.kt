/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package net.taksas.apps.watchwaterejecter.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedButton
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.protolayout.LayoutElementBuilders
import kotlinx.coroutines.launch



import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.theme.WatchWaterEjecterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp("Android")
        }
    }
}



@Composable
fun WearApp(greetingName: String) {
    WatchWaterEjecterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {
    var defaultModifierPadding = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState
            )
        },
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        timeText = {
            TimeText()
        }
    ) {

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .padding(top = 0.dp, bottom = 0.dp)
                .fillMaxWidth()
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                        listState.animateScrollBy(0f)
                    }
                    true // it means that we are handling the event with this callback
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            item {
                Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.app_name),
                )
            }

            item {
                val context = LocalContext.current
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .size(ButtonDefaults.LargeButtonSize),
                    onClick = {
                        context.startActivity(Intent(context, EjectActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                ) {


                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "airplane",
                        modifier = Modifier
                            .size(42.dp)
                           
                    )
                }

            }

            item {
                Chip(
                    modifier = defaultModifierPadding,
                    onClick = { /* Do something */ },
                    enabled = true,
                    // When we have both label and secondary label present limit both to 1 line of text
                    label = {
                        Text(
                            text = "Deniz Özsakarya",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(text = "Call", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.PlayArrow,
                            contentDescription = "call",
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    }
                )
            }

            item {
                Chip(
                    modifier = defaultModifierPadding,
                    onClick = { /* Do something */ },
                    enabled = true,
                    // When we have both label and secondary label present limit both to 1 line of text
                    label = {
                        Text(
                            text = "Deniz Özsakarya",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(text = "Call", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.PlayArrow,
                            contentDescription = "call",
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    }
                )
            }

            item {
                Chip(
                    modifier = defaultModifierPadding,
                    onClick = { /* Do something */ },
                    enabled = true,
                    // When we have both label and secondary label present limit both to 1 line of text
                    label = {
                        Text(
                            text = "Deniz Özsakarya",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(text = "Call", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    icon = {
                        Icon(
                            Icons.Outlined.PlayArrow,
                            contentDescription = "call",
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    }
                )
            }
        }

        LaunchedEffect(Unit){
            focusRequester.requestFocus()
        }










    }
}








@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}







