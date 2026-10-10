package com.roamly.app

import com.roamly.app.ai.RoamlyAiCompanion
import com.roamly.app.data.SampleData
import com.roamly.app.model.ChatSender
import com.roamly.app.model.ChecklistCategory
import com.roamly.app.model.TripStatus
import com.roamly.app.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoamlyDataModelTest {

    @Test
    fun testSampleTripsIntegrity() {
        val trips = SampleData.sampleTrips
        assertEquals("Singapore must be the single available trip", 1, trips.size)
        val singaporeTrip = trips.first()
        assertEquals("trip_singapore_2026", singaporeTrip.id)
        assertEquals("Singapore", singaporeTrip.destination)
        assertEquals("Singapore Explorer", singaporeTrip.title)
        assertEquals(5, singaporeTrip.durationDays)
        assertEquals(5, singaporeTrip.days.size)

        // Verify Day 1 to Day 5 destinations
        singaporeTrip.days.forEachIndexed { index, day ->
            assertEquals(index + 1, day.dayNumber)
            assertTrue("Day ${day.dayNumber} must have destinations", day.destinations.isNotEmpty())
            day.destinations.forEach { dest ->
                assertNotNull(dest.name)
                assertNotNull(dest.shortDescription)
                assertNotNull(dest.fullDescription)
            }
        }
    }

    @Test
    fun testSampleChecklistIntegrity() {
        val checklist = SampleData.sampleChecklist
        assertTrue("Checklist must not be empty", checklist.isNotEmpty())

        // Ensure key categories are represented
        val categories = checklist.map { it.category }.toSet()
        assertTrue(categories.contains(ChecklistCategory.DOCUMENTS))
        assertTrue(categories.contains(ChecklistCategory.CLOTHING))
        assertTrue(categories.contains(ChecklistCategory.ESSENTIALS))
        assertTrue(categories.contains(ChecklistCategory.ELECTRONICS))
        assertTrue(categories.contains(ChecklistCategory.HEALTH))

        // Non-destructive: verify initial state
        val totalCount = checklist.size
        val packedCount = checklist.count { it.isPacked }
        assertTrue("Total count should be greater than 0", totalCount > 0)
        assertTrue("Initial packed count should be reasonable", packedCount >= 0)
    }

    @Test
    fun testTravellersIntegrity() {
        val travellers = SampleData.sampleTravellers
        assertTrue("Sample travellers must not be empty", travellers.isNotEmpty())

        travellers.forEach { traveller ->
            assertNotNull(traveller.id)
            assertNotNull(traveller.name)
            assertNotNull(traveller.assignedTripId)
            assertTrue(
                listOf("Ready", "Preparation Incomplete", "Documents Pending")
                    .contains(traveller.preparationStatus)
            )
        }
    }

    @Test
    fun testRolesAndStatus() {
        assertEquals("TRAVELLER", UserRole.TRAVELLER.name)
        assertEquals("GUIDE", UserRole.GUIDE.name)

        assertEquals("Planning", TripStatus.PLANNING.displayName)
        assertEquals("Upcoming", TripStatus.UPCOMING.displayName)
        assertEquals("In Progress", TripStatus.IN_PROGRESS.displayName)
        assertEquals("Completed", TripStatus.COMPLETED.displayName)
    }

    @Test
    fun testOfflineAiCompanion() {
        val singaporeTrip = SampleData.sampleTrips.first()

        val packResponse = RoamlyAiCompanion.generateResponse("What should I pack?", singaporeTrip)
        assertEquals(ChatSender.ROAMLY_AI, packResponse.sender)
        assertTrue(packResponse.text.contains("Packing") || packResponse.text.contains("pack"))

        val marinaResponse = RoamlyAiCompanion.generateResponse("Marina Bay", singaporeTrip)
        assertTrue(marinaResponse.text.contains("Marina Bay") || marinaResponse.text.contains("SkyPark"))

        val unknownResponse = RoamlyAiCompanion.generateResponse("Random unrelated query xyz", singaporeTrip)
        assertNotNull(unknownResponse.text)
    }

    @Test
    fun testDestinationDiscoveryIntegrity() {
        val destinations = SampleData.sampleDestinations
        assertEquals(4, destinations.size)

        val countries = destinations.map { it.country }
        assertTrue(countries.contains("Singapore"))
        assertTrue(countries.contains("Japan"))
        assertTrue(countries.contains("Thailand"))
        assertTrue(countries.contains("Switzerland"))

        val singapore = destinations.find { it.country == "Singapore" }
        assertNotNull(singapore)
        assertTrue(singapore!!.isPlanAvailable)
        assertEquals("Trip plan available", singapore.availabilityStatus)
        assertEquals("5 days / 4 nights", singapore.durationText)
        assertEquals("trip_singapore_2026", singapore.associatedTripId)
        assertEquals("🇸🇬", singapore.flagEmoji)

        val otherDestinations = destinations.filter { it.country != "Singapore" }
        otherDestinations.forEach { dest ->
            assertFalse("${dest.country} must not claim trip plan is available", dest.isPlanAvailable)
            assertTrue(dest.availabilityStatus.contains("coming soon", ignoreCase = true))
            assertEquals("Preview only", dest.durationText)
            assertNull(dest.associatedTripId)
        }
    }

    @Test
    fun testSingaporeCostEstimateIntegrity() {
        val cost = SampleData.singaporeCostEstimate
        assertEquals("trip_singapore_2026", cost.tripId)
        assertEquals("Singapore", cost.destination)
        assertEquals("₹", cost.currencySymbol)
        assertEquals(90000, cost.totalEstimatedAmountInr)
        assertEquals("₹90,000", cost.formattedTotal())

        // 9 required breakdown categories
        assertEquals(9, cost.items.size)
        val categories = cost.items.map { it.category }
        assertTrue(categories.any { it.contains("Flight", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Accommodation", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Meal", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Attraction", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Airport", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Transport", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Insurance", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Visa", ignoreCase = true) })
        assertTrue(categories.any { it.contains("Miscellaneous", ignoreCase = true) })

        // Verify mathematical equality: sum of individual items MUST equal total
        val itemSum = cost.items.sumOf { it.estimatedAmountInr }
        assertEquals(90000, itemSum)
        assertEquals(cost.totalEstimatedAmountInr, itemSum)

        // Verify disclaimer explicitly declares unbooked status
        assertTrue(cost.disclaimer.contains("Flights and hotel are not booked"))
        assertTrue(cost.disclaimer.contains("Estimated planning budget"))
    }

    @Test
    fun testDestinationSearchFiltering() {
        val destinations = SampleData.sampleDestinations

        fun filter(query: String) = destinations.filter { dest ->
            dest.country.contains(query, ignoreCase = true) ||
            dest.name.contains(query, ignoreCase = true) ||
            dest.highlights.any { it.contains(query, ignoreCase = true) }
        }

        val sgResults = filter("singapore")
        assertEquals(1, sgResults.size)
        assertEquals("Singapore", sgResults.first().country)

        val jpResults = filter("Japan")
        assertEquals(1, jpResults.size)
        assertEquals("Japan", jpResults.first().country)

        val thResults = filter("Thailand")
        assertEquals(1, thResults.size)
        assertEquals("Thailand", thResults.first().country)

        val chResults = filter("Switzerland")
        assertEquals(1, chResults.size)
        assertEquals("Switzerland", chResults.first().country)

        val landResults = filter("land")
        assertEquals(2, landResults.size) // Thailand and Switzerland

        val emptyResults = filter("NonExistentCountry123")
        assertTrue(emptyResults.isEmpty())
    }
}

