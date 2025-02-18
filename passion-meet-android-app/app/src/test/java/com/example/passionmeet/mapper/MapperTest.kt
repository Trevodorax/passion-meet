package com.example.passionmeet.mapper

import com.example.passionmeet.data.local.entity.GroupEntity
import com.example.passionmeet.data.local.entity.PassionEntity
import com.example.passionmeet.models.EncounterModel
import com.example.passionmeet.models.EncounterStatus
import com.example.passionmeet.models.UserMetModel
import com.example.passionmeet.network.dto.GroupResponseDTO
import com.example.passionmeet.network.dto.LoginResponseDTO
import com.example.passionmeet.network.dto.PassionDto
import com.example.passionmeet.network.dto.SignupResponseDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapperTest {

    @Test
    fun `test mapPassionDtoToPassionEntity`() {
        // Given
        val passionDto = PassionDto(
            id = "1",
            name = "Sport",
            description = "Physical activities",
            picture = "sport.jpg",
            type = "Activity"
        )

        // When
        val result = mapPassionDtoToPassionEntity(passionDto)

        // Then
        assertEquals("1", result.id)
        assertEquals("Sport", result.name)
        assertEquals("Physical activities", result.description)
        assertEquals("sport.jpg", result.picture)
        assertEquals("Activity", result.type)
        assertFalse(result.isSelfPassion)
    }

    @Test
    fun `test mapPassionEntityToPassionCategoryModel`() {
        // Given
        val passionEntities = listOf(
            PassionEntity(
                id = "1",
                name = "Sport",
                description = "Physical activities",
                picture = "sport.jpg",
                type = "Activity",
                isSelfPassion = true
            ),
            PassionEntity(
                id = "2",
                name = "Reading",
                description = "Books and literature",
                picture = "reading.jpg",
                type = "Activity",
                isSelfPassion = false
            )
        )

        // When
        val result = mapPassionEntityToPassionCategoryModel(passionEntities)

        // Then
        assertEquals(1, result.size)
        assertEquals("Activity", result[0].name)
        assertEquals(2, result[0].items.size)

        val firstItem = result[0].items[0]
        assertEquals("1", firstItem.id)
        assertEquals("Sport", firstItem.name)
        assertEquals("sport.jpg", firstItem.image)
        assertTrue(firstItem.isSelected)

        val secondItem = result[0].items[1]
        assertEquals("2", secondItem.id)
        assertEquals("Reading", secondItem.name)
        assertEquals("reading.jpg", secondItem.image)
        assertFalse(secondItem.isSelected)
    }

    @Test
    fun `test mapGroupDtoToGroupEntity`() {
        // Given
        val groupDto = GroupResponseDTO(
            id = "1",
            name = "Sports Group",
            description = "Group for sports enthusiasts",
            imageUrl = "group.jpg"
        )

        // When
        val result = mapGroupDtoToGroupEntity(groupDto)

        // Then
        assertEquals("1", result.id)
        assertEquals("Sports Group", result.name)
        assertEquals("Group for sports enthusiasts", result.description)
        assertEquals("group.jpg", result.imageUrl)
    }

    @Test
    fun `test mapGroupEntityToGroupModel`() {
        // Given
        val groupEntities = listOf(
            GroupEntity(
                id = "1",
                name = "Sports Group",
                description = "Group for sports enthusiasts",
                imageUrl = "group.jpg"
            )
        )

        // When
        val result = mapGroupEntityToGroupModel(groupEntities)

        // Then
        assertEquals(1, result.size)
        with(result[0]) {
            assertEquals("1", id)
            assertEquals("Sports Group", name)
            assertEquals("Group for sports enthusiasts", description)
            assertEquals("group.jpg", image)
            assertEquals(0, members)
        }
    }

    @Test
    fun `test EncounterModel toEntity and toModel conversion`() {
        // Given
        val userMet = UserMetModel(
            id = "1",
            email = "user@example.com",
            username = "testUser"
        )
        val encounterModel = EncounterModel(
            id = "1",
            createdAt = "2024-03-20T10:00:00.000Z",
            isSeen = false,
            userMet = userMet
        )

        // When
        val entity = encounterModel.toEntity()
        val resultModel = entity.toModel()

        // Then
        assertEquals(encounterModel.id, entity.id)
        assertEquals(encounterModel.createdAt, entity.createdAt)
        assertEquals(encounterModel.isSeen, entity.isSeen)
        assertEquals(encounterModel.userMet.id, entity.userMetId)
        assertEquals(encounterModel.userMet.email, entity.userMetEmail)
        assertEquals(encounterModel.userMet.username, entity.userMetUsername)

        assertEquals(encounterModel.id, resultModel.id)
        assertEquals(encounterModel.createdAt, resultModel.createdAt)
        assertEquals(encounterModel.isSeen, resultModel.isSeen)
        assertEquals(encounterModel.userMet.id, resultModel.userMet.id)
        assertEquals(encounterModel.userMet.email, resultModel.userMet.email)
        assertEquals(encounterModel.userMet.username, resultModel.userMet.username)
    }

    @Test
    fun `test EncounterModel toShortenedEncounter`() {
        // Given
        val userMet = UserMetModel(
            id = "1",
            email = "user@example.com",
            username = "testUser"
        )
        val encounterModel = EncounterModel(
            id = "1",
            createdAt = "2024-03-20T10:00:00.000Z",
            isSeen = false,
            userMet = userMet
        )

        // When
        val result = encounterModel.toShortenedEncounter()

        // Then
        assertEquals("testUser", result.userEncounteredName1)
        assertEquals(EncounterStatus.UNSEEN, result.status)
        assertEquals("2024-03-20T10:00:00.000Z", result.happenedAt)
        assertEquals("https://example.com/profiles/1/profile.jpg", result.profilePic)
    }

    @Test
    fun `test mapLoginDtoToLoginModel`() {
        // Given
        val loginDto = LoginResponseDTO(
            token = "test-token"
        )

        // When
        val result = mapLoginDtoToLoginModel(loginDto)

        // Then
        assertEquals("test-token", result.token)
    }

    @Test
    fun `test mapSignupDtoToSignupModel success case`() {
        // Given
        val signupDto = SignupResponseDTO(
            id = "1",
            email = "test@example.com",
            username = "testUser"
        )
        val email = "test@example.com"
        val username = "testUser"

        // When
        val result = mapSignupDtoToSignupModel(signupDto, email, username)

        // Then
        assertTrue(result.isSignupSuccess)
    }

    @Test
    fun `test mapSignupDtoToSignupModel failure case`() {
        // Given
        val signupDto = SignupResponseDTO(
            id = "1",
            email = "wrong@example.com",
            username = "wrongUser"
        )
        val email = "test@example.com"
        val username = "testUser"

        // When
        val result = mapSignupDtoToSignupModel(signupDto, email, username)

        // Then
        assertFalse(result.isSignupSuccess)
    }

    @Test
    fun `test mapPassionCategoryDtoToPassionCategoryModel`() {
        // Given
        val passionDtos = listOf(
            PassionDto(
                id = "1",
                name = "Sport",
                description = "Physical activities",
                picture = "sport.jpg",
                type = "Activity"
            ),
            PassionDto(
                id = "2",
                name = "Reading",
                description = "Books and literature",
                picture = "reading.jpg",
                type = "Activity"
            )
        )

        // When
        val result = mapPassionCategoryDtoToPassionCategoryModel(passionDtos)

        // Then
        assertEquals(1, result.size)
        assertEquals("Activity", result[0].name)
        assertEquals(2, result[0].items.size)

        val firstItem = result[0].items[0]
        assertEquals("1", firstItem.id)
        assertEquals("Sport", firstItem.name)
        assertEquals("sport.jpg", firstItem.image)
        assertFalse(firstItem.isSelected)

        val secondItem = result[0].items[1]
        assertEquals("2", secondItem.id)
        assertEquals("Reading", secondItem.name)
        assertEquals("reading.jpg", secondItem.image)
        assertFalse(secondItem.isSelected)
    }
} 