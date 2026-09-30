package domain.validator

import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validator.WarehouseUpdateValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WarehouseUpdateValidatorTest {
    private val validator = WarehouseUpdateValidator()
    @Test
    fun `valid warehouse update passes`() {
        val input = WarehouseUpdateValidator.inputValidator(
            id = "WH-123",
            update = UpdateWarehouseInput(name = "Amman")
        )
        val result = validator.validate(input)
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `invalid warehouse update fields return errors`() {
        val cases = listOf(
            UpdateWarehouseInput(name = " ") to ValidationError(
                ValidationField.NAME,
                ValidationReason.REQUIRED
            ),
            UpdateWarehouseInput(regionalZone = " ") to ValidationError(
                ValidationField.REGIONAL_ZONE,
                ValidationReason.REQUIRED
            ),
            UpdateWarehouseInput(latitude = 91.0) to ValidationError(
                ValidationField.LATITUDE,
                ValidationReason.OUT_OF_RANGE
            ),
            UpdateWarehouseInput(longitude = 181.0) to ValidationError(
                ValidationField.LONGITUDE,
                ValidationReason.OUT_OF_RANGE
            )
        )
        cases.forEach { (update, expectedError) ->
            val input = WarehouseUpdateValidator.inputValidator(
                id = "WH-123",
                update = update
            )

            assertEquals(
                ValidationResult.Invalid(listOf(expectedError)),
                validator.validate(input)
            )
        }
    }

    @Test
    fun `invalid warehouse id returns error`() {
        val input = WarehouseUpdateValidator.inputValidator(
            id = "wrong-id",
            update = UpdateWarehouseInput(name = "Amman")
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
    fun `empty warehouse update returns no fields error`() {
        val input = WarehouseUpdateValidator.inputValidator(
            id = "WH-123",
            update = UpdateWarehouseInput()
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