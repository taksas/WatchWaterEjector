package net.taksas.apps.watchwaterejecter.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.AssetFileDescriptor

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.outlined.BubbleChart
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.theme.WatchWaterEjecterTheme
import java.util.Timer
import kotlin.concurrent.timerTask
import androidx.compose.ui.graphics.StrokeCap

import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.PathMeasure



var SOUND_LEVEL = 1.0f
var SOUND_LENGTH = 5.0f
var VIBRATION_LEVEL = 1.0f


val sound_pattern_list = listOf("Pulse_L", "Pulse_M", "Pulse_H", "Pulse_Wave", "Pulse_Fast_L", "Pulse_Fast_M", "Pulse_Fast_H", "Pulse_Fast_Wave")
var SOUND_PATTERN = sound_pattern_list[1]




class EjectActivity : ComponentActivity() {

    companion object {
        private var instance: EjectActivity? = null

        fun finishActivity() {
            instance?.finish()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        // 設定の読み込み
        val sharedPref = getSharedPreferences("net.taksas.apps.watchwaterejecter.main_preference", Context.MODE_PRIVATE)

        SOUND_LEVEL = sharedPref.getFloat("SOUND_LEVEL", 1.0f)
        SOUND_LENGTH = sharedPref.getFloat("SOUND_LENGTH", 5.0f)
        VIBRATION_LEVEL = sharedPref.getFloat("VIBRATION_LEVEL", 1.0f)
        SOUND_PATTERN = sharedPref.getString("selected_pattern", sound_pattern_list[1]) ?: sound_pattern_list[1]

            setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            EjectMain()
        }
    }

    // クラス外からアクティビティを終了させる

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}



@Composable
fun EjectMain() {
    WatchWaterEjecterTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            //TimeText()
            EjectLayout()
            AudioPlayer()
        }
    }
}

@Composable
fun EjectLayout() {

    Icon(
        Icons.Filled.BubbleChart,
        contentDescription = "airplane",
        modifier = Modifier
            .size(64.dp),
        tint = Color(0xFF99CCFF)
    )


    // (SOUND_LENGTH*1000/2).toInt()

    ShapeAsLoader()

}





@Composable
fun ShapeAsLoader() {
    val pathMeasurer = remember {
        PathMeasure()
    }
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween((SOUND_LENGTH*1000).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3600f,
        animationSpec = infiniteRepeatable(
            tween(50000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 12,
            innerRadius = 2f / 3f,
            rounding = CornerRounding(0.7f / 6f)
        )
    }
    val circlePolygon = remember {
        RoundedPolygon.circle(
            numVertices = 12
        )
    }
    val morph = remember {
        Morph(starPolygon, circlePolygon)
    }
    var morphPath = remember {
        Path()
    }
    val destinationPath = remember {
        Path()
    }
    var androidPath = remember {
        android.graphics.Path()
    }
    val matrix = remember {
        Matrix()
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .drawWithCache {
                // We first convert to an android.graphics.Path, then to compose Path.
                androidPath = morph.toPath(progress.value, androidPath)
                morphPath = androidPath.asComposePath()
                matrix.reset()
                matrix.scale(size.minDimension / 2f, size.minDimension / 2f)
                morphPath.transform(matrix)

                pathMeasurer.setPath(morphPath, false)
                val totalLength = pathMeasurer.length
                destinationPath.reset()
                pathMeasurer.getSegment(0f, totalLength * progress.value, destinationPath)

                onDrawBehind {
                    rotate(rotation.value) {
                        translate(size.width / 2f, size.height / 2f) {
                            val brush =
                                Brush.sweepGradient((1..2).flatMap { colors }, center = Offset(0.5f, 0.5f))
                            drawPath(
                                morphPath,
                                brush,
                                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
            .fillMaxSize()
    )
}

private val colors = listOf(
    Color(0xFF002699),
    Color(0xFF0033CC),
    Color(0xFF0055FF),
    Color(0xFF66B3FF),
    Color(0xFF99CCFF),
    Color(0xFF66B3FF),
    Color(0xFF0055FF),
    Color(0xFF0033CC),
    Color(0xFF002699),
)

fun Morph.toComposePath(progress: Float, scale: Float = 1f, path: Path = Path()): Path {
    var first = true
    path.rewind()
    forEachCubic(progress) { bezier ->
        if (first) {
            path.moveTo(bezier.anchor0X * scale, bezier.anchor0Y * scale)
            first = false
        }
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}






















@Composable
fun AudioPlayer() {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

    // 音量を任意のものに設定
    audioManager.setStreamVolume(
        AudioManager.STREAM_ALARM,
        (audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * SOUND_LEVEL).toInt(),
        0
    );


    DisposableEffect(key1 = mediaPlayer) {
        // assetsフォルダからファイルを開く
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("$SOUND_PATTERN.wav")
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


        // vibration
        var vibration_duration = longArrayOf(50000L)
        var vibration_level = intArrayOf((255 * VIBRATION_LEVEL).toInt())

        var vibratorManager: VibratorManager? = null
        var vibrator: Vibrator? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
            val vibrationEffect =
                VibrationEffect.createWaveform(vibration_duration, vibration_level, 0)
            val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
            vibratorManager.vibrate(combinedVibration)
        } else {
            @Suppress("DEPRECATION")
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val vibrationEffect =
                VibrationEffect.createWaveform(vibration_duration, vibration_level, 0)
            vibrator.vibrate(vibrationEffect)
        }


        val timer = Timer()
        val task = timerTask {
            // 終了処理
            mediaPlayer.stop()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (vibratorManager != null) vibratorManager.cancel()
            } else {
                if (vibrator != null) vibrator.cancel()
            }
            EjectActivity.finishActivity()
        }

        timer.schedule(task, (SOUND_LENGTH * 1000).toLong())  // 2秒後にタスクを実行


        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }


    }

}










@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun EjectPreview() {
    EjectMain()
}
