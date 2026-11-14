package egovframework.com.cmm;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

class ApiExceptionHandlerHttpStatusTest {

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.standaloneSetup(new TestController())
				.setControllerAdvice(new ApiExceptionHandler()).build();
	}

	@Test
	void malformedJsonKeepsBadRequestStatus() throws Exception {
		mvc.perform(post("/status-test/body").contentType(MediaType.APPLICATION_JSON).content("{broken"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.resultCode").value(400))
				.andExpect(content().string(not(containsString("HttpMessageNotReadableException"))));
	}

	@Test
	void parameterTypeMismatchKeepsBadRequestStatus() throws Exception {
		mvc.perform(get("/status-test/typed").param("value", "not-an-integer"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.resultCode").value(400));
	}

	@Test
	void explicitResponseStatusIsPreservedWithoutReasonLeak() throws Exception {
		mvc.perform(get("/status-test/missing"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.resultCode").value(404))
				.andExpect(content().string(not(containsString("sensitive-detail"))));
	}

	@Test
	void unexpectedRuntimeExceptionRemainsInternalServerError() throws Exception {
		mvc.perform(get("/status-test/crash"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.resultCode").value(500))
				.andExpect(content().string(not(containsString("sensitive-detail"))));
	}

	@RestController
	static class TestController {
		@PostMapping("/status-test/body")
		Map<String, String> body(@RequestBody Map<String, String> body) {
			return body;
		}

		@GetMapping("/status-test/typed")
		int typed(@RequestParam("value") int value) {
			return value;
		}

		@GetMapping("/status-test/missing")
		void missing() {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "sensitive-detail");
		}

		@GetMapping("/status-test/crash")
		void crash() {
			throw new IllegalStateException("sensitive-detail");
		}
	}
}
