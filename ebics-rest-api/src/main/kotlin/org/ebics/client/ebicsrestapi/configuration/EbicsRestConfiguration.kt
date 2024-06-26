package org.ebics.client.ebicsrestapi.configuration

import org.ebics.client.api.EbicsConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class EbicsRestConfiguration(

    @Value("\${ebics.signatureVersion:A005}")
    override val signatureVersion: String,

    @Value("\${ebics.authenticationVersion:X002}")
    override val authenticationVersion: String,

    @Value("\${ebics.encryptionVersion:E002}")
    override val encryptionVersion: String,

    @Value("\${ebics.trace:#{true}}")
    override val isTraceEnabled: Boolean,

    @Value("\${ebics.compression:#{true}}")
    override val isCompressionEnabled: Boolean,

    @Value("\${ebics.locale.language:en}")
    private val localeLanguage:String,

) : EbicsConfiguration {

    final override val locale: Locale = Locale(localeLanguage)

    init {
        //Setting default locale as well in order to set locale for Messages singleton object
        Locale.setDefault(locale)
    }
}