package org.ebics.client.ebicsrestapi.utils

import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.apache.xml.security.Init
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ebics.client.api.trace.BankConnectionTraceSession
import org.ebics.client.api.trace.IFileService
import org.ebics.client.api.trace.orderType.OrderTypeDefinition
import org.ebics.client.ebicsrestapi.EbicsProductConfiguration
import org.ebics.client.ebicsrestapi.MockSession
import org.ebics.client.ebicsrestapi.TestContext
import org.ebics.client.ebicsrestapi.configuration.EbicsRestConfiguration
import org.ebics.client.exception.EbicsServerException
import org.ebics.client.exception.h005.EbicsReturnCode
import org.ebics.client.model.EbicsVersion
import org.ebics.client.order.EbicsAdminOrderType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.io.ByteArrayOutputStream
import java.security.Security
import java.time.ZonedDateTime

@SpringBootTest
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [TestContext::class])
class FileDownloadCacheTest(
    @Autowired private val fileDownloadCache: IFileDownloadCache,
    @Autowired private val fileService: IFileService,
    @Autowired private val configuration: EbicsRestConfiguration,
    @Autowired private val fileDownloadH004: org.ebics.client.filetransfer.h004.FileDownload,
    @Autowired private val fileDownloadH005: org.ebics.client.filetransfer.h005.FileDownload,
) {
    init {
        Init.init()
        Security.addProvider(BouncyCastleProvider())
    }

    val prod = EbicsProductConfiguration("testProd", "de", "JTO", "3.5")

    @BeforeEach
    fun initTestContext() {
        //Mock getting files from EBICS
        val bos = ByteArrayOutputStream()
        bos.write("firstLiveDownload".toByteArray())
        every { fileDownloadH004.fetchFile(any(), any(), any()) } returns bos
        every { fileDownloadH005.fetchFile(any(), any(), any()) } returns bos

        //Remove all stored entries from cache (the fileDownloadCache has state)
        (fileService as FileServiceMockImpl).removeAllFilesOlderThan(ZonedDateTime.now())
    }

    @Test
    fun whenCalledOnceWithoutCache_thenMustRetrieveOnline() {
        val session = MockSession.getSession(1, false, prod, configuration)
        val traceSession = BankConnectionTraceSession(session, OrderTypeDefinition(EbicsAdminOrderType.HTD))
        val result =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005,
                false
            )
        Assertions.assertEquals("firstLiveDownload", String(result))
    }

    @Test
    fun whenCalledTwiceWithoutCache_thenMustRetrieveBothOnline() {
        val session = MockSession.getSession(1, false, prod, configuration)
        val traceSession = BankConnectionTraceSession(session, OrderTypeDefinition(EbicsAdminOrderType.HTD))
        val result =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005,
                false
            )
        Assertions.assertEquals("firstLiveDownload", String(result))
        val result2 =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005,
                false
            )
        Assertions.assertEquals("firstLiveDownload", String(result2))
    }

    @Test
    fun whenCalledOnce_thenMustRetrieveOnline() {
        val session = MockSession.getSession(1, false, prod, configuration)
        val traceSession = BankConnectionTraceSession(session, OrderTypeDefinition(EbicsAdminOrderType.HTD))
        val result =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        Assertions.assertEquals("firstLiveDownload", String(result))
    }

    @Test
    fun whenCalledTwice_thenSecondMustRetrieveFromCache() {
        val session = MockSession.getSession(1, false, prod, configuration)
        val traceSession = BankConnectionTraceSession(session, OrderTypeDefinition(EbicsAdminOrderType.HTD))
        val result =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        Assertions.assertEquals("firstLiveDownload", String(result))

        val result2 =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        Assertions.assertEquals("firstLiveDownload-cached", String(result2))
    }

    @Test
    fun whenCall1Exception_call2Ok_call3Exception_call4ok_then_call2ReturnsLiveResultAndCall4Cached_otherwiseExceptions() {
        every { fileDownloadH005.fetchFile(any(), any(), any())} throws EbicsServerException(EbicsReturnCode.EBICS_NO_DOWNLOAD_DATA_AVAILABLE)

        val session = MockSession.getSession(1, false, prod, configuration)
        val traceSession = BankConnectionTraceSession(session, OrderTypeDefinition(EbicsAdminOrderType.HTD))
        try {
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        } catch (exception: EbicsServerException) {
            Assertions.assertEquals(EbicsReturnCode.EBICS_NO_DOWNLOAD_DATA_AVAILABLE, exception.ebicsReturnCode)
        }

        val bos = ByteArrayOutputStream()
        bos.write("firstLiveDownload".toByteArray())
        every { fileDownloadH005.fetchFile(any(), any(), any()) } returns bos

        val result1 =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        Assertions.assertEquals("firstLiveDownload", String(result1))

        every { fileDownloadH005.fetchFile(any(), any(), any())} throws EbicsServerException(EbicsReturnCode.EBICS_NO_DOWNLOAD_DATA_AVAILABLE)

        try {
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        } catch (exception: EbicsServerException) {
            Assertions.assertEquals(EbicsReturnCode.EBICS_NO_DOWNLOAD_DATA_AVAILABLE, exception.ebicsReturnCode)
        }

        val result2 =
            fileDownloadCache.getLastFileCached(
                session,
                traceSession,
                OrderTypeDefinition(EbicsAdminOrderType.HTD),
                EbicsVersion.H005
            )
        Assertions.assertEquals("firstLiveDownload-cached", String(result2))
    }
}