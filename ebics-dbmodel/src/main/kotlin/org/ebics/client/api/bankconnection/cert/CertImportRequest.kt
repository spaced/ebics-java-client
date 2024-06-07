package org.ebics.client.api.bankconnection.cert

class CertImportRequest(val dn: String, val usePassword: Boolean, val password: String, val authenticationX002Xml: String, val signatureA005Xml: String, val encryptionE002Xml: String)
