package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.common.paging.Page as PluginPage
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

fun Pageable.toJpaPage() = PageRequest.of(page, size)

inline fun <T, U> Page<T>.toPluginPage(transform: (T) -> U): PluginPage<U> = PluginPage(
    totalElements = totalElements,
    totalPages = totalPages,
    number = number,
    size = size,
    numberOfElements = numberOfElements,
    isFirst = isFirst,
    isLast = isLast,
    hasNext = hasNext(),
    content = content.map { transform.invoke(it) },
)
