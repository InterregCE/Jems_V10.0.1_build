package io.cloudflight.ems.workpackage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
class WorkPackageControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Autowired
    private lateinit var jsonMapper: ObjectMapper

//    @Test
//    @Transactional
//    fun `get work package by id`() {
//        mockMvc.perform(
//            get("/api/work_package/1")
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//        )
//
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(UserFactory.ADMINISTRATOR_EMAIL))
//    }
}