package io.cloudflight.ems.exception

class DataValidationException(
    val errors: Map<String, List<String>>
) : Exception() {

    /**
     * prefix with type
     */
    companion object {
        val NULL = "missing"
        val STRING_LONG = "long"
        val LOCAL_DATE_IN_PAST = "date_in_past"
    }
}
