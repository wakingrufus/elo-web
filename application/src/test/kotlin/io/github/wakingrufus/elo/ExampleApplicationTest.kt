package io.github.wakingrufus.elo

import io.github.wakingrufus.elo.league.LeaguePersistence
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.client.MockMvcWebTestClient
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.reactive.function.BodyInserters
import java.nio.charset.StandardCharsets

@SpringBootTest(
    classes = [EloApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["logging.level.org.springframework.security=DEBUG"]
)
class ExampleApplicationTest {
    private val log = KotlinLogging.logger {}
    private lateinit var client: WebTestClient

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var leaguePersistence: LeaguePersistence

    @BeforeEach
    fun setup() {
        client = MockMvcWebTestClient
            .bindToApplicationContext(context)
            .apply(springSecurity())
            .defaultRequest(get("/").with(SecurityMockMvcRequestPostProcessors.csrf()))
            .configureClient()
            .build()
    }

    @Test
    fun test() {
        val response = client.get()
            .uri("/index")
            .accept(MediaType.TEXT_HTML)
            .exchange()
        val getResult = response.returnResult(String::class.java)
        assertThat(getResult.status).isEqualTo(HttpStatus.OK)
        log.info { getResult.responseBodyContent?.toString(StandardCharsets.UTF_8) }

        val createResponse = client.post()
            .uri("/leagues")
            .headers { it.setBasicAuth("user", "default") }
            .accept(MediaType.TEXT_HTML)
            .body(BodyInserters.fromFormData("name", "bob"))
            .exchange()
        val createResult = createResponse.returnResult(String::class.java)
        assertThat(createResult.status).isEqualTo(HttpStatus.OK)
        log.info { createResult.responseBodyContent?.toString(StandardCharsets.UTF_8) }

        val getResponse = client.get()
            .uri("/leagues/" + leaguePersistence.listLeagues().first().first)
            .accept(MediaType.TEXT_HTML)
            .exchange()
        log.info { getResponse.returnResult(String::class.java).responseBodyContent?.toString(StandardCharsets.UTF_8) }
    }

//    @Test
//    fun `test health`() {
//        val response = client.get()
//            .uri("/actuator/health")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//        val getResult = response.returnResult(String::class.java)
//        assertThat(getResult.status).isEqualTo(HttpStatus.OK)
//        log.info { getResult.responseBodyContent?.toString(StandardCharsets.UTF_8) }
//    }
}