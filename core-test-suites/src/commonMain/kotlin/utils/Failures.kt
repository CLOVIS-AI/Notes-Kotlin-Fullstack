package opensavvy.notes.core.suites.utils

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import opensavvy.backbone.Ref
import opensavvy.backbone.now
import opensavvy.state.outcome.Outcome
import opensavvy.state.outcome.failed

fun <Failure, Value> Outcome<Failure, Value>.shouldBeSuccessful(): Value {
	withClue("Expecting to be successful: $this") {
		this::class shouldBe Outcome.Success::class
	}

	return (this as Outcome.Success).value
}

fun <Failure, Value> Outcome<Failure, Value>.shouldBeSuccessfulAnd(block: (Value) -> Unit): Value {
	val value = shouldBeSuccessful()

	assertSoftly {
		block(value)
	}

	return value
}

infix fun <Failure, Value> Outcome<Failure, Value>.shouldFailWith(failure: Failure) {
	this shouldBe failure.failed()
}

/**
 * Accesses and returns the value between a [Ref].
 *
 * If the request fails, an assertion is thrown.
 */
suspend fun <Failure, Value> Ref<Failure, Value>.get(): Value {
	return withClue("Requesting $this") {
		val result = now()

		withClue("The result is $result") {
			result::class shouldBe Outcome.Success::class
		}

		(result as Outcome.Success).value
	}
}

suspend fun <Failure, Value> Ref<Failure, Value>.check(block: (Value) -> Unit): Value {
	val value = get()

	assertSoftly {
		block(value)
	}

	return value
}
