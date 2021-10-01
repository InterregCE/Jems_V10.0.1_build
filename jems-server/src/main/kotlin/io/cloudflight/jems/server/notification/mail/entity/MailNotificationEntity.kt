package io.cloudflight.jems.server.notification.mail.entity

import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity(name = "mail_notification")
class MailNotificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val subject: String,

    @field:NotNull
    val body: String,

    @ElementCollection
    @CollectionTable(name = "mail_notification_recipient", joinColumns = [JoinColumn(name = "notification_id", referencedColumnName = "id")])
    @Column(name = "recipient")
    val recipients: Set<String>,

    @field:NotNull
    val messageType: String
)
