package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.common.paging.Page as PluginPage
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable as PluginPageable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

fun PluginPageable.toJpaPage() =
    if (this.isPaged) PageRequest.of(page, size) else Pageable.unpaged()

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
