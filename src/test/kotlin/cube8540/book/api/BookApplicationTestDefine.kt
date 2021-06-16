package cube8540.book.api

import io.github.cube8540.validator.core.ValidationError
import io.github.cube8540.validator.core.ValidationResult
import kotlin.reflect.jvm.isAccessible

fun createValidationResults(
    vararg errors: ValidationError?
): ValidationResult {
    val constructor = ValidationResult::class.constructors.first()
    constructor.isAccessible = true

    val validationResultInstance = constructor.call()

    errors.let { it.forEach { error -> validationResultInstance.registerError(error) } }

    return validationResultInstance
}