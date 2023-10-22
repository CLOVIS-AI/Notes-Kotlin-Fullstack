package opensavvy.notes.core.suites.accounts

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import opensavvy.notes.core.Account
import opensavvy.notes.core.RequiresAuthorization.Unauthorized
import opensavvy.notes.core.executeAs
import opensavvy.notes.core.suites.utils.check
import opensavvy.notes.core.suites.utils.get
import opensavvy.notes.core.suites.utils.shouldBeSuccessful
import opensavvy.notes.core.suites.utils.shouldFailWith
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.SuiteDsl

internal fun <AccountRef : Account.Ref> SuiteDsl.accountEdition(prepareAccounts: Prepared<Account.Service<AccountRef>>) = suite("Account edition") {
	val prepareAccount by prepareAccounts.create()
	val prepareAttacker by prepareAccounts.create()

	suite("Full name") {
		test("Users can edit their name") {
			val account = prepareAccount()

			executeAs(account) {
				account.edit(fullName = "My new name").shouldBeSuccessful()

				account.check {
					it.fullName shouldBe "My new name"
				}
			}
		}

		test("Users cannot edit someone else's name") {
			val account = prepareAccount()

			val initialName = executeAs(account) {
				account.get().fullName
			}

			val attacker = prepareAttacker()

			executeAs(attacker) {
				account.edit(fullName = "Another name") shouldFailWith Unauthorized
			}

			executeAs(account) {
				account.check {
					it.fullName shouldBe initialName
				}
			}
		}
	}

	suite("Email") {
		test("Users can edit their email") {
			val account = prepareAccount()
			val email = createAccountEmail

			executeAs(account) {
				account.edit(email = email).shouldBeSuccessful()

				account.check {
					it.email shouldBe email
				}
			}
		}

		test("Users can make other actions after changing their email (it didn't break everything)") {
			val account = prepareAccount()
			val email = createAccountEmail

			executeAs(account) {
				account.edit(email = email).shouldBeSuccessful()
				account.edit(fullName = "My new name").shouldBeSuccessful()
				account.check {
					it.email shouldBe email
					it.fullName shouldBe "My new name"
				}
			}
		}

		test("Users cannot reuse an already existing email") {
			val account = prepareAccount()
			val email = executeAs(account) {
				account.get().email
			}

			val attacker = prepareAttacker()

			executeAs(attacker) {
				attacker.edit(email = email) shouldFailWith Account.Failures.InvalidEmailAddress
				attacker.check {
					it.email shouldNotBe email
				}
			}

			executeAs(account) {
				account.check {
					it.email shouldBe email
				}
			}
		}

		test("Users cannot edit someone else's email") {
			val account = prepareAccount()

			val initialEmail = executeAs(account) {
				account.get().email
			}

			val attacker = prepareAttacker()

			executeAs(attacker) {
				account.edit(email = createAccountEmail) shouldFailWith Unauthorized
			}

			executeAs(account) {
				account.check {
					it.email shouldBe initialEmail
				}
			}
		}
	}
}
