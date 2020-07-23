package io.cloudflight.ems.entity

import io.cloudflight.ems.api.dto.call.CallStatus
import java.time.ZonedDateTime
import javax.persistence.*

@Entity(name = "calls")
data class Call (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    val creator: User,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, name = "start_date")
    val startDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false, name = "end_date")
    val endDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: CallStatus,

    @Column
    val description: String?
)
