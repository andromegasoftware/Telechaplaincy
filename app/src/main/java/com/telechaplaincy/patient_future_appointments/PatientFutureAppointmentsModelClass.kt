package com.telechaplaincy.patient_future_appointments

class PatientFutureAppointmentsModelClass(
    var appointmentId: String? = null,
    val patientId: String? = null,
    val patientName: String? = null,
    val patientSurname: String? = null,
    val appointmentPrice: String? = null,
    val appointmentDate: String? = null,
    val chaplainId: String? = null,
    val chaplainName: String ?= null,
    val chaplainSurname: String ?= null,
    val chaplainProfileImageLink:String ?= null,
    val chaplainAddressingTitle:String ?= null,
    val chaplainCredentialsTitle:ArrayList<String>? = null,
    val chaplainField:ArrayList<String>? = null,
    var patientAppointmentTimeZone: String? = null
) {
}