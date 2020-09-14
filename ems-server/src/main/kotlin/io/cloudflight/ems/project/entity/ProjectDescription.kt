package io.cloudflight.ems.project.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne

@Entity(name = "project_description")
data class ProjectDescription(

    @Id
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @OneToOne(optional = false)
    @MapsId
    val project: Project,

    @Column
    val projectCoordination: String?,

    @Column
    val projectQualityAssurance: String?,

    @Column
    val projectCommunication: String?,

    @Column
    val projectFinancialManagement: String?,

    @Column
    val projectJointDevelopment: String?,

    @Column
    val projectJointImplementation: String?,

    @Column
    val projectJointStaffing: String?,

    @Column
    val projectJointFinancing: String?,

    @OneToOne(mappedBy = "project", cascade = [CascadeType.ALL])
    val projectHorizontalPrinciples: ProjectHorizontalPrinciples?,

    @Column
    val projectOwnership: String?,

    @Column
    val projectDurability: String?,

    @Column
    val projectTransferability: String?
)
