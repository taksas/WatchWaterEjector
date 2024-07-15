package net.taksas.apps.watchwaterejecter.presentation

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import kotlinx.coroutines.launch
import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.theme.WatchWaterEjecterTheme
import org.intellij.lang.annotations.Pattern



class PatternSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // 設定の読み込み
        val sharedPref = getSharedPreferences("net.taksas.apps.watchwaterejecter.main_preference", Context.MODE_PRIVATE)

        SOUND_LEVEL = sharedPref.getFloat("SOUND_LEVEL", 1.0f)
        SOUND_LENGTH = sharedPref.getFloat("SOUND_LENGTH", 5.0f)
        VIBRATION_LEVEL = sharedPref.getFloat("VIBRATION_LEVEL", 1.0f)
        SOUND_PATTERN = sharedPref.getString("selected_pattern", sound_pattern_list[1]) ?: sound_pattern_list[1]


        setContent {
            PatternSelectMain(sharedPref)
        }
    }
}

@Composable
fun PatternSelectMain(sharedPref: SharedPreferences) {
    WatchWaterEjecterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            //TimeText()
            PatternSelectLayout(sharedPref)
        }
    }
}



@Composable
fun PatternSelectLayout(sharedPref: SharedPreferences) {
    var defaultModifierPadding = Modifier.padding(vertical = 0.dp, horizontal = 0.dp)
    val listState = rememberScalingLazyListState()
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val sharedPrefEditor = sharedPref.edit()
    val context = LocalContext.current
    val options = listOf("Pulse_L", "Pulse_M", "Pulse_H", "Pulse_Wave", "Pulse_Fast_L", "Pulse_Fast_M", "Pulse_Fast_H", "Pulse_Fast_Wave")
    var selectedOption by remember { mutableStateOf(sharedPref.getString("selected_pattern", options[1]) ?: options[1]) }



    androidx.wear.compose.material.Scaffold(
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
                    fontSize = MaterialTheme.typography.body1.fontSize / fontScale)
                androidx.wear.compose.material.Text(
                    modifier = Modifier.padding(top = 0.dp, bottom = 0.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF99CCFF),
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.pattern_select),
                    style = adjustedTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }

            items(options.size) { index ->
                val option = options[index]
                ToggleChip(
                    checked = selectedOption == option,
                    onCheckedChange = {
                        if (it) {
                            selectedOption = option
                            sharedPrefEditor.putString("selected_pattern", selectedOption).apply()
                            AudioSamplePlayer(option, context)
                        }
                    },
                    label = {
                        val density = LocalDensity.current
                        val fontScale = density.fontScale
                        val adjustedTextStyle = MaterialTheme.typography.body1.copy(
                            fontSize = MaterialTheme.typography.body1.fontSize / fontScale
                        )
                        Text(
                            text = option,
                            style = adjustedTextStyle,
                            color = Color.White,
                            overflow = TextOverflow.Visible,
                            maxLines = 1
                        )
                    },
                    toggleControl = {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = null, // This is handled by the ToggleChip
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.White,
                                unselectedColor = Color.White
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                        .background(brushForButton, shape = RoundedCornerShape(50)),
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedStartBackgroundColor = Color.Transparent,
                        checkedEndBackgroundColor = Color.Transparent,

                        uncheckedStartBackgroundColor = Color.Transparent,
                        uncheckedEndBackgroundColor = Color.Transparent

                ),
                )
            }

        }

        LaunchedEffect(Unit){
            focusRequester.requestFocus()
        }
    }
}



fun AudioSamplePlayer(pattern: String, context: Context) {
    val mediaPlayer = MediaPlayer()
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

    // 音量を任意のものに設定
    audioManager.setStreamVolume(
        AudioManager.STREAM_ALARM,
        (audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * SOUND_LEVEL).toInt(),
        0
    );


    // assetsフォルダからファイルを開く
    val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("$pattern.wav")
    mediaPlayer.setDataSource(
        assetFileDescriptor.fileDescriptor,
        assetFileDescriptor.startOffset,
        assetFileDescriptor.length
    )
    assetFileDescriptor.close()
    mediaPlayer.setAudioAttributes(
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
    )
    mediaPlayer.setVolume(1F, 1F)
    mediaPlayer.setLooping(true)
    mediaPlayer.prepare()
    mediaPlayer.setOnCompletionListener {
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0)
        mediaPlayer.release()
    }
    mediaPlayer.start()

    // x秒後に実行する処理を記述する
    Handler(Looper.getMainLooper()).postDelayed({
        // 終了処理
        mediaPlayer.stop()
    },  (1000).toLong()) // ミリ秒

}





