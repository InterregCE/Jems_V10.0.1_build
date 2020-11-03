package io.cloudflight.jems.api.programme.dto

data class OutputProgrammeLanguage(
    val code: SystemLanguage,
    val ui: Boolean,
    val fallback: Boolean,
    val input: Boolean
) {
    fun getChange(newData: OutputProgrammeLanguage): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()
        if (code != newData.code) {
            changes["code"] = Pair(code, newData.code)
        }
        if (ui != newData.ui) {
            changes["ui"] = Pair(ui, newData.ui)
        }
        if (fallback != newData.fallback) {
            changes["fallback"] = Pair(fallback, newData.fallback)
        }
        if (input != newData.input) {
            changes["input"] = Pair(input, newData.input)
        }

        return changes
    }
}
