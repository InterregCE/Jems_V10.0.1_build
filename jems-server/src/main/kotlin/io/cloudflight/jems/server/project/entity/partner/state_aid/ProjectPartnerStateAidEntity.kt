package io.cloudflight.jems.server.project.entity.partner.state_aid

import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "project_partner_state_aid")
class ProjectPartnerStateAidEntity(

    @Id
    @Column(name = "partner_id")
    val partnerId: Long,

    val answer1: Boolean? = null,
    val answer2: Boolean? = null,
    val answer3: Boolean? = null,
    val answer4: Boolean? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.partnerId")
    val translatedValues: Set<ProjectPartnerStateAidTranslEntity> = emptySet(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "id.projectPartnerStateAid")
    val activities: MutableList<ProjectPartnerStateAidActivityEntity>? = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_aid_id")
    val stateAidScheme: ProgrammeStateAidEntity? = null
)
