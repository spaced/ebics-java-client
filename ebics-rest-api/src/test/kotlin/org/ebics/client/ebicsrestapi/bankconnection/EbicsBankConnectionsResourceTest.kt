package org.ebics.client.ebicsrestapi.bankconnection

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
class EbicsBankConnectionsResourceTest(@Autowired private val context: WebApplicationContext) {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(documentationConfiguration(restDocumentation)).build()
    }

    @Test
    fun bankConnectionsGet() {
        mockMvc.perform(get("/bankconnections").param("permission", "READ"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo{ MockMvcResultHandlers.print() }
            //assert MockUser data
            .andExpect(jsonPath("$.length()", `is`(2)))
            //.andExpect(jsonPath("$[0].id", `is`(1)))
            //.andExpect(jsonPath("$[1].id", `is`(2)))
            .andExpect(jsonPath("$[*].name", hasItems("UserId:1", "UserId:2")))
            .andExpect(jsonPath("$[*].userId", hasItems("CHT10001")))
            .andExpect(jsonPath("$[0]", hasKey("name")))
            .andExpect(jsonPath("$[*].partner.partnerId", hasItems("CH100001")))
            .andExpect(jsonPath("$[*].partner.bank.hostId", hasItems("EBXUBSCH")))
            .andDo(document("bankconnections"))
    }

    @Test
    fun bankConnectionGet() {
        mockMvc.perform(get("/bankconnections/1").param("permission", "READ"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo{ MockMvcResultHandlers.print() }
            //assert MockUser data
            //.andExpect(jsonPath("$.id",  `is`(null)))
            .andExpect(jsonPath("$.name", `is`("UserId:1")))
            .andExpect(jsonPath("$.userId", `is`("CHT10001")))
            .andExpect(jsonPath("$", hasKey("name")))
            .andExpect(jsonPath("$.partner.partnerId", `is`("CH100001")))
            .andExpect(jsonPath("$.partner.bank.hostId", `is`("EBXUBSCH")))
            .andDo(document("bankconnections/{id}"))
    }

}
