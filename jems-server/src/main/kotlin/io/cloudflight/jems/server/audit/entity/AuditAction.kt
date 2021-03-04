package io.cloudflight.jems.server.audit.entity

enum class AuditAction {
    // package CALL
    CALL_ADDED,
    CALL_CONFIGURATION_CHANGED,
    CALL_PUBLISHED,


    // package NUTS
    NUTS_DATASET_DOWNLOAD,

    // package PROGRAMME
    PROGRAMME_PRIORITY_ADDED,
    PROGRAMME_PRIORITY_UPDATED,
    PROGRAMME_BASIC_DATA_EDITED,
    PROGRAMME_NUTS_AREA_CHANGED,
    PROGRAMME_FUNDS_CHANGED,
    PROGRAMME_UI_LANGUAGES_CHANGED,
    PROGRAMME_INPUT_LANGUAGES_CHANGED,
    PROGRAMME_STRATEGIES_CHANGED,

    // PROGRAMME - Indicators
    PROGRAMME_INDICATOR_ADDED,
    PROGRAMME_INDICATOR_EDITED,

    // PROGRAMME - Cost Options:
    PROGRAMME_LUMP_SUM_ADDED,
    PROGRAMME_LUMP_SUM_CHANGED,
    PROGRAMME_UNIT_COST_ADDED,
    PROGRAMME_UNIT_COST_CHANGED,

    // package PROJECT
    APPLICATION_STATUS_CHANGED,
    CALL_ALREADY_ENDED,
    QUALITY_ASSESSMENT_CONCLUDED,
    ELIGIBILITY_ASSESSMENT_CONCLUDED,

    // messages related to FILEs
    PROJECT_FILE_DELETED,
    PROJECT_FILE_UPLOADED_SUCCESSFULLY,
    PROJECT_FILE_UPLOAD_FAILED,
    PROJECT_FILE_DESCRIPTION_CHANGED,

    // package SECURITY
    USER_LOGGED_IN,
    USER_LOGGED_OUT,
    USER_SESSION_EXPIRED,

    // package USER
    USER_CREATED,
    USER_REGISTERED,
    USER_ROLE_CHANGED,
    PASSWORD_CHANGED,
    USER_DATA_CHANGED,

    //package LEGAL STATUS
    LEGAL_STATUS_EDITED,

}
