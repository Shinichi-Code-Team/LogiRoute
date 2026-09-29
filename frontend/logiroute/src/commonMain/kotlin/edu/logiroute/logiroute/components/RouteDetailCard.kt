package edu.logiroute.logiroute.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Route
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun RouteDetailCard(
    route: Route,
    modifier: Modifier = Modifier,
    onClick: ((Route) -> Unit)? = null
) {
    val clickableModifier = onClick?.let {
        Modifier.clickable { it.invoke(route) }
    } ?: Modifier

    val delayTextColor = if (route.typicalDelayMin > 60) {
        ByteBloomTokens.WarningAmber
    } else {
        ByteBloomTokens.TextSecondary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium))
            .then(clickableModifier),
        shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium),
        border = BorderStroke(
            width = ByteBloomTokens.BorderWidthThin,
            color = ByteBloomTokens.Border
        ),
        colors = CardDefaults.cardColors(
            containerColor = ByteBloomTokens.ElevatedCardSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(ByteBloomTokens.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(ByteBloomTokens.SpaceLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = route.origin.name,
                    color = ByteBloomTokens.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = " ➔ ",
                    color = ByteBloomTokens.SecondaryAccent,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = route.destination.name,
                    color = ByteBloomTokens.TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${route.distanceKm} km",
                    color = ByteBloomTokens.TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${route.typicalDelayMin} min delay",
                    color = delayTextColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (route.typicalDelayMin > 60) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRouteDetailCardShort() {
    RouteDetailCard(route = SampleData.shortRoute)
}

@Preview
@Composable
private fun PreviewRouteDetailCardLongDelay() {
    RouteDetailCard(route = SampleData.longRouteWithDelay)
}