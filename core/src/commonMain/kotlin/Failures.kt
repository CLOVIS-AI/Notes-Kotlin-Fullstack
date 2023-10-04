package opensavvy.notes.core

/**
 * Reasons of operation failures which may happen for all operations.
 */
sealed interface CommonFailures :
	Account.Failures.Get,
	Account.Failures.Create,
	Account.Failures.Edit,
	Account.Failures.EditPassword,
	Account.Failures.LogIn,
	Note.Failures.Get,
	Note.Failures.List,
	Note.Failures.Create,
	Note.Failures.Archive,
	Note.Failures.Delete,
	Note.Failures.Edit,
	Note.Failures.Share,
	Note.Failures.AccessOtherAccounts,
	Element.Failures.Get,
	Element.Failures.List,
	Element.Failures.Tick,
	Element.Failures.Add,
	Element.Failures.Remove,
	Event.Failures.Get,
	Event.Failures.List {

	/**
	 * The client could not communicate with the backend.
	 *
	 * Because this failure is not caused by an incorrect request, but by an invalid network status, it is reasonable to
	 * try the request again.
	 */
	data class ConnectionLost(val technicalMessage: String) : CommonFailures

	/**
	 * The operation failed, but we could not determine why.
	 *
	 * In this case, it is likely unsafe to retry the request.
	 */
	data class UnknownError(val technicalMessage: String) : CommonFailures
}

/**
 * Marker interface for operations which fail for guest users.
 */
sealed interface RequiresAuthentication :
	RequiresAuthorization,
	Note.Failures.List,
	Note.Failures.Create,
	Account.Failures.Get,
	Account.Failures.Edit,
	Account.Failures.EditPassword {

	/**
	 * The operation failed because no credentials were provided.
	 *
	 * The user should [log in][Account.Service.logIn], then retry the operation.
	 */
	data object MissingAuthentication : RequiresAuthentication

	/**
	 * The operation failed because, while some credentials were provided, they could not be confirmed to be valid.
	 *
	 * This failure may be generated if a user has changed their password on another device, thus forcing all devices
	 * to log out.
	 *
	 * The user should be asked to [log in again][Account.Service.logIn], after which the operation may be retried.
	 */
	data object InvalidAuthentication : RequiresAuthentication
}

/**
 * Marker interface for operations which may be successful for some users but fail for others.
 *
 * For example, this may happen when a resource is only available for some users.
 */
sealed interface RequiresAuthorization :
	Note.Failures.Get,
	Note.Failures.Archive,
	Note.Failures.Delete,
	Note.Failures.Edit,
	Note.Failures.Share,
	Note.Failures.AccessOtherAccounts,
	Element.Failures.Get,
	Element.Failures.List,
	Element.Failures.Tick,
	Element.Failures.Edit,
	Element.Failures.Add,
	Element.Failures.Remove,
	Event.Failures.List,
	Event.Failures.Get {

	/**
	 * The operation failed because the user is not allowed to go through with it.
	 *
	 * The authenticator did successfully authenticate the user (otherwise, the operation would have
	 * failed with [RequiresAuthentication.MissingAuthentication] or [RequiresAuthentication.InvalidAuthentication]).
	 * Retrying this operation will give the same result unless some external action changed the account's access rights.
	 */
	data object Unauthorized : RequiresAuthorization
}

sealed interface ResourceAccessFailure :
	Account.Failures.Get,
	Note.Failures.Get,
	Element.Failures.Get,
	Event.Failures.Get {

	/**
	 * The requested resource does not exist.
	 */
	data object NotFound : ResourceAccessFailure
}
