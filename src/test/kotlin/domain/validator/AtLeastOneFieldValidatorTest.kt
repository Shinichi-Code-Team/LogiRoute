package domain.validator

import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason
import com.example.logiroute.domain.validator.AtLeastOneFieldValidator

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AtLeastOneFieldValidatorTest {

    private val validator = AtLeastOneFieldValidator()

    @Test
    fun `all null values return no fields error`() {
        val values = mapOf(
            "name" to null,
            "weight" to null
        )
        val result = validator.validate(values)
        assertEquals(
            ValidationError(
                ValidationField.UPDATE_FIELDS,
                ValidationReason.NO_FIELDS_PROVIDED
            ),
            result
        )
    }

    @Test
    fun `at least one non null value passes`() {
        val values = mapOf(
            "name" to null,
            "weight" to 10.0
        )
        val result = validator.validate(values)
        assertNull(result)
    }
}