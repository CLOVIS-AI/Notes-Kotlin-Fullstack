package opensavvy.notes.core.suites

import opensavvy.notes.core.Account
import opensavvy.notes.core.suites.accounts.accountCreation
import opensavvy.notes.core.suites.accounts.accountEdition
import opensavvy.notes.core.suites.accounts.accountSecurity
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.SuiteDsl

fun <AccountRef : Account.Ref> SuiteDsl.accountSuite(accounts: Prepared<Account.Service<AccountRef>>) {
	accountCreation(accounts)
	accountSecurity(accounts)
	accountEdition(accounts)
}
