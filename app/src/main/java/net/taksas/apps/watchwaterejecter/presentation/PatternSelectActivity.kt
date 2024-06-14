package net.taksas.apps.watchwaterejecter.presentation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
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
import net.taksas.apps.watchwaterejecter.presentation.theme.WatchWaterEjecterTheme

class PatternSelectActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        // 設定の読み込み
        val sharedPref = getSharedPreferences("net.taksas.apps.watchwaterejecter.main_preference", Context.MODE_PRIVATE)


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

            items(options.size) { index ->
                val option = options[index]
                ToggleChip(
                    checked = selectedOption == option,
                    onCheckedChange = {
                        if (it) {
                            selectedOption = option
                            sharedPrefEditor.putString("selected_pattern", selectedOption).apply()
                        }
                    },
                    label = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.body1
                        )
                    },
                    toggleControl = {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = null // This is handled by the ToggleChip
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                    colors = ToggleChipDefaults.toggleChipColors(
                        checkedEndBackgroundColor = MaterialTheme.colors.primaryVariant,
                        uncheckedEndBackgroundColor = MaterialTheme.colors.primaryVariant,
                        checkedStartBackgroundColor = MaterialTheme.colors.primary,
                        uncheckedStartBackgroundColor = MaterialTheme.colors.primary
                ),
                )
            }

        }

        LaunchedEffect(Unit){
            focusRequester.requestFocus()
        }











    }




}