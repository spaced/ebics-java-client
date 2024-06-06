package org.ebics.client.certificate

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.*
import java.math.BigInteger
import java.net.URL
import java.security.Security
import java.security.interfaces.RSAPublicKey
import java.util.Base64


class KeyUtilTest {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    private fun getResourceURL(path: String): URL {
        val resource = object {}.javaClass.getResource(path)
        if (resource != null)
            return resource
        else
            throw IllegalArgumentException("The requested resource URL '$path' doesn't exist")
    }

    private fun getResourceAsStream(path: String): InputStream? =
        getResourceURL(path).openStream()

    private fun getResourceAsFile(path: String): File =
        File(getResourceURL(path).file)

    @Test
    fun testGenerateDigestFromFiles() {
        getResourceAsFile("/testPublicKeys/").walk().filter { it.isFile }.forEach { pubFile ->
            val pubKey = KeyUtil.loadKey(FileInputStream(pubFile))
            val hash = pubFile.nameWithoutExtension.removePrefix("key-")
            Assertions.assertEquals(hash, String(KeyUtil.getKeyHash(pubKey as RSAPublicKey)))
            println(hash)
        }
    }

    @Test
    fun testGenerateAndSaveAndLoadAndCompareDigest() {
        val kp = KeyUtil.makeKeyPair(2048)
        val digest = String(KeyUtil.getKeyHash(kp.public as RSAPublicKey))
        println(digest)
        val bos = ByteArrayOutputStream()
        KeyUtil.saveKey(bos, kp.public)
        val publicKey = KeyUtil.loadKey(ByteArrayInputStream(bos.toByteArray()))
        val digest2 = String(KeyUtil.getKeyHash(publicKey as RSAPublicKey))
        Assertions.assertEquals(digest2, digest)
    }

    @Test
    fun testBuildKeyPair() {
        val m = "zR23Lcg7qLekrQSuMLzipknJMf1lsnK36S3gpAyppZWRMyWhw6yMBe3POdUiYwyLHdCnzXLtCsOEzRmqCZ8KSa7DRV2PkOxUZgC/FBp8qvLE/IoYMkI8MK0mKZA609Wt1lYIYNBCBAdNLpNB2o9bsyhKnqMhUD17wzifCBhtI1bAIz7Lu546qQ3AqQsuPQS3iUtx2m2itaEOxm6sQHVjxun1qgHa4DnzXmC93h0lkBwIFx13wFPLwQNseLHtefOoFq7binQJMf2a4C8GSnV5EZlde1fDElim2gCDD2v1ypBzKeLpD+XeySwz5y0e5dTiIpgrEQNaSJlkeKyavVDJEw=="
        val privateK = "A1FLuS3X6S6vNy0wNBGaCN6AppJWpsA8QUVAsOaTh94XbyBYsXWE/kOOan0MBVYamezaCfSl6NqotziHC4jK/c4Z0nFk9Q8dfljcvlJ/WbR7gyooogd4OcFTMLDogqyeFy1lDUvRBYeos+wl0IcpEIjCTDaPP2D/M3Ui/VqHyV6CYi8oa4KyAvmXQchIp+ARxzzGHO2VVUZOWRAxOBsHadsL5wY0Z6IVXaAL3aZEBPQqg+hzBcY0ih2HN6x09ElDTZaAoLMYLsc6c/Ofqnxlnhr4vFb/u4LZZs8c8lTS1l/5KSvO97G7zlFX3vHxM5LE1tNr2xRQyUTXh+5frrLFgQ=="
        val publicK ="AQAB"

        val keyPair = KeyUtil.buildKeyPair(m, publicK, privateK)

        val hash = KeyUtil.getKeyHash(keyPair.public as RSAPublicKey)
        Assertions.assertEquals("F712F1C290D197F69216DD9D66F245D1812EE701F8227A22E227952AF65F4CF7",String(hash))
    }

    /*@Test
    fun createTestFileAndStoreIt() {
        val kp = KeyUtil.makeKeyPair(2048)
        val digest = String(KeyUtil.getKeyHash(kp.public as RSAPublicKey))
        println(digest)
        val fos = FileOutputStream("/tmp/key-$digest.pub")
        KeyUtil.saveKey(fos, kp.public)
    }*/

}
