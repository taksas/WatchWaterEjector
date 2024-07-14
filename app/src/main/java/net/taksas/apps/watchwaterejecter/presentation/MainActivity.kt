/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package net.taksas.apps.watchwaterejecter.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.Build
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.remote.interactions.RemoteActivityHelper
import kotlinx.coroutines.launch



import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.theme.WatchWaterEjecterTheme
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // 設定の読み込み
        val sharedPref = getSharedPreferences("net.taksas.apps.watchwaterejecter.main_preference", Context.MODE_PRIVATE)


        setContent {
            WearApp(sharedPref)
        }
    }
}



@Composable
fun WearApp(sharedPref: SharedPreferences) {
    WatchWaterEjecterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            MainLayout(sharedPref)
        }
    }
}

@Composable
fun MainLayout(sharedPref: SharedPreferences) {
    var defaultModifierPadding = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sharedPrefEditor = sharedPref.edit()
    val context = LocalContext.current
    val localDensityContext = LocalDensity.current



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
                .padding(top = 0.dp, bottom = 0.dp, start = 4.dp, end = 4.dp,)
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

            // `ButtonDefaults.LargeButtonSize`をPxに変換
            val largeButtonSize = ButtonDefaults.LargeButtonSize
            val radiusInPx = with(localDensityContext) { largeButtonSize.toPx() }
            val brush = Brush.radialGradient(
                colors = listOf(Color(0xFF66B3FF), Color(0xFF0055FF)),
                center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                radius = radiusInPx
            )

            val displayMetrics = context.resources.displayMetrics
            val brushForButton = Brush.radialGradient(
                colors = listOf(Color(0xFF66B3FF), Color(0xFF0055FF)),
                center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                radius = displayMetrics.widthPixels.toFloat()/5
            )


            item {
                val density = LocalDensity.current
                val fontScale = density.fontScale
                val adjustedTextStyle = MaterialTheme.typography.body1.copy(
                    fontSize = MaterialTheme.typography.body1.fontSize / fontScale
                )

                Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF99CCFF),
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.app_name),
                    style = adjustedTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            item {


                Button(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .size(ButtonDefaults.LargeButtonSize)
                        .background(brush, shape = CircleShape),
                    onClick = {
                        context.startActivity(Intent(context, EjectActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    shape = CircleShape,
                ) {


                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier
                            .size(42.dp)
                           
                    )
                }

            }




            // sound_select
            item {
                Chip(
                    modifier = Modifier.padding(top = 0.dp, bottom = 16.dp)
                        .background(brushForButton, shape = RoundedCornerShape(50)),
                    onClick = { context.startActivity(Intent(context, PatternSelectActivity::class.java)) },
                    enabled = true,
                    // When we have both label and secondary label present limit both to 1 line of text
                    label = {

                        val density = LocalDensity.current
                        val fontScale = density.fontScale
                        val adjustedTextStyle = MaterialTheme.typography.body1.copy(
                            fontSize = MaterialTheme.typography.body1.fontSize / fontScale
                        )

                        Text(
                            text = stringResource(R.string.sound_select),
                            maxLines = 1,
                            style = adjustedTextStyle,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Build,
                            contentDescription = stringResource(R.string.sound_select_description),
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color.Transparent)
                )
            }


            // sound_level
            item {
                Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF99CCFF),
                    fontWeight = FontWeight.Normal,
                    text = stringResource(R.string.sound_level),
                    fontSize = 12.sp,
                )
            }
            item {
                var value by remember { mutableFloatStateOf(sharedPref.getFloat("SOUND_LEVEL", 1.0f)) }
                InlineSlider(
                    value = value,
                    onValueChange = {
                        sharedPrefEditor.putFloat("SOUND_LEVEL", it).apply()
                        value = it},
                    increaseIcon = { Icon(InlineSliderDefaults.Increase, "Increase") },
                    decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Decrease") },
                    valueRange = 0.1f..1.0f,
                    steps = 6,
                    segmented = true,
                    colors = InlineSliderDefaults.colors(
                        selectedBarColor = Color(0xFF99CCFF),
                        backgroundColor = Color(0xFF0033CC)
                    )
                )
            }

            // sound_length
            item {
                Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF99CCFF),
                    fontWeight = FontWeight.Normal,
                    text = stringResource(R.string.sound_length),
                    fontSize = 12.sp,
                )
            }
            item {
                var value by remember { mutableFloatStateOf(sharedPref.getFloat("SOUND_LENGTH", 5.0f)) }
                InlineSlider(
                    value = value,
                    onValueChange = {
                        sharedPrefEditor.putFloat("SOUND_LENGTH", it).apply()
                        value = it},
                    increaseIcon = { Icon(InlineSliderDefaults.Increase, "Increase") },
                    decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Decrease") },
                    valueRange = 1.0f..15.0f,
                    steps = 20,
                    segmented = false,
                    colors = InlineSliderDefaults.colors(
                        selectedBarColor = Color(0xFF99CCFF),
                        backgroundColor = Color(0xFF0033CC)
                    )
                )
            }

            // vibration_level
            item {
                Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF99CCFF),
                    fontWeight = FontWeight.Normal,
                    text = stringResource(R.string.vibration_level),
                    fontSize = 12.sp,
                )
            }
            item {
                var value by remember { mutableFloatStateOf(sharedPref.getFloat("VIBRATION_LEVEL", 1.0f)) }
                InlineSlider(
                    value = value,
                    onValueChange = {
                        sharedPrefEditor.putFloat("VIBRATION_LEVEL", it).apply()
                        value = it},
                    increaseIcon = { Icon(InlineSliderDefaults.Increase, "Increase") },
                    decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Decrease") },
                    valueRange = 0.0f..1.0f,
                    steps = 4,
                    segmented = true,
                    colors = InlineSliderDefaults.colors(
                        selectedBarColor = Color(0xFF99CCFF),
                        backgroundColor = Color(0xFF0033CC)
                    )
                )
            }


            // Help Button
            item {
                Chip(
                    modifier = Modifier.padding(top = 16.dp, bottom = 0.dp)
                        .background(brushForButton, shape = RoundedCornerShape(50)),
                    onClick = {
                        // 確認用画面（Artifact: androidx.wear:wear - ConfirmationActivity）
                        val intent = Intent(context, ConfirmationActivity::class.java).also {
                            it.putExtra(
                                ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION
                            )
                            it.putExtra(
                                ConfirmationActivity.EXTRA_MESSAGE,
                                context.getString(R.string.help_select_description)
                            )
                            it.putExtra(ConfirmationActivity.EXTRA_ANIMATION_DURATION_MILLIS,
                                  2000)
                        }
                        context.startActivity(intent)

                        // リモート（スマホ側）で開

                        val remoteActivityHelper = RemoteActivityHelper(context, Executors.newSingleThreadExecutor())
                        val result = remoteActivityHelper.startRemoteActivity(
                            Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(
                                    Uri.parse("https://taksas.net/watchwaterejecter")
                                ),
                            null
                        )
                    },
                    enabled = true,
                    // When we have both label and secondary label present limit both to 1 line of text
                    label = {
                        Text(
                            text = stringResource(R.string.help_select),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Rounded.Help,
                            contentDescription = stringResource(R.string.help_select_description),
                            modifier = Modifier
                                .size(ChipDefaults.IconSize)
                                .wrapContentSize(align = Alignment.Center),
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color.Transparent)
                )
            }






        }

        LaunchedEffect(Unit){
            focusRequester.requestFocus()
        }










    }
}






//
//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp(null)
//}







