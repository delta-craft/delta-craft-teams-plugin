package eu.deltacraft.deltacraftteams.utils.enums

enum class SessionError(val value: String) {
    InvalidCode("invalid_code"),
    RequestExpired("request_expired"),
    SessionExpired("session_expired"),
    InvalidIP("invalid_ip"),
    Unknown("unknown"),
    IPMismatch("ip_mismatch"),
    UuidNotValid("uuid_not_valid");


    companion object {
        fun from(findValue: String): SessionError? = values().firstOrNull { it.value == findValue }
    }
}