package com.telechaplaincy.appointment

class AppointmentModelClass(
    var appointmentId: String? = null,
    val patientId: String? = null,
    val chaplainId: String? = null,
    val appointmentPrice: String? = null,
    val appointmentDate: String? = null,
    val patientName: String? = null,
    val patientSurname: String? = null,
    val chaplainName: String? = null,
    val chaplainSurname: String? = null,
    val chaplainProfileImageLink: String? = null,
    val patientProfileImageLink: String? = null,
    val chaplainAddressingTitle: String? = null,
    val chaplainCredentialsTitle: ArrayList<String>? = null,
    val chaplainField: ArrayList<String>? = null,
    var patientAppointmentTimeZone: String? = null,
    val chaplainUniqueUserId: String? = null,
    var patientUniqueUserId: String? = null,
    var chaplainNoteForPatient: String? = null,
    var chaplainNoteForDoctors: String? = null,
    var chaplainCategory: String? = null,
    var appointmentStatus: String? = null,
    var isAppointmentPricePaidOutToChaplain: Boolean? = false
)