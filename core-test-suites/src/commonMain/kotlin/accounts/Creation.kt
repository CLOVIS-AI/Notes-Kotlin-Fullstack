package opensavvy.notes.core.suites.accounts

import io.kotest.matchers.shouldBe
import opensavvy.backbone.now
import opensavvy.notes.core.Account
import opensavvy.notes.core.executeAs
import opensavvy.notes.core.suites.utils.shouldBeSuccessful
import opensavvy.notes.core.suites.utils.shouldBeSuccessfulAnd
import opensavvy.notes.core.suites.utils.shouldFailWith
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.SuiteDsl

internal suspend fun Account.Service<*>.createDefault(
	fullName: String = createAccountName,
	email: String = createAccountEmail,
	password: String = createAccountPassword,
) = create(fullName, email, password)

internal fun <AccountRef : Account.Ref> SuiteDsl.accountCreation(prepareAccounts: Prepared<Account.Service<AccountRef>>) = suite("Account creation") {
	suite("Password") {
		val passwords = listOf("", "1234", "123456")

		for (password in passwords) {
			test("The password '$password' is too simple") {
				val accounts = prepareAccounts()

				accounts.create(createAccountName, createAccountEmail, password) shouldFailWith Account.Failures.PasswordTooSimple
			}
		}
	}

	suite("Email") {
		test("The email 'test' is invalid") {
			val accounts = prepareAccounts()

			accounts.create(createAccountName, "test", createAccountPassword) shouldFailWith Account.Failures.InvalidEmailAddress
		}

		test("Cannot create an account with an already used email") {
			val accounts = prepareAccounts()
			val email = createAccountEmail

			accounts.create(createAccountName, email, createAccountPassword).shouldBeSuccessful()
			accounts.create(createAccountName, email, createAccountPassword) shouldFailWith Account.Failures.InvalidEmailAddress
		}

		test("The created user has the correct email") {
			val email = createAccountEmail
			val prepareAccount by prepareAccounts.create(email = email)
			val account = prepareAccount()

			executeAs(account) {
				account.now().shouldBeSuccessfulAnd {
					it.email shouldBe email
				}
			}
		}
	}

	suite("Full name") {
		val names = listOf("", "A short name", "A longer name")

		for (name in names) {
			val prepareAccount by prepareAccounts.create(fullName = name)

			test("The name '$name' is allowed") {
				val accounts = prepareAccounts()

				accounts.create(name, createAccountEmail, createAccountPassword).shouldBeSuccessful()
			}

			test("The created user has the correct name") {
				val account = prepareAccount()

				executeAs(account) {
					account.now().shouldBeSuccessfulAnd {
						it.fullName shouldBe name
					}
				}
			}
		}
	}
}
