package opensavvy.notes.core

/**
 * The different access rights an account can have regarding a note.
 *
 * Because it is not possible to access a note without having any rights on it, there is no representation of "no rights".
 */
enum class AccessRight {
	// The order is important! Do not change it

	/**
	 * The account can:
	 * - access the note
	 * - access un-ticked [Ref.elements]
	 */
	Limited,

	/**
	 * The account can:
	 * - do everything [Limited] allows
	 * - see ticked [Note.Ref.elements]
	 */
	ReadOnly,

	/**
	 * The account can:
	 * - do everything [ReadOnly] allows
	 * - add, edit and remove elements
	 * - see the list of accounts having access to the note
	 */
	Contributor,

	/**
	 * The account can:
	 * - share and un-share the note
	 * - archive or un-archive the note
	 * - delete the note
	 * - edit the note
	 */
	Owner,
}

/** The account is allowed to access a note. */
val AccessRight.canSeeNote get() = this >= AccessRight.Limited

/** The account is allowed to see un-ticked elements of a note. */
val AccessRight.canSeeUnTickedElements get() = this >= AccessRight.Limited

/** The account is allowed to see ticked elements of a note. */
val AccessRight.canSeeTickedElements get() = this >= AccessRight.ReadOnly

/** The account is allowed to add, remove and edit elements of a note. */
val AccessRight.canEditElements get() = this >= AccessRight.Contributor

/** The account is allowed to see other accounts with whom the note is shared. */
val AccessRight.canSeeOtherAccounts get() = this >= AccessRight.Contributor

/** The account is allowed to share and un-share the note with other users. */
val AccessRight.canShare get() = this >= AccessRight.Owner

/** The account is allowed to archive and un-archive the note with other users. */
val AccessRight.canArchive get() = this >= AccessRight.Owner

/** The account is allowed to edit and delete the note. */
val AccessRight.canEdit get() = this >= AccessRight.Owner
