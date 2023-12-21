package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.payments.entity.PaymentAuditExportEntity
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(PaymentApplicationToEcAuditExportModelMapper::class.java)

fun Page<PaymentAuditExportEntity>.toModel() = map { it.toModel() }

fun PaymentAuditExportEntity.toModel() = mapper.map(this)

fun Iterable<PaymentAuditExportEntity>.toModel() =
    this.map { mapper.map(it) }

@Mapper
abstract class PaymentApplicationToEcAuditExportModelMapper {
    @Named("toFundModel")
    fun toFundModel(entity: ProgrammeFundEntity?) = entity?.toModel()

    @Mapping(source = "programmeFund", target = "fund", qualifiedByName = ["toFundModel"])
    abstract fun map(entity: PaymentAuditExportEntity): PaymentToEcExportMetadata
}
