package edu.logiroute.logiroute.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.logiroute.domain.model.Package
import edu.logiroute.logiroute.previews.SampleData
import edu.logiroute.logiroute.theme.ByteBloomTokens

@Composable
fun PackagePriorityBadge(
    pkg: Package,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (pkg.priority.name.uppercase()) {
        "URGENT", "CRITICAL" -> ByteBloomTokens.ErrorRed
        "STANDARD" -> ByteBloomTokens.PrimaryAccent
        else -> ByteBloomTokens.ElevatedCardSurface
    }

    val textColor = when (pkg.priority.name.uppercase()) {
        "URGENT", "CRITICAL", "STANDARD" -> ByteBloomTokens.OnPrimary
        else -> ByteBloomTokens.TextSecondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(ByteBloomTokens.CornerRadiusMedium)
            )
            .padding(ByteBloomTokens.SpaceLarge),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ID: ${pkg.id} (${pkg.priority.name})",
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(
            text = "${pkg.weight} kg",
            color = ByteBloomTokens.TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun PreviewPackagePriorityBadgeUrgent() {
    PackagePriorityBadge(pkg = SampleData.urgentPackage)
}

@Preview
@Composable
private fun PreviewPackagePriorityBadgeStandard() {
    PackagePriorityBadge(pkg = SampleData.standardPackage)
}

@Preview
@Composable
private fun PreviewPackagePriorityBadgeLow() {
    PackagePriorityBadge(pkg = SampleData.lowPackage)
}