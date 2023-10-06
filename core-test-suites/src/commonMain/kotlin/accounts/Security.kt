package opensavvy.notes.core.suites.accounts

import opensavvy.notes.core.Account
import opensavvy.notes.core.RequiresAuthentication.InvalidAuthentication
import opensavvy.notes.core.RequiresAuthorization.Unauthorized
import opensavvy.notes.core.executeAs
import opensavvy.notes.core.suites.utils.shouldBeSuccessful
import opensavvy.notes.core.suites.utils.shouldFailWith
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.prepared

internal fun <AccountRef : Account.Ref> SuiteDsl.accountSecurity(prepareAccounts: Prepared<Account.Service<AccountRef>>) = suite("Security") {
	val prepareEmail by prepared { createAccountEmail }
	val preparePassword by prepared { createAccountPassword }

	val prepareAccount by prepared {
		prepareAccounts().createDefault(email = prepareEmail(), password = preparePassword())
	}

	suite("Log in") {
		test("It is possible to log in as a created user") {
			val accounts = prepareAccounts()
			prepareAccount()

			accounts.logIn(prepareEmail(), preparePassword()).shouldBeSuccessful()
		}

		test("It is not possible to log in with an incorrect password") {
			val accounts = prepareAccounts()
			prepareAccount()

			accounts.logIn(prepareEmail(), "not the correct email") shouldFailWith Account.Failures.InvalidCredentials
		}

		test("It is not possible to log in with an incorrect email") {
			val accounts = prepareAccounts()
			prepareAccount()

			accounts.logIn("another-email", preparePassword()) shouldFailWith Account.Failures.InvalidCredentials
		}
	}

	suite("Log out") {
		test("After logging out, all actions are forbidden") {
			prepareAccount()
			val account = prepareAccounts().logIn(prepareEmail(), preparePassword()).shouldBeSuccessful()

			executeAs(account) {
				account.logOut()
				account.edit(fullName = "Another name") shouldFailWith InvalidAuthentication
			}
		}

		test("Cannot log out someone else") {
			prepareAccount()
			val account = prepareAccounts().logIn(prepareEmail(), preparePassword()).shouldBeSuccessful()

			val prepareAttacker by prepareAccounts.create()
			val attacker = prepareAttacker()

			executeAs(attacker) {
				account.logOut()
			}

			executeAs(account) {
				account.edit(fullName = "Another name").shouldBeSuccessful()
			}
		}

		test("Logging out doesn't impact the other sessions") {
			prepareAccount()
			val session1 = prepareAccounts().logIn(prepareEmail(), preparePassword()).shouldBeSuccessful()
			val session2 = prepareAccounts().logIn(prepareEmail(), preparePassword()).shouldBeSuccessful()

			executeAs(session1) {
				session1.logOut()
			}

			executeAs(session2) {
				session2.edit(fullName = "Another name").shouldBeSuccessful()
			}
		}
	}

	suite("Password modification") {
		test("Users can edit their password then log in with the new password") {
			prepareAccount()
			val initialPassword = preparePassword()
			val account = prepareAccounts().logIn(prepareEmail(), initialPassword).shouldBeSuccessful()

			executeAs(account) {
				account.editPassword(initialPassword, "A new password").shouldBeSuccessful()
			}

			prepareAccounts().logIn(prepareEmail(), "A new password").shouldBeSuccessful()
			prepareAccounts().logIn(prepareEmail(), initialPassword) shouldFailWith Account.Failures.InvalidCredentials
		}

		test("After editing the password, the current session is invalidated") {
			prepareAccount()
			val initialPassword = preparePassword()
			val account = prepareAccounts().logIn(prepareEmail(), initialPassword).shouldBeSuccessful()

			executeAs(account) {
				account.editPassword(initialPassword, "A new password").shouldBeSuccessful()
				account.edit(fullName = "test") shouldFailWith InvalidAuthentication
			}
		}

		test("After editing the password, other sessions are invalidated") {
			prepareAccount()
			val initialPassword = preparePassword()
			val session1 = prepareAccounts().logIn(prepareEmail(), initialPassword).shouldBeSuccessful()
			val session2 = prepareAccounts().logIn(prepareEmail(), initialPassword).shouldBeSuccessful()

			executeAs(session1) {
				session1.editPassword(initialPassword, "A new password").shouldBeSuccessful()
			}

			executeAs(session2) {
				session2.edit(fullName = "test") shouldFailWith InvalidAuthentication
			}
		}

		test("Users cannot edit someone else's password") {
			prepareAccount()
			val initialPassword = preparePassword()
			val account = prepareAccounts().logIn(prepareEmail(), initialPassword).shouldBeSuccessful()

			val prepareAttacker by prepareAccounts.create()
			val attacker = prepareAttacker()

			executeAs(attacker) {
				account.editPassword(initialPassword, "A new password") shouldFailWith Unauthorized
			}

			executeAs(account) {
				account.edit(fullName = "Another name").shouldBeSuccessful()
			}
		}
	}
}
