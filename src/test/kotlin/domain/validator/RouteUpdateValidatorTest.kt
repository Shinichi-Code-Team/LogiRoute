package domain.validator

import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validator.RouteUpdateValidator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RouteUpdateValidatorTest {

    private val validator = RouteUpdateValidator()

    @Test
    fun `valid route update passes`() {
        val input = RouteUpdateValidator.inputValidator(
            id = "RT-12345",
            update = UpdateRouteInput(distanceKm = 10.0)
        )
        val result = validator.validate(input)
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `invalid route update fields return errors`() {
        val cases = listOf(
            UpdateRouteInput(originHubId = "bad-id") to ValidationError(
                ValidationField.ORIGIN_HUB_ID,
                ValidationReason.INVALID_FORMAT
            ),
            UpdateRouteInput(destinationHubId = "bad-id") to ValidationError(
                ValidationField.DESTINATION_HUB_ID,
                ValidationReason.INVALID_FORMAT
            ),
            UpdateRouteInput(distanceKm = 0.0) to ValidationError(
                ValidationField.DISTANCE_KM,
                ValidationReason.MUST_BE_POSITIVE
            ),
            UpdateRouteInput(typicalDelayMin = -1) to ValidationError(
                ValidationField.TYPICAL_DELAY_MIN,
                ValidationReason.MUST_BE_NON_NEGATIVE
            )
        )
        cases.forEach { (update, expectedError) ->
            val input = RouteUpdateValidator.inputValidator(
                id = "RT-12345",
                update = update
            )

            assertEquals(
                ValidationResult.Invalid(listOf(expectedError)),
                validator.validate(input)
            )
        }
    }

    @Test
    fun `invalid route id returns error`() {
        // Given
        val input = RouteUpdateValidator.inputValidator(
            id = "wrong-id",
            update = UpdateRouteInput(distanceKm = 10.0)
        )

        // When
        val result = validator.validate(input)

        // Then
        assertEquals(
            ValidationResult.Invalid(
                listOf(
                    ValidationError(
                        ValidationField.ID,
                        ValidationReason.INVALID_FORMAT
                    )
                )
            ),
            result
        )
    }

    @Test
    fun `empty route update returns no fields error`() {
        val input = RouteUpdateValidator.inputValidator(
            id = "RT-12345",
            update = UpdateRouteInput()
        )
        val result = validator.validate(input)
        assertEquals(
            ValidationResult.Invalid(
                listOf(
                    ValidationError(
                        ValidationField.UPDATE_FIELDS,
                        ValidationReason.NO_FIELDS_PROVIDED
                    )
                )
            ),
            result
        )
    }
}