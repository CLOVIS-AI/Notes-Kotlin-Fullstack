package opensavvy.notes.core.suites.accounts

import opensavvy.notes.core.Account
import opensavvy.notes.core.suites.utils.shouldBeSuccessful
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.PreparedProvider
import opensavvy.prepared.suite.prepared
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Creates a valid [Account.fullName].
 */
val createAccountName get() = "A test user #${Random.nextUInt()}"

/**
 * Creates a valid [Account.email].
 */
val createAccountEmail get() = "test-${Random.nextUInt()}@gmail.com"

/**
 * Creates a valid account password.
 */
val createAccountPassword get() = "a-str0ng-p4ssw0rd-${Random.nextUInt()}"

/**
 * Creates and logs in to a valid test account.
 */
fun <AccountRef : Account.Ref> Prepared<Account.Service<AccountRef>>.create(
	fullName: String = createAccountName,
	email: String = createAccountEmail,
	password: String = createAccountPassword,
): PreparedProvider<AccountRef> = prepared {
	val accounts = this@create()

	accounts.create(fullName = fullName, email = email, password = password).shouldBeSuccessful()
	accounts.logIn(email, password).shouldBeSuccessful()
}
