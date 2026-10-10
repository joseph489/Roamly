package com.roamly.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roamly.app.data.RoamlyRepository
import com.roamly.app.data.SampleData
import com.roamly.app.model.CostCategoryItem
import com.roamly.app.model.DestinationItem
import com.roamly.app.model.Trip
import com.roamly.app.ui.components.BelongingsReminderDialog
import com.roamly.app.ui.theme.RoamlyAccent
import com.roamly.app.ui.theme.RoamlyAccentContainer
import com.roamly.app.ui.theme.RoamlyBackground
import com.roamly.app.ui.theme.RoamlyBorder
import com.roamly.app.ui.theme.RoamlyPrimary
import com.roamly.app.ui.theme.RoamlyPrimaryContainer
import com.roamly.app.ui.theme.RoamlySecondary
import com.roamly.app.ui.theme.RoamlySkyBlue
import com.roamly.app.ui.theme.RoamlySurface
import com.roamly.app.ui.theme.RoamlySurfaceVariant
import com.roamly.app.ui.theme.RoamlyTextPrimary
import com.roamly.app.ui.theme.RoamlyTextSecondary
import com.roamly.app.ui.theme.RoamlyWarning
import kotlinx.coroutines.launch

@Composable
fun SingaporeOverviewScreen(
    repository: RoamlyRepository,
    onBackToDestinations: () -> Unit
) {
    var showDailyItineraryView by remember { mutableStateOf(false) }
    val trip = repository.getCurrentTrip()
    val costEstimate = repository.getTripCostEstimate(trip.id) ?: SampleData.singaporeCostEstimate

    // Belongings reminder dialog for itinerary
    var showBelongingsDialogForDest by remember { mutableStateOf<DestinationItem?>(null) }

    if (showBelongingsDialogForDest != null) {
        BelongingsReminderDialog(
            destinationName = showBelongingsDialogForDest!!.name,
            onDismiss = { showBelongingsDialogForDest = null },
            onConfirmed = {
                repository.markDepartureReminderAcknowledged(trip.id, showBelongingsDialogForDest!!.id)
                repository.markDestinationVisited(trip.id, showBelongingsDialogForDest!!.id, true)
                showBelongingsDialogForDest = null
            }
        )
    }

    if (showDailyItineraryView) {
        DailyItineraryView(
            trip = trip,
            repository = repository,
            onBackToTrips = { showDailyItineraryView = false },
            onTriggerBelongingsReminder = { dest ->
                showBelongingsDialogForDest = dest
            }
        )
        return
    }

    // Intercept back button to return to Destination Discovery
    BackHandler(onBack = onBackToDestinations)

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoamlyBackground)
    ) {
        // Top Navigation Header with Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoamlySurface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .border(0.5.dp, RoamlyBorder),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToDestinations) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Destinations",
                        tint = RoamlyPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Explore Singapore",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoamlyTextPrimary
                    )
                    Text(
                        text = "Destination Overview & Planning",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoamlyTextSecondary
                    )
                }
            }

            // Status chip: "Sample Itinerary • Ready to plan" (Not "Booked")
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RoamlyPrimaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Ready to Plan",
                    style = MaterialTheme.typography.labelSmall,
                    color = RoamlyPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 1. HERO BANNER
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, RoamlyBorder, RoundedCornerShape(22.dp))
                ) {
                    Column {
                        // Hero Art Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            RoamlyPrimary,
                                            RoamlySecondary,
                                            RoamlySkyBlue.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Country & Flag badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RoamlySurface.copy(alpha = 0.25f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🇸🇬",
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "SINGAPORE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoamlySurface,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        )
                                    }

                                    // Duration badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RoamlySurface.copy(alpha = 0.25f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "5 DAYS / 4 NIGHTS",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RoamlySurface,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Singapore Explorer",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RoamlySurface,
                                    fontSize = 24.sp
                                )
                            }
                        }

                        // Hero Details & Status Notes
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = RoamlyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${trip.dates} (5 days)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoamlyTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RoamlyAccentContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Sample Itinerary",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RoamlyTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = trip.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = RoamlyTextSecondary,
                                lineHeight = 21.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Meeting Point / Arrival note
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = RoamlyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Arrival: ${trip.meetingPoint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RoamlyTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. TRIP PLANNING ACTIONS
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Trip Planning Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action 1: View 5-Day Itinerary (Primary)
                        Button(
                            onClick = { showDailyItineraryView = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoamlyPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = RoamlySurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View 5-Day Itinerary",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoamlySurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = RoamlySurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action 2: View Cost Breakdown (Scrolls to cost section)
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(2)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, RoamlyPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalAtm,
                                contentDescription = null,
                                tint = RoamlyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "View Cost Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoamlyPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Action 3: Explore Attractions
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(3)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoamlyBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = RoamlyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Attractions",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RoamlyTextPrimary
                                )
                            }

                            // Action 4: Back to Destinations
                            OutlinedButton(
                                onClick = onBackToDestinations,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoamlyBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = RoamlyTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Back",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RoamlyTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 3. ESTIMATED TRIP COST CARD (Section index = 2)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, RoamlyBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(RoamlyPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalAtm,
                                        contentDescription = null,
                                        tint = RoamlyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Estimated Trip Budget",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = RoamlyTextPrimary
                                    )
                                    Text(
                                        text = "Illustrative planning estimate",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoamlyTextSecondary
                                    )
                                }
                            }

                            // Indian Currency Label
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RoamlySurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "INR (₹)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RoamlyTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Display of Total: ₹90,000 per person
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(RoamlyPrimaryContainer.copy(alpha = 0.45f))
                                .border(1.dp, RoamlyPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = costEstimate.formattedTotal(),
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = RoamlyPrimary,
                                        fontSize = 32.sp
                                    )
                                    Text(
                                        text = "per person",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoamlyTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Demo planning estimate for 5-day Singapore journey.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RoamlyTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // MANDATORY DISCLAIMER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(RoamlyAccentContainer.copy(alpha = 0.7f))
                                .border(1.dp, RoamlyWarning.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Disclaimer",
                                    tint = RoamlyWarning,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = costEstimate.disclaimer,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = RoamlyTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Estimated Cost Breakdown (9 Categories)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = RoamlyTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Detailed Cost Breakdown Items (Sums to ₹90,000)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            costEstimate.items.forEach { item ->
                                CostItemRow(item = item)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = RoamlyBorder, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sum Verification Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Estimated Sum",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RoamlyTextPrimary
                                )
                                Text(
                                    text = "Sum of all 9 item categories above",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RoamlyTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = costEstimate.formattedTotal(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = RoamlyPrimary
                            )
                        }
                    }
                }
            }

            // 4. EXPLORE ATTRACTIONS SECTION (Section index = 3)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = RoamlySurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoamlyBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Key Singapore Attractions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoamlyTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RoamlyPrimaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "5-Day Highlights",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoamlyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Featured landmarks planned across the 5 daily schedules:",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoamlyTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            AttractionHighlightRow(
                                dayNumber = 1,
                                title = "Marina Bay Sands SkyPark & Gardens by the Bay",
                                category = "Iconic Skyline & Cloud Forest",
                                tip = "Timed-entry ticket slots avoid lines. Stay for the evening Garden Rhapsody."
                            )
                            AttractionHighlightRow(
                                dayNumber = 2,
                                title = "Buddha Tooth Relic Temple & Chinatown",
                                category = "Heritage & Hawker Dining",
                                tip = "Modest clothing covering shoulders and knees is provided at temple entrance."
                            )
                            AttractionHighlightRow(
                                dayNumber = 3,
                                title = "National Gallery & Clarke Quay Cruise",
                                category = "Arts & Riverfront Stroll",
                                tip = "Sunset bumboat cruise provides golden hour views of Merlion Park and Fullerton."
                            )
                            AttractionHighlightRow(
                                dayNumber = 4,
                                title = "Sentosa Island & S.E.A. Aquarium",
                                category = "Marine Wonder & Beach Walk",
                                tip = "Take Sentosa Express monorail from VivoCity directly to Resorts World."
                            )
                            AttractionHighlightRow(
                                dayNumber = 5,
                                title = "Jewel Changi HSBC Rain Vortex & Departure",
                                category = "Modern Wonder & Airport",
                                tip = "Early baggage check-in available directly inside Jewel before terminal transit."
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showDailyItineraryView = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoamlySurfaceVariant)
                        ) {
                            Text(
                                text = "Open Full 5-Day Itinerary Schedule",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoamlyPrimary
                            )
                        }
                    }
                }
            }

            // 5. TRAVEL PREPARATION & ENTRY ESSENTIALS
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
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = RoamlyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Travel Preparation Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RoamlyTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            TravelNoteItem(
                                title = "SG Arrival Card (SGAC)",
                                description = "All travellers must submit the electronic SGAC declaration within 3 days prior to arrival. Official service is free via ICA portal."
                            )
                            TravelNoteItem(
                                title = "Currency & Contactless Payments",
                                description = "Public transit accepts contactless Mastercard/Visa. Carry modest Singapore Dollar cash for traditional hawker stalls."
                            )
                            TravelNoteItem(
                                title = "Electrical Plug Standard",
                                description = "Singapore uses Type G (UK 3-pin rectangular plug) at 230V 50Hz. Pack a universal adapter."
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CostItemRow(item: CostCategoryItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RoamlySurfaceVariant)
            .border(0.5.dp, RoamlyBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val icon = getCategoryIcon(item.category)
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = RoamlyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = RoamlyTextPrimary
                    )
                }

                Text(
                    text = item.formattedAmount(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = RoamlyPrimary
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = RoamlyTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when {
        category.contains("Flight", ignoreCase = true) -> Icons.Default.Flight
        category.contains("Accommodation", ignoreCase = true) -> Icons.Default.Hotel
        category.contains("Meal", ignoreCase = true) -> Icons.Default.Restaurant
        category.contains("Attraction", ignoreCase = true) -> Icons.Default.Place
        category.contains("Airport", ignoreCase = true) -> Icons.Default.DirectionsBus
        category.contains("Transport", ignoreCase = true) -> Icons.Default.DirectionsBus
        category.contains("Insurance", ignoreCase = true) -> Icons.Default.Shield
        category.contains("Visa", ignoreCase = true) -> Icons.Default.AccountBalance
        else -> Icons.Default.MoreHoriz
    }
}

@Composable
private fun AttractionHighlightRow(
    dayNumber: Int,
    title: String,
    category: String,
    tip: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RoamlySurfaceVariant)
            .border(0.5.dp, RoamlyBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Day $dayNumber: $title",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = RoamlyTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                color = RoamlyPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tip: $tip",
                style = MaterialTheme.typography.bodySmall,
                color = RoamlyTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TravelNoteItem(title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = RoamlyPrimary,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = RoamlyTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = RoamlyTextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
