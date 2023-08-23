package opensavvy.notes.core

import kotlinx.datetime.Instant

/**
 * An event in the history of a [Note].
 */
data class Event(
	/**
	 * The account who is the origin of this event.
	 *
	 * This value can be `null` if the user making the request is not allowed to know who created the event, or
	 * because the associated account has been deleted.
	 */
	val author: Account.Ref?,

	/**
	 * The timestamp at which the event happened.
	 */
	val at: Instant,

	/**
	 * Information on the event itself.
	 */
	val data: Type,
) {

	sealed class Type {
		/** [Note.title] was edited. */
		data class EditedTitle(val previousTitle: String) : Type()

		/** [Note.description] was edited. */
		data class EditedDescription(val previousDescription: String) : Type()

		/** The note was shared with [accounts]. If [Note.AccessRight] is `null`, the sharing stopped. */
		data class SharedWith(val accounts: List<Pair<Account.Ref, AccessRight?>>) : Type()

		/** The note was shared publicly. If [rights] is `null`, the sharing stopped. */
		data class SharePublicly(val rights: AccessRight?) : Type()

		/** The note was archived. */
		data object Archived : Type()

		/** The note was unarchived. */
		data object Unarchived : Type()

		/** Event triggered when the note is first created, then never afterward. */
		data object Created : Type()

		/** Elements of this note have been ticked. If an element has been removed since, it does not appear in the list. */
		data class Ticked(val elements: List<Element.Ref>) : Type()

		/** Elements of this note have been un-ticked. If an element has been removed since, it does not appear in the list. */
		data class UnTicked(val elements: List<Element.Ref>) : Type()

		/** [Element.description] was edited. */
		data class RenamedElement(val element: Element.Ref, val previousDescription: String) : Type()

		/** One or more elements have been removed. */
		data object RemovedElements : Type()
	}

	interface Ref : opensavvy.backbone.Ref<Failures.Get, Event>

	sealed interface Failures {
		sealed interface Get : Failures
		sealed interface List : Failures
	}
}
