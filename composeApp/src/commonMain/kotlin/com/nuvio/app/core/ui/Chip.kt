package com.nuvio.app.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    if (LocalMaterialYou.current) {
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = modifier,
            enabled = enabled,
            shape = MaterialTheme.shapes.small,
        )
    } else {
        val tokens = MaterialTheme.nuvio
        Surface(
            selected = selected,
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = nuvioShape(default = RoundedCornerShape(NuvioTokens.Radius.md), materialYou = RoundedCornerShape(12.dp)),
            color = if (selected) tokens.colors.overlaySelected else Color.Transparent,
            contentColor = when {
                !enabled -> tokens.colors.textDisabled
                selected -> tokens.colors.textPrimary
                else -> tokens.colors.textMuted
            },
            border = BorderStroke(
                width = tokens.borders.thin,
                color = when {
                    selected -> tokens.colors.borderSelected
                    enabled -> tokens.colors.borderStrong
                    else -> tokens.colors.borderDefault
                },
            ),
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(
                    horizontal = NuvioTokens.Space.s12,
                    vertical = tokens.components.chipVerticalPadding,
                ),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
