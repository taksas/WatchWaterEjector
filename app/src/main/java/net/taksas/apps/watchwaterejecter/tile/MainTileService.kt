package net.taksas.apps.watchwaterejecter.tile

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.TitleChip
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.tools.LayoutRootPreview
import com.google.android.horologist.compose.tools.buildDeviceParameters
import com.google.android.horologist.tiles.SuspendingTileService
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ModifiersBuilders
import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.EjectActivity

private const val RESOURCES_VERSION = "0"

/**
 * Skeleton for a tile with no images.
 */
@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder().setVersion(RESOURCES_VERSION).build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val singleTileTimeline = TimelineBuilders.Timeline.Builder().addTimelineEntry(
            TimelineBuilders.TimelineEntry.Builder().setLayout(
                LayoutElementBuilders.Layout.Builder().setRoot(tileLayout(this)).build()
            ).build()
        ).build()

        return TileBuilders.Tile.Builder().setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(singleTileTimeline).build()
    }
}


private fun tileLayout(context: Context): LayoutElementBuilders.LayoutElement {
    val buttonClickable = ModifiersBuilders.Clickable.Builder()
        .setId("eject_button")
        .setOnClick(
            ActionBuilders.LaunchAction.Builder()
                .setAndroidActivity(
                    ActionBuilders.AndroidActivity.Builder()
                        .setPackageName(context.packageName)
                        .setClassName(EjectActivity::class.java.name)
                        .build()
                )
                .build()
        )
        .build()

    return PrimaryLayout.Builder(buildDeviceParameters(context.resources))
        .setPrimaryLabelTextContent(
            Text.Builder(context,  context.getString(R.string.app_name))
                .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                .setColor(ColorBuilders.argb(Color.parseColor("#FF66B3FF")))
                .build()
        )
        .setContent(
            TitleChip.Builder(context, context.getString(R.string.tile_start_button_label), buttonClickable, buildDeviceParameters(context.resources))
                // TitleChip/Chip's default width == device width minus some padding
                // Since PrimaryLayout's content slot already has margin, this leads to clipping
                // unless we override the width to use the available space
                .setWidth(DimensionBuilders.ExpandedDimensionProp.Builder().build())
                .setChipColors(
                    ChipColors(
                        /*backgroundColor=*/
                        argb(Color.parseColor("#FF0033CC")),
                        /*contentColor=*/
                        argb(Color.parseColor("#FF99CCFF"))
                    )
                )
                .build()
        )
        .setSecondaryLabelTextContent(
            Text.Builder(context, context.getString(R.string.tile_sub_label))
                .setTypography(Typography.TYPOGRAPHY_CAPTION2)
                .setColor(ColorBuilders.argb(Color.parseColor("#FF66B3FF")))
                .build()
        )
        .build()



}

@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showSystemUi = true,
    backgroundColor = 0xff000000,
    showBackground = true
)
@Composable
fun TilePreview() {
    LayoutRootPreview(root = tileLayout(LocalContext.current))
}