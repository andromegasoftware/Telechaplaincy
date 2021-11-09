package com.telechaplaincy.chaplain_sign_activities

class ChaplainUserProfile(
    val name: String ?= null,
    val surname: String ?= null,
    val email: String ?= null,
    val userId: String ?= null,
    val profileImageLink:String ?= null,
    val addressingTitle:String ?= null,
    val phone:String ?= null,
    val birthDate:String ?= null,
    val education:String ?= null,
    val experience:String ?= null,
    val language:String ?= null,
    val ssn:String ?= null,
    val ordainedName:String ?= null,
    val ordainedPeriod:String ?= null,
    val additionalCredential:String ?= null,
    val bio:String ?= null,
    val cvUrl:String ?= null,
    val certificateUrl:String ?= null,
    val certificateName:String ?= null,
    val cvName:String ?= null,
    val credentialsTitle:ArrayList<String>? = null,
    val faith:ArrayList<String>? = null,
    val ethnic:ArrayList<String>? = null,
    val otherLanguages:ArrayList<String>? = null,
    val field:ArrayList<String>? = null,
    var accountStatus: String ?= "1"
    ) {

}