package io.cloudflight.jems.server.programme.entity.checklist

import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import javax.validation.constraints.NotNull

@Entity(name = "programme_checklist")
class ProgrammeChecklistEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProgrammeChecklistType = ProgrammeChecklistType.ELIGIBILITY,

    val name: String?,

    var lastModificationDate: ZonedDateTime?,

    @OneToMany(mappedBy = "checklist", cascade = [CascadeType.ALL], orphanRemoval = true)
    var components: MutableSet<ProgrammeChecklistComponentEntity>? = mutableSetOf()
) {
    @PrePersist
    @PreUpdate
    fun updateLastModificationDate() {
        lastModificationDate = ZonedDateTime.now()
    }
}
