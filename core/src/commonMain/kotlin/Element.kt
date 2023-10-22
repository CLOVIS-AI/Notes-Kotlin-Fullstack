package opensavvy.notes.core

import opensavvy.state.outcome.Outcome

/**
 * Tick-able element of a [Note].
 */
data class Element(
	val description: String,
	val ticked: Boolean,
) {

	interface Ref : opensavvy.backbone.Ref<Failures.Get, Element> {

		/**
		 * Sets [Element.ticked] to `true`.
		 */
		suspend fun tick(): Outcome<Failures.Tick, Unit>

		/**
		 * Sets [Element.ticked] to `false`.
		 */
		suspend fun unTick(): Outcome<Failures.Tick, Unit>

		/**
		 * Removes this element permanently.
		 */
		suspend fun remove(): Outcome<Failures.Remove, Unit>

		/**
		 * Edits [Element.description].
		 */
		suspend fun edit(description: String): Outcome<Failures.Edit, Unit>

	}

	sealed interface Failures {
		/** Failures of [Ref.request]. */
		sealed interface Get : Failures

		/** Failures of [Note.Ref.elements]. */
		sealed interface List : Failures

		/** Failures of [Ref.tick] and [Ref.unTick]. */
		sealed interface Tick : Failures

		/** Failures of [Ref.edit]. */
		sealed interface Edit : Failures

		/** Failures of [Note.Ref.addElement]. */
		sealed interface Add : Failures

		/** Failures of [Ref.remove]. */
		sealed interface Remove : Failures
	}
}
