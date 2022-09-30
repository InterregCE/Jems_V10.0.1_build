package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.EMAIL_REGEX
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingPartnerDocumentsLocation(
    private val documentsLocationPersistence: ContractingPartnerDocumentsLocationPersistence,
    private val generalValidator: GeneralValidatorService
): UpdateContractingPartnerDocumentsLocationInteractor {

    @CanUpdateProjectContractingPartner
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerDocumentsLocationException::class)
    override fun updateDocumentsLocation(
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocation
    ): ContractingPartnerDocumentsLocation {
        validateDocumentsLocation(documentsLocation)
        return documentsLocationPersistence.updateDocumentsLocation(partnerId, documentsLocation)
    }

    private fun validateDocumentsLocation(input: ContractingPartnerDocumentsLocation) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(input.firstName, 50, "firstNam"),
            generalValidator.maxLength(input.lastName, 50, "lastName"),
            generalValidator.maxLength(input.title, 50, "title"),
            generalValidator.maxLength(input.emailAddress, 50, "emailAddress"),
            if (input.emailAddress.isNullOrBlank()) emptyMap () else
                generalValidator.matches(input.emailAddress, EMAIL_REGEX, "emailAddress", "user.email.wrong.format"),
            generalValidator.maxLength(input.telephoneNo, 25, "telephoneNo"),
            generalValidator.maxLength(input.institutionName, 100, "institutionName"),
            generalValidator.maxLength(input.street, 50, "street"),
            generalValidator.maxLength(input.locationNumber, 20, "locationNumber"),
            generalValidator.maxLength(input.postalCode, 20, "postalCode"),
            generalValidator.maxLength(input.city, 50, "city"),
            generalValidator.maxLength(input.countryCode, 2, "countryCode"),
            generalValidator.maxLength(input.nutsTwoRegionCode, 4, "nutsTwoRegionCode"),
            generalValidator.maxLength(input.nutsThreeRegionCode, 5, "nutsThreeRegionCode"))
    }
}
