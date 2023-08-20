package opensavvy.notes.core

import opensavvy.backbone.Backbone
import opensavvy.notes.core.Note.Ref
import opensavvy.notes.core.data.Paging
import opensavvy.state.outcome.Outcome

/**
 * A note is composed of a [title], a [description] and a list of [elements][Ref.elements].
 */
data class Note(
	val title: String,
	val description: String?,

	/**
	 * `true` if this note is archived.
	 */
	val archived: Boolean,

	/**
	 * The publicly-available URL by which this note may be accessed.
	 *
	 * If this note is not publicly-shared, this field is `null`.
	 */
	val publicUrl: String?,

	/**
	 * The access rights available to the user who made the request which returned this object.
	 */
	val myRights: AccessRight,
) {

	interface Ref : opensavvy.backbone.Ref<Failures.Get, Note> {

		suspend fun archive(): Outcome<Failures.Archive, Unit>

		suspend fun unArchive(): Outcome<Failures.Archive, Unit>

		suspend fun delete(): Outcome<Failures.Delete, Unit>

		suspend fun edit(
			title: String? = null,
			description: String? = null,
		): Outcome<Failures.Edit, Unit>

		suspend fun share(
			account: Account.Ref,
			accessRight: AccessRight?,
		): Outcome<Failures.Share, Unit>

		suspend fun sharePublicly(
			accessRight: AccessRight?,
		): Outcome<Failures.Share, String>

		suspend fun sharedWith(): Outcome<Failures.AccessOtherAccounts, List<Pair<Account.Ref, AccessRight>>>

		suspend fun addElement(
			description: String,
		): Outcome<Element.Failures.Add, Element.Ref>

		suspend fun elements(
			includeTicked: Boolean,
			paging: Paging? = null,
		): Outcome<Element.Failures.List, List<Element.Ref>>

		suspend fun history(
			paging: Paging? = null,
		): Outcome<Event.Failures.List, List<Event.Ref>>

	}

	interface Service<R : Ref> : Backbone<R, Failures.Get, Note> {

		/**
		 * Lists all notes the calling account has access to.
		 *
		 * @param includeArchived If set to `false`, archived notes are not returned by this request.
		 * @param query If specified, the backend does a full-text search with the requested query, and only returns results
		 * which satisfy the query.
		 */
		suspend fun list(
			includeArchived: Boolean = false,
			query: String? = null,
			paging: Paging? = null,
		): Outcome<Failures.List, List<R>>

		/**
		 * Creates a new note.
		 */
		suspend fun create(
			title: String,
			description: String? = null,
		): Outcome<Failures.Create, R>

	}

	sealed interface Failures {
		/** Failures of [Ref.request]. */
		sealed interface Get : Failures

		/** Failures of [Service.list]. */
		sealed interface List : Failures

		/** Failures of [Service.create]. */
		sealed interface Create : Failures

		/** Failures of [Ref.archive] and [Ref.unArchive]. */
		sealed interface Archive : Failures

		/** Failures of [Ref.delete]. */
		sealed interface Delete : Failures

		/** Failures of [Ref.edit]. */
		sealed interface Edit : Failures

		/** Failures of [Ref.share] and [Ref.sharePublicly]. */
		sealed interface Share : Failures

		/** Failures of [Ref.sharedWith]. */
		sealed interface AccessOtherAccounts : Failures
	}
}
