package net.taksas.apps.watchwaterejecter.presentation

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

var SOUND_LEVEL = 0.3f
var SOUND_LENGTH = 10.0f
var VIBRATION_LEVEL = 1.0f






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
        Icons.Outlined.PlayArrow,
        contentDescription = "airplane",
        modifier = Modifier
            .size(42.dp)
    )

}



@Composable
fun AudioPlayer() {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

    // 音量を任意のものに設定
    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) * SOUND_LEVEL).toInt(), 0);


    DisposableEffect(key1 = mediaPlayer) {
        // assetsフォルダからファイルを開く
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd("sine_-06_05_00400.wav")
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
        var vibration_duration = longArrayOf(10000L)
        var vibration_level = intArrayOf((255*VIBRATION_LEVEL).toInt())

        var vibratorManager: VibratorManager? = null
        var vibrator: Vibrator? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
            val vibrationEffect = VibrationEffect.createWaveform(vibration_duration, vibration_level, 0)
            val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
            vibratorManager.vibrate(combinedVibration)
        } else {
            @Suppress("DEPRECATION")
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val vibrationEffect = VibrationEffect.createWaveform(vibration_duration, vibration_level, 0)
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

        timer.schedule(task, (SOUND_LENGTH*1000).toLong())  // 2秒後にタスクを実行



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
