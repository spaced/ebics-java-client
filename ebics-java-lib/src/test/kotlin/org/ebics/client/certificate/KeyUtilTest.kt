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

    @Test
    fun testBuildKeyFromXml() {
        val xml = """
            <RSAParameters xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <Exponent>AQAB</Exponent>
            <Modulus>zR23Lcg7qLekrQSuMLzipknJMf1lsnK36S3gpAyppZWRMyWhw6yMBe3POdUiYwyLHdCnzXLtCsOEzRmqCZ8KSa7DRV2PkOxUZgC/FBp8qvLE/IoYMkI8MK0mKZA609Wt1lYIYNBCBAdNLpNB2o9bsyhKnqMhUD17wzifCBhtI1bAIz7Lu546qQ3AqQsuPQS3iUtx2m2itaEOxm6sQHVjxun1qgHa4DnzXmC93h0lkBwIFx13wFPLwQNseLHtefOoFq7binQJMf2a4C8GSnV5EZlde1fDElim2gCDD2v1ypBzKeLpD+XeySwz5y0e5dTiIpgrEQNaSJlkeKyavVDJEw==</Modulus>
            <P>z6NWDr4kpFcXv+C7vhAdtF+wouLN1kkwvLaqvMR9PZ1zebpl+JB2WRAQ+GKTMvxsPVOJKEWgMFOaH61o+TF0ER3jhx35Y3hDWyCsBNmLne+8cTQZ2IYMGdUAr5kwA7NWFaXuysngCTX0uv8n/NzxtNIgjTk2hbEecl4An7NpD4M=</P>
            <Q>/OQBOe47ZKYaLGfRQ7MBqX0InFKBTiYBUT90K06BcUycs3dPtHbFSylC2lNqoZ5EsKm/EwxG2iR1iV08Pn0YUqkkot2SldEN/zi7IM+R6+qoEh67e9BDotaBVYcyAMZeOIeVab6bsNptCrVZJJEbG4pd2hlzBXobHWOhcpGGGzE=</Q>
            <DP>CszzQAcR/t5s5NU6ztMlyGkl1gM4M8pQ17Akx3btENRs2ksg3MIe9dkJGPJ/t6o7syDyRH8CqyDeJoGtsTCqWe2VFUUw4MSztDzJ1eK/CXMb+UN9iK7IWFulEn1EaxzcAHpGJpRDrBb7aPK20MdpZz964/2y4VdqPwa2v5fkgiM=</DP>
            <DQ>tldYIwMJRBsYCCibFKs/aYvD56L9pPF2RbTw+EDUyHKrEiPXDpIM05Lepzk6S2oosRDTutJBTAy5yt+1DoF4RjG7PstwZCGma/3lv1SrLGW35cjO+glCm2j7PEYM4c4mJbBtJR/QdoYn1W7I5brSxtrPrPBopOxUHK63vDn9biE=</DQ>
            <InverseQ>OMS8JMwPrHtpJK5oBONkaE8bnqO3alnmjtBoWfM6Kqo2tgE9Dse5gdZMKeV/zSnyZLpZdIrVnIVqxFZpuduy/Ah24MjNm30NMed8YRJJyjOFpHbixtOEQ9J7+AheUrY6aSqeuHV3S2jtcQQyhFQ5D07vbNXVKWb7pqfnXnb4fcA=</InverseQ>
            <D>A1FLuS3X6S6vNy0wNBGaCN6AppJWpsA8QUVAsOaTh94XbyBYsXWE/kOOan0MBVYamezaCfSl6NqotziHC4jK/c4Z0nFk9Q8dfljcvlJ/WbR7gyooogd4OcFTMLDogqyeFy1lDUvRBYeos+wl0IcpEIjCTDaPP2D/M3Ui/VqHyV6CYi8oa4KyAvmXQchIp+ARxzzGHO2VVUZOWRAxOBsHadsL5wY0Z6IVXaAL3aZEBPQqg+hzBcY0ih2HN6x09ElDTZaAoLMYLsc6c/Ofqnxlnhr4vFb/u4LZZs8c8lTS1l/5KSvO97G7zlFX3vHxM5LE1tNr2xRQyUTXh+5frrLFgQ==</D>
            </RSAParameters>
        """.trimIndent()

        val keyPair = KeyUtil.buildKeyPairFromXml(xml)

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
