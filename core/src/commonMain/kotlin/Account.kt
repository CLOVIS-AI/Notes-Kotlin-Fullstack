package opensavvy.notes.core

import opensavvy.backbone.Backbone
import opensavvy.state.outcome.Outcome

/**
 * Accounts are the technical information allowing a user to interact with the application.
 *
 * Users can have multiple accounts.
 */
data class Account(
	/**
	 * The name displayed when viewing this account.
	 *
	 * This name may contain spaces and other special characters.
	 */
	val fullName: String,
	/**
	 * The email address used by the user to prove their ownership of this account.
	 *
	 * Each backend implementation may use its own validation algorithm to check that an email address is valid.
	 * The only cross-implementation guarantee is that an email address contains at least one '@' character.
	 */
	val email: String,
) {

	/**
	 * A reference to an [Account], abstracting away the changes made to accounts over time.
	 *
	 * To learn more about this pattern, see [opensavvy.backbone.Ref].
	 */
	interface Ref : opensavvy.backbone.Ref<Failures.Get, Account> {

		/**
		 * Edits the account's information.
		 *
		 * When a value is `null`, it is not modified.
		 *
		 * @param fullName See [Account.fullName]
		 * @param email See [Account.email]
		 */
		suspend fun edit(fullName: String? = null, email: String? = null): Outcome<Failures.Edit, Unit>

		/**
		 * Edits the account's password.
		 *
		 * To be able to edit a password, a user must be authenticated *and* know their previous password.
		 *
		 * Editing the password will force all devices using this account to log out.
		 *
		 * @param [oldPassword] The current password
		 * @param newPassword The new password, if the operation is successful
		 */
		suspend fun editPassword(oldPassword: String, newPassword: String): Outcome<Failures.EditPassword, Unit>

		/**
		 * Invalidates the access token generated when [logIn][Service.logIn] was called.
		 *
		 * Because this invalidates the access token, all future operations executed with this account reference
		 * will fail with [RequiresAuthentication.InvalidAuthentication].
		 */
		suspend fun logOut(): Outcome<Nothing, Unit>

	}

	interface Service<R : Ref> : Backbone<R, Failures.Get, Account> {

		/**
		 * Creates a new [Account].
		 *
		 * The account is not necessarily created immediately.
		 * For example, a backend may send a validation email to the provided [email] before allowing log in attempts.
		 *
		 * @param fullName See [Account.fullName]
		 * @param email See [Account.email]
		 * @param password The password the user will use to [logIn]
		 */
		suspend fun create(fullName: String, email: String, password: String): Outcome<Failures.Create, Unit>

		/**
		 * Generates an access token for the user with the email address [email].
		 *
		 * The caller is not given access to the access token.
		 * For example, on the web platform, the access token is managed directly by the browser, and is not available
		 * to scripts executing on the page, for security reasons.
		 *
		 * However, the page may opt to destroy the access token by calling [logOut][Ref.logOut] on the returned [Ref].
		 */
		suspend fun logIn(email: String, password: String): Outcome<Failures.LogIn, R>

	}

	sealed interface Failures {
		/** Failures of [Ref.request]. */
		sealed interface Get : Failures

		/** Failures of [Service.create]. */
		sealed interface Create : Failures

		/** Failures of [Ref.edit]. */
		sealed interface Edit : Failures

		/** Failures of [Ref.editPassword]. */
		sealed interface EditPassword : Failures

		/** Failures of [Service.logIn]. */
		sealed interface LogIn : Failures

		data object InvalidEmailAddress : Create, Edit
		data object PasswordTooSimple : Create, EditPassword
		data object InvalidCredentials : LogIn, EditPassword
	}
}
