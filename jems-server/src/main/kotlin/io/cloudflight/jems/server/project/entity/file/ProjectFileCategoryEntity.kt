package io.cloudflight.jems.server.project.entity.file

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "project_file_category")
class ProjectFileCategoryEntity(

    @EmbeddedId
    val categoryId: ProjectFileCategoryId,

    @MapsId("fileId")
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @field:NotNull
    val projectFile: ProjectFileEntity
)
