package opensavvy.notes.core.doubles

import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import opensavvy.notes.core.Account
import opensavvy.notes.core.Account.Failures
import opensavvy.notes.core.CommonFailures
import opensavvy.notes.core.RequiresAuthentication.InvalidAuthentication
import opensavvy.notes.core.RequiresAuthorization.Unauthorized
import opensavvy.notes.core.ResourceAccessFailure.NotFound
import opensavvy.notes.core.currentAccount
import opensavvy.progress.done
import opensavvy.state.arrow.out
import opensavvy.state.coroutines.ProgressiveFlow
import opensavvy.state.outcome.Outcome
import opensavvy.state.outcome.onFailure
import opensavvy.state.progressive.withProgress
import kotlin.random.Random
import kotlin.random.nextUInt

class FakeAccounts : Account.Service<FakeAccounts.FakeAccountRef> {

	// The key is the ID
	private val data = HashMap<UInt, Account>()
	private val passwords = HashMap<UInt, String>()
	private val tokens = HashMap<UInt, MutableSet<Int>>()
	private val lock = Mutex()

	// Unsafe: you should hold the lock
	private fun findByEmail(email: String) = data.asSequence().find { it.value.email == email }

	private fun checkPassword(password: String): Outcome<Failures.PasswordTooSimple, Unit> = out {
		ensure(password.isNotBlank()) { Failures.PasswordTooSimple }

		var increment = ""
		for (i in 1..30) {
			increment += i.toString()
			ensure(password != increment) { Failures.PasswordTooSimple }
		}
	}

	private fun checkEmail(email: String): Outcome<Failures.InvalidEmailAddress, Unit> = out {
		ensure('@' in email) { Failures.InvalidEmailAddress }
	}

	override suspend fun create(fullName: String, email: String, password: String): Outcome<Failures.Create, Unit> = out {
		checkPassword(password).bind()
		checkEmail(email).bind()

		lock.withLock("create($fullName, $email)") {
			ensure(findByEmail(email) == null) { Failures.InvalidEmailAddress }
			val id = Random.nextUInt()
			check(!data.containsKey(id)) { "Unlucky! We generated a test ID that already exists: $id" }
			data[id] = Account(fullName, email)
			passwords[id] = password
		}
	}

	override suspend fun logIn(email: String, password: String): Outcome<Failures.LogIn, FakeAccountRef> = out {
		lock.withLock("logIn($email)") {
			val account = findByEmail(email)
			ensureNotNull(account) { Failures.InvalidCredentials }

			val (id, _) = account

			ensure(passwords.containsKey(id)) { Failures.InvalidCredentials }
			ensure(passwords[id] == password) { Failures.InvalidCredentials }

			val token = Random.nextInt()
			tokens.getOrPut(id, ::HashSet).add(token)

			FakeAccountRef(id, email, token)
		}
	}

	inner class FakeAccountRef(
		private val id: UInt,
		private val email: String,
		private val token: Int,
	) : Account.Ref {
		private suspend fun isValid(): Boolean = lock.withLock("${this@FakeAccountRef}.isValid()") {
			val myTokens = tokens[id]
			return myTokens != null && token in myTokens
		}

		override fun request(): ProgressiveFlow<Failures.Get, Account> = flow {
			out {
				lock.withLock("${this@FakeAccountRef}.request($email)") {
					val result = data[id]
					ensureNotNull(result) { NotFound }
					result
				}
			}.withProgress(done())
				.also { emit(it) }
		}

		override suspend fun edit(fullName: String?, email: String?): Outcome<Failures.Edit, Unit> = out {
			ensure(currentAccount() == this@FakeAccountRef) { Unauthorized }
			ensure(isValid()) { InvalidAuthentication }

			lock.withLock("${this@FakeAccountRef}.edit($fullName, $email)") {
				var result = data[id]
					?: raise(CommonFailures.UnknownError("The account $id doesn't seem to exist"))

				if (fullName != null)
					result = result.copy(fullName = fullName)

				if (email != null) {
					checkEmail(email).bind()
					ensure(data.none { it.value.email == email }) { Failures.InvalidEmailAddress }
					result = result.copy(email = email)
				}

				data[id] = result
			}
		}

		override suspend fun editPassword(oldPassword: String, newPassword: String): Outcome<Failures.EditPassword, Unit> = out {
			ensure(currentAccount() == this@FakeAccountRef) { Unauthorized }
			ensure(isValid()) { InvalidAuthentication }
			checkPassword(newPassword).bind()

			// Use log in to check if the old password is correct
			logIn(email, oldPassword).onFailure {
				when (it) {
					is CommonFailures -> raise(it)
					is Failures.InvalidCredentials -> raise(it)
				}
			}

			lock.withLock("${this@FakeAccountRef}.editPassword($oldPassword, $newPassword)") {
				passwords[id] = newPassword
				tokens[id]?.clear()
			}
		}

		override suspend fun logOut() {
			if (currentAccount() != this) return
			if (!isValid()) return

			lock.withLock("$this.logOut()") {
				tokens[id]?.remove(token)
			}
		}

		// region Identity

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is FakeAccountRef) return false

			if (id != other.id) return false
			if (email != other.email) return false
			if (token != other.token) return false

			return true
		}

		override fun hashCode(): Int {
			var result = id.hashCode()
			result = 31 * result + email.hashCode()
			result = 31 * result + token
			return result
		}

		override fun toString() = "FakeAccountRef(for=$email, id=$id, session=$token)"

		// endregion
	}
}
