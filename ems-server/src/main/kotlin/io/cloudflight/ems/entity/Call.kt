package io.cloudflight.ems.entity

import io.cloudflight.ems.api.dto.call.CallStatus
import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "calls")
data class Call (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    val creator: User,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = false, name = "start_date")
    val startDate: ZonedDateTime,

    @Column(nullable = false, name = "end_date")
    val endDate: ZonedDateTime,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: CallStatus,

    @Column
    val description: String?
)
