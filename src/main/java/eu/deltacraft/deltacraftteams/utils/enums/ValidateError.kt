package eu.deltacraft.deltacraftteams.utils.enums

enum class ValidateError constructor(val value: String) {
    ArgumentsError("arguments_error"),
    MethodNotValid("method_not_valid"),
    MissingConsent("missing_consent"),
    MissingName("missing_name"),
    UuidNotValid("uuid_not_valid"),
    NotRegistered("not_registered"),
    Unauthorized("unauthorized"),
    NotInTeam("not_in_team"),
    Unknown("unknown");

    companion object {
        fun from(findValue: String): ValidateError? = values().firstOrNull { it.value == findValue }
    }
}