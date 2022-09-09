package io.cloudflight.jems.api.programme.dto.typologyerrors

class TypologyErrorsUpdateDTO (
    val toDeleteIds: List<Long>,
    val toPersist: List<TypologyErrorsDTO>,
)
