package io.cloudflight.jems.server.call.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ApplicationFormFieldConfigurationId(

    @Column
    @field:NotNull
    val id: String,

    @field:NotNull
    @JoinColumn(name = "application_form_configuration_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    var applicationFormConfigurationEntity: ApplicationFormConfigurationEntity

) : Serializable
