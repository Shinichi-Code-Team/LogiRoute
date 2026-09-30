package domain.validator

import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validator.PackageUpdateValidator
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PackageUpdateValidatorTest {

    private val validator = PackageUpdateValidator()

    @Test
    fun `valid package update passes`() {
        val input = PackageUpdateValidator.inputValidator(
            id = "PKG-123456",
            update = UpdatePackageInput(weight = 10.0)
        )
        val result = validator.validate(input)
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `invalid package update fields return errors`() {
        // Given
        val cases = listOf(
            UpdatePackageInput(weight = 0.0) to ValidationError(
                ValidationField.WEIGHT,
                ValidationReason.MUST_BE_POSITIVE
            ),
            UpdatePackageInput(originHubId = "bad-id") to ValidationError(
                ValidationField.ORIGIN_HUB_ID,
                ValidationReason.INVALID_FORMAT
            ),
            UpdatePackageInput(destinationHubId = "bad-id") to ValidationError(
                ValidationField.DESTINATION_HUB_ID,
                ValidationReason.INVALID_FORMAT
            )
        )
        cases.forEach { (update, expectedError) ->
            val input = PackageUpdateValidator.inputValidator(
                id = "PKG-123456",
                update = update
            )

            assertEquals(
                ValidationResult.Invalid(listOf(expectedError)),
                validator.validate(input)
            )
        }
    }

    @Test
    fun `invalid package id returns error`() {
        // Given
        val input = PackageUpdateValidator.inputValidator(
            id = "wrong-id",
            update = UpdatePackageInput(weight = 10.0)
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
    fun `empty package update returns no fields error`() {
        val input = PackageUpdateValidator.inputValidator(
            id = "PKG-123456",
            update = UpdatePackageInput()
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