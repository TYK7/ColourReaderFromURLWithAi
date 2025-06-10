package com.example.colorschemeidentifier.web;

import com.example.colorschemeidentifier.model.ColorInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Using a generic controller an advice, not specific controllers.
// So, not using @WebMvcTest(controllers = TestController.class) to avoid component scan issues for service.
// Instead, manually build MockMvc with the controller and the advice.
// Update: A simpler way for testing @ControllerAdvice is to use @WebMvcTest on a dummy controller
// and Spring Boot will automatically pick up the @ControllerAdvice.

@WebMvcTest(controllers = RestExceptionHandlerTest.TestController.class)
public class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Dummy controller that can throw exceptions handled by RestExceptionHandler
    @Controller
    public static class TestController {
        @GetMapping("/test/illegalArgument")
        @ResponseBody
        public String throwIllegalArgumentException(@RequestParam String param) {
            if (param.equals("throw")) {
                throw new IllegalArgumentException("Test Illegal Argument");
            }
            return "OK";
        }

        @GetMapping("/test/genericException")
        @ResponseBody
        public String throwGenericException() {
            throw new RuntimeException("Test Generic Exception");
        }
    }

    // If RestExceptionHandler is not picked up automatically (e.g. not in component scan for tests)
    // we might need to configure it manually. But @WebMvcTest should pick it up if it's in a sub-package or same package.
    // For this structure, RestExceptionHandler is in com.example.colorschemeidentifier.web
    // and this test is in the same package.
    // Let's add it explicitly to be sure, or ensure component scan includes it.
    // Actually, @WebMvcTest will only load the specified controllers and supporting MVC infrastructure,
    // which includes @ControllerAdvice beans by default. So RestExceptionHandler should be picked up.

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test/illegalArgument").param("param", "throw"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Illegal Argument")))
                .andExpect(jsonPath("$[0].source", is("input_validation")));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test/genericException"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("An unexpected server error occurred.")))
                .andExpect(jsonPath("$[0].source", is("server_error")));
    }

    @Test
    void noException_shouldReturnOk() throws Exception {
        // Test that the dummy controller works without throwing an exception
        mockMvc.perform(get("/test/illegalArgument").param("param", "ok"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
