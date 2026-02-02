package com.joelkanyi.flip.tile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.joelkanyi.flip.R
import com.joelkanyi.flip.data.model.CoinSide
import com.joelkanyi.flip.data.repository.FlipRepositoryImpl
import kotlinx.coroutines.flow.first

private const val RESOURCES_VERSION = "1"
private const val ID_COIN_HEADS = "coin_heads"
private const val ID_COIN_TAILS = "coin_tails"

@OptIn(ExperimentalHorologistApi::class)
class MainTileService : SuspendingTileService() {

    private val repository by lazy { FlipRepositoryImpl(this) }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .addIdToImageMapping(
                ID_COIN_HEADS,
                ResourceBuilders.ImageResource.Builder()
                    .setAndroidResourceByResId(
                        ResourceBuilders.AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.coin_heads)
                            .build()
                    )
                    .build()
            )
            .addIdToImageMapping(
                ID_COIN_TAILS,
                ResourceBuilders.ImageResource.Builder()
                    .setAndroidResourceByResId(
                        ResourceBuilders.AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.coin_tails)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val lastResult = repository.lastResult.first()
        val stats = repository.stats.first()

        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout(requestParams, this, lastResult, stats.totalFlips))
                            .build()
                    )
                    .build()
            )
            .build()

        return TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTileTimeline(singleTileTimeline)
            .setFreshnessIntervalMillis(0)
            .build()
    }

    private fun tileLayout(
        requestParams: RequestBuilders.TileRequest,
        context: Context,
        lastResult: CoinSide?,
        totalFlips: Int
    ): LayoutElementBuilders.LayoutElement {
        val clickable = ModifiersBuilders.Clickable.Builder()
            .setId("flip_action")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(context.packageName)
                            .setClassName("com.joelkanyi.flip.presentation.MainActivity")
                            .build()
                    )
                    .build()
            )
            .build()

        val modifiers = ModifiersBuilders.Modifiers.Builder()
            .setClickable(clickable)
            .build()

        val content = if (lastResult != null) {
            LayoutElementBuilders.Column.Builder()
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    LayoutElementBuilders.Image.Builder()
                        .setResourceId(
                            if (lastResult == CoinSide.HEADS) ID_COIN_HEADS else ID_COIN_TAILS
                        )
                        .setWidth(dp(48f))
                        .setHeight(dp(48f))
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(dp(8f))
                        .build()
                )
                .addContent(
                    Text.Builder(context, lastResult.name)
                        .setColor(
                            argb(
                                if (lastResult == CoinSide.HEADS) 0xFF64B5F6.toInt()
                                else 0xFFFFB300.toInt()
                            )
                        )
                        .setTypography(Typography.TYPOGRAPHY_TITLE3)
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(dp(4f))
                        .build()
                )
                .addContent(
                    Text.Builder(context, "$totalFlips flips")
                        .setColor(argb(0xFF9E9E9E.toInt()))
                        .setTypography(Typography.TYPOGRAPHY_CAPTION2)
                        .build()
                )
                .setModifiers(modifiers)
                .build()
        } else {
            LayoutElementBuilders.Column.Builder()
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    Text.Builder(context, "Flip")
                        .setColor(argb(0xFFFFFFFF.toInt()))
                        .setTypography(Typography.TYPOGRAPHY_TITLE2)
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(dp(8f))
                        .build()
                )
                .addContent(
                    Text.Builder(context, "Tap to flip")
                        .setColor(argb(0xFF9E9E9E.toInt()))
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .build()
                )
                .setModifiers(modifiers)
                .build()
        }

        return PrimaryLayout.Builder(requestParams.deviceConfiguration)
            .setResponsiveContentInsetEnabled(true)
            .setContent(content)
            .build()
    }
}
