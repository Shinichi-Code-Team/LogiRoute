package validator

import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationField
import com.example.logiroute.domain.validator.ValidationReason
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.VehicleUpdateValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VehicleUpdateValidatorTest {

    private val validator = VehicleUpdateValidator()

    @Test
    fun `valid vehicle update passes`() {
        val input = VehicleUpdateValidator.Input(
            id = "TRK-1234",
            update = UpdateVehicleInput(maxCapacityKg = 500.0)
        )
        val result = validator.validate(input)
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `invalid vehicle update fields return errors`() {
        val cases = listOf(
            UpdateVehicleInput(maxCapacityKg = 0.0) to ValidationError(
                ValidationField.MAX_CAPACITY_KG,
                ValidationReason.MUST_BE_POSITIVE
            ),
            UpdateVehicleInput(costPerKm = 0.0) to ValidationError(
                ValidationField.COST_PER_KM,
                ValidationReason.MUST_BE_POSITIVE
            ),
            UpdateVehicleInput(currentHubId = "bad-id") to ValidationError(
                ValidationField.CURRENT_HUB_ID,
                ValidationReason.INVALID_FORMAT
            )
        )
        cases.forEach { (update, expectedError) ->
            val input = VehicleUpdateValidator.Input(
                id = "TRK-1234",
                update = update
            )

            assertEquals(
                ValidationResult.Invalid(listOf(expectedError)),
                validator.validate(input)
            )
        }
    }

    @Test
    fun `invalid vehicle id returns error`() {
        val input = VehicleUpdateValidator.Input(
            id = "wrong-id",
            update = UpdateVehicleInput(maxCapacityKg = 500.0)
        )

        val result = validator.validate(input)
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
    fun `empty vehicle update returns no fields error`() {
        val input = VehicleUpdateValidator.Input(
            id = "TRK-1234",
            update = UpdateVehicleInput()
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