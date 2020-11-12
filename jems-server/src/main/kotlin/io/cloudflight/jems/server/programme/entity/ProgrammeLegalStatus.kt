package io.cloudflight.jems.server.programme.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Column

@Entity(name = "programme_legal_status")
data class ProgrammeLegalStatus (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column
    val description: String? = null
)
