package net.taksas.apps.watchwaterejecter.complication

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.Icon
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.MonochromaticImageComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.SmallImage
import androidx.wear.watchface.complications.data.SmallImageComplicationData
import androidx.wear.watchface.complications.data.SmallImageType
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import net.taksas.apps.watchwaterejecter.R
import net.taksas.apps.watchwaterejecter.presentation.EjectActivity
import java.util.Calendar

/**
 * Skeleton for complication data source that returns short text.
 */
class MainComplicationService : SuspendingComplicationDataSourceService() {
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        //if (type == ComplicationType.SHORT_TEXT) { return createShortTextComplicationData() }
        //if (type == ComplicationType.LONG_TEXT) { return createLongTextComplicationData() }
        if (type == ComplicationType.SMALL_IMAGE) { return createSmallImageComplicationData() }
        //if (type == ComplicationType.SMALL_IMAGE) { return createMonochromaticImageComplicationData() }

        return null
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        //if (type == ComplicationType.SHORT_TEXT) { return createShortTextComplicationData() }
        //if (type == ComplicationType.LONG_TEXT) { return createLongTextComplicationData() }
        if (request.complicationType == ComplicationType.SMALL_IMAGE) { return createSmallImageComplicationData() }
        //if (type == ComplicationType.SMALL_IMAGE) { return createMonochromaticImageComplicationData() }

        return null
    }


//    private fun createShortTextComplicationData() : ComplicationData {
//        val intent = Intent(this, EjectActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        return ShortTextComplicationData.Builder(
//            text = PlainComplicationText.Builder(getString(R.string.complication_description_short)).build(),
//            contentDescription = PlainComplicationText.Builder(getString(R.string.complication_description))
//                .build()
//        ).setTapAction(pendingIntent).build()
//
//    }
//
//    private fun createLongTextComplicationData() : ComplicationData {
//        val intent = Intent(this, EjectActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        return LongTextComplicationData.Builder(
//            text = PlainComplicationText.Builder(getString(R.string.complication_description)).build(),
//            contentDescription = PlainComplicationText.Builder(getString(R.string.complication_description))
//                .build()
//        ).setTapAction(pendingIntent).build()
//
//    }

    private fun createSmallImageComplicationData() : ComplicationData {
        val intent = Intent(this, EjectActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return SmallImageComplicationData.Builder(
            smallImage = SmallImage.Builder(Icon.createWithResource(this, R.drawable.complication_icon), SmallImageType.ICON).build(),
            contentDescription = PlainComplicationText.Builder(getString(R.string.complication_description))
                .build()
        ).setTapAction(pendingIntent).build()

    }

//    private fun createMonochromaticImageComplicationData(): ComplicationData {
//        val intent = Intent(this, EjectActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val icon = Icon.createWithResource(this, R.mipmap.ic_launcher)
//        return MonochromaticImageComplicationData.Builder(
//            monochromaticImage = MonochromaticImage.Builder(icon).build(),
//            contentDescription = PlainComplicationText.Builder(getString(R.string.complication_description_short)).build()
//        ).setTapAction(pendingIntent).build()
//    }




}



