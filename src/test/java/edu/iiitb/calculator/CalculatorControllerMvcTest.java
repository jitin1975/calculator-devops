package edu.iiitb.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerMvcTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void addEndpointReturnsResult() throws Exception {
        mvc.perform(get("/api/v1/add?a=2&b=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    void divideByZeroReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/divide?a=6&b=0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingParameterReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/add?a=2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nonNumericParameterReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/v1/add?a=foo&b=3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void healthEndpointIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
