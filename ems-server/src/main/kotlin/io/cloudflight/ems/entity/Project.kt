package io.cloudflight.ems.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity(name = "project")
data class Project (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false)
    val acronym: String,

    @ManyToOne(optional = false)
    val applicant: User,

    @Column(nullable = false)
    val submissionDate: LocalDate

)
