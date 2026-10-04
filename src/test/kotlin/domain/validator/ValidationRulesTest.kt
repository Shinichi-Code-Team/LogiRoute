package domain.validator

import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validation.ValidationRules

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidationRulesTest {
    private val validationRules = ValidationRules()
    @Test
    fun `valid package id passes`() {
        val id = "PKG-123456"
        val result = validationRules.validatePackageId(id)
        assertNull(result)
    }
    @Test
    fun `malformed package id returns invalid format`() {
        val id = "PKG-123"
  val result = validationRules.validatePackageId(id)
        assertEquals(
            ValidationError(
                ValidationField.ID,
                ValidationReason.INVALID_FORMAT
            ),
            result
        )
    }

    @Test
    fun `missing package id returns required error`() {
        val id: String? = null
        val result = validationRules.validatePackageId(id)
        assertEquals(
            ValidationError(
                ValidationField.ID,
                ValidationReason.REQUIRED
            ),
            result
        )
    }

    @Test
    fun `zero positive double returns must be positive error`() {
        val value = 0.0
        val result = validationRules.validatePositiveDouble(
            value = value,
            field = ValidationField.WEIGHT
        )
        assertEquals(
            ValidationError(
                ValidationField.WEIGHT,
                ValidationReason.MUST_BE_POSITIVE
            ),
            result
        )
    }

    @Test
    fun `latitude outside range returns out of range error`() {
        val latitude = 91.0
        val result = validationRules.validateLatitude(latitude)
        assertEquals(
            ValidationError(
                ValidationField.LATITUDE,
                ValidationReason.OUT_OF_RANGE
            ),
            result
        )
    }
}