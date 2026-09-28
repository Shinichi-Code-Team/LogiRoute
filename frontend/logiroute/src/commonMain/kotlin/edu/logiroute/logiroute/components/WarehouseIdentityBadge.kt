package edu.logiroute.logiroute.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun WarehouseIdentityBadge(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = onClick?.let {
        Modifier.clickable { it.invoke() }
    } ?: Modifier

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = ByteBloomTokens.SurfaceBase,
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium)
            )
            .clip(RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium))
            .then(clickableModifier)
            .padding(ByteBloomTokens.SpaceLarge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = warehouse.name,
            color = ByteBloomTokens.TextPrimary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "ID: ${warehouse.id}",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace
        )
        Box(
            modifier = Modifier
                .background(
                    color = ByteBloomTokens.SecondaryAccent,
                    shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusSmall)
                )
                .padding(
                    horizontal = ByteBloomTokens.SpaceSmall,
                    vertical = ByteBloomTokens.SpaceMicro
                )
        ) {
            Text(
                text = warehouse.regionalZone,
                color = ByteBloomTokens.OnPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview
@Composable
private fun PreviewWarehouseIdentityBadge() {
    WarehouseIdentityBadge(warehouse = SampleData.centralWarehouse)
}