package org.ebics.client.ebicsrestapi.bank

import org.ebics.client.ebicsrestapi.ApiTestContext
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.Matchers.hasKey
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@WebAppConfiguration
@WebMvcTest
@ContextConfiguration(classes = [ApiTestContext::class])
@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
class EbicsBankResourceTest(@Autowired private val context: WebApplicationContext) {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation)).build()
    }

    @Test
    fun listBanks() {
        mockMvc.perform(get("/banks"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo{ MockMvcResultHandlers.print() }
            //assert MockBank data
            .andExpect(jsonPath("$.length()", `is`(2)))
            .andExpect(jsonPath("$[0].id", `is`(1)))
            .andExpect(jsonPath("$[1].id", `is`(2)))
            .andExpect(jsonPath("$[*].name", hasItems("Test bank 1", "Test bank 2")))
            .andExpect(jsonPath("$[*].bankURL", hasItems("https://ebics.ubs.com/ebicsweb/ebicsweb")))
            .andExpect(jsonPath("$[0]", hasKey("name")))
            .andExpect(jsonPath("$[*].hostId", hasItems("EBXUBSCH")))
            .andDo(document("banks"))
    }

    @Test
    fun getBankById() {
        mockMvc.perform(get("/banks/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo{ MockMvcResultHandlers.print() }
            //assert MockBank data
            .andExpect(jsonPath("$.id", `is`(1)))
            .andExpect(jsonPath("$.name", `is`("Test bank 1")))
            .andExpect(jsonPath("$.bankURL", `is`("https://ebics.ubs.com/ebicsweb/ebicsweb")))
            .andExpect(jsonPath("$", hasKey("name")))
            .andExpect(jsonPath("$.hostId", `is`("EBXUBSCH")))
            .andDo(document("banks/{id}"))
    }

}
