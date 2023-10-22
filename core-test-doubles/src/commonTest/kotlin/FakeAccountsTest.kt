package opensavvy.notes.core.doubles

import io.kotest.core.spec.style.StringSpec
import opensavvy.notes.core.suites.accountSuite
import opensavvy.prepared.runner.kotest.preparedSuite
import opensavvy.prepared.suite.prepared

class FakeAccountsTest : StringSpec({
	preparedSuite {
		val fakeAccounts by prepared { FakeAccounts() }

		accountSuite(
			fakeAccounts,
		)
	}
})
