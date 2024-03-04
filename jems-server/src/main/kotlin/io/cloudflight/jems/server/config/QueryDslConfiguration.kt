package io.cloudflight.jems.server.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@AutoConfiguration
class QueryDslConfiguration {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Bean
    fun jpaQueryFactoryConfig(): JPAQueryFactory = JPAQueryFactory(entityManager)

}
