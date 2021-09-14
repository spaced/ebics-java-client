package org.ebics.client.ebicsrestapi.bank

import org.ebics.client.api.FunctionException
import org.ebics.client.api.bank.Bank
import org.ebics.client.api.bank.BankService
import org.ebics.client.api.bank.versions.VersionSupport
import org.ebics.client.api.bank.versions.VersionSupportBase
import org.ebics.client.api.bank.versions.VersionSupportService
import org.ebics.client.ebicsrestapi.EbicsRestConfiguration
import org.ebics.client.bank.BankOperations
import org.ebics.client.ebicsrestapi.EbicsAccessMode
import org.ebics.client.model.EbicsVersion
import org.springframework.stereotype.Component
import java.net.URL

@Component("EbicsBankAPI")
class EbicsBankAPI(private val configuration: EbicsRestConfiguration, private val bankService: BankService, private val versionSupportService: VersionSupportService) {

    fun updateSupportedVersion(bankId: Long, versionSupport: VersionSupportBase) {
        val bank = bankService.getBankById(bankId)
        return versionSupportService.updateVersionSupport(versionSupport, bank)
    }

    fun getSupportedVersions(bankId: Long, mode: EbicsAccessMode): List<VersionSupport> {
        val bank = bankService.getBankById(bankId)

        val serverVersions = when(mode) {
            EbicsAccessMode.ForcedOnline -> getVersions(bank)
            EbicsAccessMode.OptionalOnline -> {
                try {
                    getVersions(bank)
                } catch (ex: Exception) {
                    return bank.ebicsVersions ?: emptyList()
                }
            }
            EbicsAccessMode.Offline -> return bank.ebicsVersions ?: emptyList()
        }

        val allVersions = serverVersions.toSet() + clientVersions.toSet()
        val supportedVersions = serverVersions.toSet().intersect(clientVersions)

        val highestSupportedVersion = supportedVersions.maxOrNull()
        val currentDefaultVersion = bank.ebicsVersions?.find { it.isDefault }?.version
        //Check if the current preferred version is still supported by bank,
        //If not select the highest one automatically
        val futureDefaultVersion =
            if (currentDefaultVersion == null || supportedVersions.find { it == currentDefaultVersion } == null) highestSupportedVersion
            else currentDefaultVersion

        return allVersions.map { version ->
            val storedVersion = bank.ebicsVersions?.find { evPer -> evPer.version == version }
            VersionSupport(
                version,
                isSupportedByBank = serverVersions.contains(version),
                isSupportedByClient = clientVersions.contains(version),
                isAllowed = supportedVersions.contains(version) && storedVersion?.isAllowed ?: true,
                isDefault = version == futureDefaultVersion, bank
            )
        }
    }

    fun getSupportedVersionsLive(bankURL: String, bankHostId: String): List<VersionSupport> {
        //Its not required to have bank persisted in DB, its just online gui check of version, therefore is bank temp created only
        val bank = Bank(null, URL(bankURL), bankHostId, "", null, emptyList())

        val serverVersions = getVersions(bank)
        val allVersions = serverVersions.toSet() + clientVersions.toSet()
        val supportedVersions = serverVersions.toSet().intersect(clientVersions)
        val highestSupportedVersion = supportedVersions.maxOrNull()

        return allVersions.map { version ->
            VersionSupport(version, true, clientVersions.contains(version), clientVersions.contains(version),
                version == highestSupportedVersion, bank
            )
        }
    }

    private fun getVersions(bank: Bank): List<EbicsVersion> {
        val versions = BankOperations(configuration).sendHEV(bank.bankURL.toString(), bank.hostId)
        if (versions.isEmpty())
            throw FunctionException(
                "The bank ${bank.bankURL} with hostID: ${bank.hostId} doen't support any EBICS version, HEV return empty list",
                null
            )
        return versions
    }

    companion object {
        val clientVersions = listOf(EbicsVersion.H004, EbicsVersion.H005)
    }
}