package org.ptss.support.core.controllers

import io.mockk.coEvery
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.ws.rs.core.HttpHeaders
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.core.facades.GeneralInformationFacade
import java.util.*

@QuarkusTest
class GeneralInformationControllerTest {

    private val generalInformationFacade: GeneralInformationFacade = mockk()

    @Test
    fun testGetAllGeneralInformation() {
        // Mock the response from the service layer
        val expectedResponse = PagedResult(
            data = listOf(GeneralInformationListItemResponse(UUID.randomUUID(), "Wat is PTSS?")),
            nextCursor = null,
            pageSize = 10,
            totalItems = 1,
            totalPages = 1
        )
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } returns expectedResponse

        // Add Cookie header with the access_token
        val accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MzY0Njc4ODMsImlhdCI6MTczNjQ2NjY4MywianRpIjoiNmMyOTg2OWItZmNkMy00NjA0LWEyZjMtMWQ4NzA1N2M3Y2JiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wdHNzLXN1cHBvcnQiLCJzdWIiOiIxM2ZhZDAyYS03OTJlLTRmNWMtYTI3NC02ODNiZTgyZTY3NjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2lkIjoiNzU4MzlhOTctNjNmZC00OTJmLTgxOGMtZTViZDU5ODg2YjIwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInNjb3BlIjoib3BlbmlkIHVzZXItZGV0YWlscyIsInVzZXJfaWQiOiIxM2ZhZDAyYS03OTJlLTRmNWMtYTI3NC02ODNiZTgyZTY3NjYiLCJncm91cF9pZCI6ImE1NWEzNWQzLTMwZjAtNDA4Yi1iMWQ0LWE2MDkxZWVjYzAzNiIsInJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1wdHNzLXN1cHBvcnQiLCJ1bWFfYXV0aG9yaXphdGlvbiJdLCJyb2xlIjoicGF0aWVudCIsImxhc3RfbmFtZSI6IkRlcnNqYW50IiwiZmlyc3RfbmFtZSI6IkZyYW5rIiwiaGFzX3BpbiI6ZmFsc2V9.xxoFGOVvy5AQKvkQOyvZ2s29SIRJOzrkPYjkZRtsWjk"

        // Make the API call and assert the response
        RestAssured.given()
            .cookie("access_token", accessToken)  // Adding access_token in Cookie
            .queryParam("cursor", "some-cursor")
            .queryParam("size", 10)
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .`when`().get("/general-information")
            .then()
            .statusCode(200)  // Expecting 200 OK
            .body("data[0].title", equalTo("Wat is PTSS?"))
            .body("totalItems", equalTo(1))
    }

    @Test
    fun testCreateGeneralInformation() {
        // Prepare the request data
        val createRequest = CreateGeneralInformationRequest(
            title = "Test Title",
            content = "Test Content"
        )

        val createdResponse = CreateGeneralInformationResponse(
            id = UUID.randomUUID(),
            title = createRequest.title,
            content = createRequest.content
        )

        // Mock the service layer response
        coEvery { generalInformationFacade.createGeneralInformation(createRequest) } returns createdResponse

        // Add the admin-specific access token to the cookie (ensure this is a valid admin token)
        val accessToken = "eyJleHAiOjE3MzY0Njc4ODMsImlhdCI6MTczNjQ2NjY4MywianRpIjoiNmMyOTg2OWItZmNkMy00NjA0LWEyZjMtMWQ4NzA1N2M3Y2JiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wdHNzLXN1cHBvcnQiLCJzdWIiOiIxM2ZhZDAyYS03OTJlLTRmNWMtYTI3NC02ODNiZTgyZTY3NjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2lkIjoiNzU4MzlhOTctNjNmZC00OTJmLTgxOGMtZTViZDU5ODg2YjIwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInNjb3BlIjoib3BlbmlkIHVzZXItZGV0YWlscyIsInVzZXJfaWQiOiIxM2ZhZDAyYS03OTJlLTRmNWMtYTI3NC02ODNiZTgyZTY3NjYiLCJncm91cF9pZCI6ImE1NWEzNWQzLTMwZjAtNDA4Yi1iMWQ0LWE2MDkxZWVjYzAzNiIsInJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1wdHNzLXN1cHBvcnQiLCJ1bWFfYXV0aG9yaXphdGlvbiJdLCJyb2xlIjoiYWRtaW4iLCJsYXN0X25hbWUiOiJEZXJzamFudCIsImZpcnN0X25hbWUiOiJGcmFuayIsImhhc19waW4iOmZhbHNlfQ.GdeD6gnPT1CvlSUJJGzRWZYfp86wFkd5Q6Ty5w48zS8"
        // Make the API call and assert the response
        RestAssured.given()
            .cookie("access_token", accessToken)  // Adding admin access_token in Cookie
            .contentType("application/json")
            .body(createRequest)
            .`when`().post("/general-information")
            .then()
            .statusCode(200)  // Expecting 200 OK for successful creation
            .body("id", equalTo(createdResponse.id.toString()))
            .body("title", equalTo(createdResponse.title))
            .body("content", equalTo(createdResponse.content))
    }
}