package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Entity(name = "application_form_field_configuration")
class ApplicationFormFieldConfigurationEntity(

    @EmbeddedId
    val id: ApplicationFormFieldConfigurationId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var visibilityStatus: FieldVisibilityStatus

)
