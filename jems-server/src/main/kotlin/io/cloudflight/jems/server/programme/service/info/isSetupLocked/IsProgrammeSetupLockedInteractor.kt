package io.cloudflight.jems.server.programme.service.info.isSetupLocked

interface IsProgrammeSetupLockedInteractor {
    fun isLocked(): Boolean

    fun isAnyReportCreated(): Boolean

    fun isFastTrackLumpSumReadyForPayment(programmeLumpSumId: Long): Boolean
}

