package com.roamly.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roamly.app.data.RoamlyRepository
import com.roamly.app.model.DestinationDiscovery
import com.roamly.app.model.Trip
import com.roamly.app.ui.theme.RoamlyAccent
import com.roamly.app.ui.theme.RoamlyAccentContainer
import com.roamly.app.ui.theme.RoamlyBackground
import com.roamly.app.ui.theme.RoamlyBorder
import com.roamly.app.ui.theme.RoamlyPrimary
import com.roamly.app.ui.theme.RoamlyPrimaryContainer
import com.roamly.app.ui.theme.RoamlySkyBlue
import com.roamly.app.ui.theme.RoamlySurface
import com.roamly.app.ui.theme.RoamlySurfaceVariant
import com.roamly.app.ui.theme.RoamlyTextPrimary
import com.roamly.app.ui.theme.RoamlyTextSecondary

@Composable
fun HomeScreen(
    repository: RoamlyRepository,
    onSelectSingapore: () -> Unit,
    onViewTravelPlan: (Trip) -> Unit = {},
    onNavigateToTrips: () -> Unit = {},
    onNavigateToAi: (prompt: String?) -> Unit = {},
    onNavigateToChecklists: () -> Unit = {}
) {
    val destinations by repository.destinations.collectAsState()
    val checklist by repository.checklist.collectAsState()
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedPreviewDestination by remember { mutableStateOf<DestinationDiscovery?>(null) }

    val filteredDestinations = remember(searchQuery, destinations) {
        if (searchQuery.isBlank()) {
            destinations
        } else {
            repository.filterDestinations(searchQuery)
        }
    }

    val totalItems = checklist.size
    val packedItems = checklist.count { it.isPacked }

    // Preview Dialog for non-Singapore destinations (Japan, Thailand, Switzerland)
    if (selectedPreviewDestination != null) {
        val dest = selectedPreviewDestination!!
        DestinationPreviewDialog(
            destination = dest,
            onDismiss = { selectedPreviewDestination = null },
            onExploreSingapore = {
                selectedPreviewDestination = null
                onSelectSingapore()
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RoamlyBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // 1. WELCOME HEADER (Roamly branding & welcoming greeting)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Where would you like to go?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoamlyTextPrimary,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Discover places and plan your next adventure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoamlyTextSecondary,
                        lineHeight = 20.sp
                    )
                }

                // Demo weather indicator badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(RoamlySurfaceVariant)
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Weather",
                                tint = RoamlyAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "31°C Sunny",
                                style = MaterialTheme.typography.labelMedium,
                                color = RoamlyTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Singapore",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoamlyTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // 2. SEARCH FIELD (Live filtering, clear button, proper keyboard actions)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search countries or destinations",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoamlyTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = RoamlyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = RoamlyTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RoamlySurface,
                    unfocusedContainerColor = RoamlySurface,
                    focusedBorderColor = RoamlyPrimary,
                    unfocusedBorderColor = RoamlyBorder
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )
        }

        // 3. POPULAR DESTINATIONS TITLE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Destinations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RoamlyTextPrimary
                )

                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "${filteredDestinations.size} result(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoamlyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 4. DESTINATION CARDS OR EMPTY STATE
        if (filteredDestinations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(RoamlyBorder)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(RoamlySurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = RoamlyTextSecondary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No destinations found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No destinations match \"$searchQuery\". Try searching for Singapore, Japan, Thailand, or Switzerland.",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoamlyTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { searchQuery = "" },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Clear Search")
                        }
                    }
                }
            }
        } else {
            items(filteredDestinations, key = { it.id }) { destination ->
                DestinationCard(
                    destination = destination,
                    onSelect = {
                        if (destination.isPlanAvailable) {
                            onSelectSingapore()
                        } else {
                            selectedPreviewDestination = destination
                        }
                    }
                )
            }
        }

        // 5. QUICK TRAVEL ACTIONS SECTION
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Quick Travel Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RoamlyTextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ACTION 1: What Should I Pack?
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToChecklists() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoamlyAccentContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Luggage,
                                contentDescription = null,
                                tint = RoamlyAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Packing List",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$packedItems/$totalItems packed",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoamlyTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // ACTION 2: Ask Travel Help
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToAi(null) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoamlyPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = RoamlyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Travel Help",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Offline assistant",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoamlyTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // ACTION 3: My Trips
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToTrips() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoamlySurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = RoamlyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "My Trips",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "1 Active Trip",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoamlyTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 6. TRAVEL PREPARATION REMINDER
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RoamlyBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = RoamlyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Travel Notice & Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Singapore is currently Roamly's fully implemented sample destination for Layover 2026. Detailed trip planning for other regions will roll out in upcoming phases.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoamlyTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DestinationCard(
    destination: DestinationDiscovery,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = RoamlySurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RoamlyBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onSelect)
    ) {
        Column {
            // Visual Banner with gradient tailored to destination
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(getDestinationGradient(destination.country))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country name and flag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoamlySurface.copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = destination.flagEmoji,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = destination.country.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = RoamlySurface,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                        }

                        // Availability status chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (destination.isPlanAvailable) RoamlySurface else RoamlySurface.copy(alpha = 0.25f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (destination.isPlanAvailable) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = RoamlyPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = destination.availabilityStatus,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (destination.isPlanAvailable) RoamlyPrimary else RoamlySurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = destination.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = RoamlySurface
                    )
                }
            }

            // Card Body: subtitle, highlights, and CTA button
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = destination.shortSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RoamlyTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Highlights / Duration Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (destination.isPlanAvailable) Icons.Default.CalendarMonth else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (destination.isPlanAvailable) RoamlyPrimary else RoamlyTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = destination.durationText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (destination.isPlanAvailable) RoamlyPrimary else RoamlyTextSecondary
                        )
                    }

                    if (destination.sampleBudgetEstimate != null) {
                        Text(
                            text = "Est. ${destination.sampleBudgetEstimate}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar
                if (destination.isPlanAvailable) {
                    Button(
                        onClick = onSelect,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoamlyPrimary)
                    ) {
                        Text(
                            text = "View Singapore Trip Overview",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = RoamlySurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = RoamlySurface,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onSelect,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoamlyBorder)
                    ) {
                        Text(
                            text = "Explore Destination Preview",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RoamlyTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun getDestinationGradient(country: String): Brush {
    return when (country.lowercase()) {
        "singapore" -> Brush.horizontalGradient(
            listOf(RoamlyPrimary, RoamlySecondary, RoamlySkyBlue.copy(alpha = 0.85f))
        )
        "japan" -> Brush.horizontalGradient(
            listOf(Color(0xFF8B475D), Color(0xFFA66277), Color(0xFFE09F9F))
        )
        "thailand" -> Brush.horizontalGradient(
            listOf(Color(0xFF9E6830), Color(0xFFC78B43), Color(0xFFECC279))
        )
        "switzerland" -> Brush.horizontalGradient(
            listOf(Color(0xFF3F647E), Color(0xFF5D84A0), Color(0xFF90B5CE))
        )
        else -> Brush.horizontalGradient(
            listOf(RoamlyPrimary, RoamlySecondary)
        )
    }
}

@Composable
private fun DestinationPreviewDialog(
    destination: DestinationDiscovery,
    onDismiss: () -> Unit,
    onExploreSingapore: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = destination.flagEmoji,
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = destination.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RoamlyTextPrimary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RoamlySurfaceVariant)
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Trip Planning Coming Soon",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = RoamlyTextSecondary
                    )
                }

                Text(
                    text = destination.shortSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = RoamlyTextPrimary
                )

                if (destination.highlights.isNotEmpty()) {
                    Text(
                        text = "Key highlights planned: " + destination.highlights.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = RoamlyTextSecondary
                    )
                }

                // Unobtrusive honest notice
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoamlyAccentContainer.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Detailed itineraries, cost estimates, and activities for ${destination.country} are currently under development. Roamly provides full sample trip planning for Singapore today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoamlyTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onExploreSingapore,
                colors = ButtonDefaults.buttonColors(containerColor = RoamlyPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Explore Singapore")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    color = RoamlyTextSecondary
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = RoamlySurface
    )
}
