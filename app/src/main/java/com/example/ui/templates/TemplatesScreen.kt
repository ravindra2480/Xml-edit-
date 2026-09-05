package com.example.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TemplateItem
import com.example.data.model.TemplatesDataProvider
import com.example.ui.theme.*

@Composable
fun TemplatesScreen(
    onSelectTemplate: (TemplateItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All", "Trending", "Cinematic", "Reels", "Shorts", "YouTube",
        "Travel", "Wedding", "Festival", "Motivation", "Status", "Birthday"
    )
    var selectedCategory by remember { mutableStateOf("All") }
    val allTemplates = remember { TemplatesDataProvider.getTemplates() }

    val filtered = remember(selectedCategory) {
        if (selectedCategory == "All") allTemplates
        else allTemplates.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(XmlBackground)
            .padding(top = 16.dp)
    ) {
        // Screen Header
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Cinematic Templates",
                fontWeight = FontWeight.Black,
                fontSize = 22.sp,
                color = XmlWhite
            )
            Text(
                text = "One-tap presets with beat sync, licensed audio & animations",
                fontSize = 12.sp,
                color = XmlTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Categories Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) XmlPurple else XmlSurface)
                        .border(1.dp, if (isSelected) XmlElectricCyan else XmlBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) XmlWhite else XmlTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Templates List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filtered, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onUse = { onSelectTemplate(template) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: TemplateItem,
    onUse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, XmlBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = XmlSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category Pill + Duration & Beats Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(XmlPurpleDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = template.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlElectricCyan
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .background(XmlSurfaceHighlight, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${template.durationSec}s",
                            fontSize = 10.sp,
                            color = XmlWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(XmlSurfaceHighlight, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${template.beatsCount} Beats",
                            fontSize = 10.sp,
                            color = XmlSunsetOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = template.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = XmlWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = template.description,
                fontSize = 12.sp,
                color = XmlTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tags + Use Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    template.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            fontSize = 11.sp,
                            color = XmlTextMuted
                        )
                    }
                }

                Button(
                    onClick = onUse,
                    colors = ButtonDefaults.buttonColors(containerColor = XmlPurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("use_template_${template.id}")
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Use Template", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
