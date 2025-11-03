class Sample(
    @RemoveAfterRelease("sample") // property
    private val sample: String,
    @RemoveAfterRelease("sample") // property with initial value
    private val sample2: String = "",
    @param:RemoveAfterRelease("sample") // property with use-sites
    private val sample3: String = "",
) {
    fun sample() {
        @RemoveAfterRelease("sample") // expression statement
        println("hogehoge")

        @RemoveAfterRelease("sample") // local variable statement,
        val a = "hogehoge"

        listOf(
            @RemoveAfterRelease("sample") // value argument
            true
        )

        listOf(element = @RemoveAfterRelease("sample") true) // named argument

        val sample = Sample(@RemoveAfterRelease("sample") "sample") // constructor argument

        val b = @RemoveElseClauseAfterRelease("sample") if (true) {
            "enabled"
        } else {
            "disabled"
        }

        val c = @RemoveElseClauseAfterRelease("sample") if (true) "enabled" else "disabled"
    }

    @RemoveAfterRelease("sample")
    fun sample2() {
    }

    class Sample2 @RemoveAfterRelease("sample") constructor()
}
