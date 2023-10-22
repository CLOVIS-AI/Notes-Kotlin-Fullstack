package opensavvy.notes.core.data

/**
 * Requests a subset of the available information, based on the number of total elements.
 */
data class Paging(
	/**
	 * The identifier of the requested page.
	 *
	 * When making a search, the first [pageSize] elements are page 0.
	 * The next [pageSize] elements are page 1, and so on.
	 */
	val page: UInt,

	/**
	 * The number of elements requested per page.
	 *
	 * If set to `null`, the other interlocutor decides how many elements are in that page.
	 * When set to a specific value, the other interlocutor makes a best effort attempt at respecting it.
	 * The interlocutor is forbidden from returning more elements than requested.
	 * However, the interlocutor is allowed to behave as if a lower [pageSize] has been requested, as long as they behave
	 * consistently when computing the [page] number.
	 */
	val pageSize: UInt? = null,
)
