package opensavvy.notes.core

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Implicit information about the user currently running the code.
 *
 * A common source of security vulnerabilities in multi-user applications is privilege escalation: a user becomes
 * able to execute an operation, or gain access to a system they shouldn't have been able to. This usually happens
 * because authorization happens in a specific part of the process ("firewall-style"), and some function inside the
 * "protected" area accidentally calls another "protected" function which assumes correct authorization has already been
 * checked.
 *
 * To avoid this scenario, we purposefully embed authentication information in all layers of the application.
 * This ensures all functions are able to check autonomously whether they should be called or not.
 *
 * Authentication information is stored in the [CoroutineContext] to be available in all suspending functions.
 * To change the current user, use [executeAs] or [executeAsGuest].
 *
 * To access the current authentication, use [currentAuth] or [currentAccount].
 */
sealed class Auth : AbstractCoroutineContextElement(Key) {

	/**
	 * The function has been called by [account].
	 */
	data class Authenticated(val account: Account.Ref) : Auth() {

		override fun toString() = "Auth($account)"
	}

	/**
	 * The function has been called by a user who possesses no account, or who did not log in yet.
	 */
	data object Guest : Auth() {

		override fun toString() = "Auth.Guest"
	}

	/** [CoroutineContext.Key] for [Auth]. */
	object Key : CoroutineContext.Key<Auth>
}

/**
 * Executes [block] authenticated as [account].
 *
 * See [Auth] and [Auth.Authenticated].
 */
suspend inline fun <T> executeAs(account: Account.Ref, crossinline block: suspend () -> T): T =
	withContext(Auth.Authenticated(account)) {
		block()
	}

/**
 * Executes [block] authenticated as a guest.
 *
 * See [Auth] and [Auth.Guest].
 */
suspend inline fun <T> executeAsGuest(crossinline block: suspend () -> T): T =
	withContext(Auth.Guest) {
		block()
	}

/**
 * The [Auth.Authenticated.account], or `null` if it's a [Auth.Guest].
 */
val Auth.account: Account.Ref?
	get() = when (this) {
		is Auth.Authenticated -> account
		Auth.Guest -> null
	}

/**
 * Accesses the current [Auth], or returns `null` if none is available.
 *
 * @see currentAuth
 * @see currentAccount
 */
suspend fun currentAuthOrNull(): Auth? =
	currentCoroutineContext()[Auth.Key]

/**
 * Accesses the current [Auth], or fails with [IllegalStateException] if none is available.
 *
 * @see currentAuthOrNull
 * @see currentAccount
 */
suspend fun currentAuth(): Auth =
	currentAuthOrNull()
		?: error("The function attempted to check the current authentication, but couldn't find it. Call executeAs or executeAsGuest before calling this function to ensure it receives the correct credentials.")

/**
 * Accesses the current [Auth]'s [account][Auth.account].
 *
 * - If the user is authenticated, returns their account.
 * - If the user is not authenticated, returns `null`.
 * - If no authentication information is available, throws [IllegalStateException].
 */
suspend fun currentAccount(): Account.Ref? =
	currentAuth().account
