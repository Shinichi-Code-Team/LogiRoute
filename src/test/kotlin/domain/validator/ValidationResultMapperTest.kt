package domain.validator

import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ValidationResultMapperTest {

    private val mapper = ValidationResultMapper()

    @Test
    fun `should return valid when there are no errors`() {
        val result = mapper.map(emptyList())

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should preserve all validation errors`() {
        val errors = listOf(
            ValidationError(
                ValidationField.ID,
                ValidationReason.INVALID_FORMAT
            ),
            ValidationError(
                ValidationField.WEIGHT,
                ValidationReason.MUST_BE_POSITIVE
            )
        )

        val result = mapper.map(errors)

        val invalid = assertIs<ValidationResult.Invalid>(result)
        assertEquals(errors, invalid.errors)
    }

    @Test
    fun `should preserve errors when original list changes`() {
        val error = ValidationError(
            ValidationField.ID,
            ValidationReason.REQUIRED
        )
        val errors = mutableListOf(error)

        val result = mapper.map(errors)
        errors.clear()

        val invalid = assertIs<ValidationResult.Invalid>(result)
        assertEquals(listOf(error), invalid.errors)
    }
}