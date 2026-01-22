//package com.iprody.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import model.com.iprody.api.PaymentDto;
//import persistence.com.iprody.api.PaymentEntity;
//import persistence.com.iprody.api.PaymentRepository;
//import com.iprody.persistence.PaymentStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.Optional;
//import java.util.UUID;
//import static org.hamcrest.Matchers.containsString;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@AutoConfigureMockMvc
//class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void shouldReturnOnlyLiquibasePayments() throws Exception {
//        mockMvc.perform(get("/payments")
//                        .with(TestJwtFactory.jwtWithRole("test-user", "READER"))
//                        .param("page", "0")
//                        .param("size", "10")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(5))
//                .andExpect(jsonPath("$[0].currency").value("USD"))
//                .andExpect(jsonPath("$[0].amount").value(99.99))
//                .andExpect(jsonPath("$[0].note").value("Initial test payment"))
//                .andExpect(jsonPath("$[1].currency").value("USD"))
//                .andExpect(jsonPath("$[1].amount").value(99.99))
//                .andExpect(jsonPath("$[1].note").value("Test payment 1"));
//                //.andReturn();
//    }
//
//    @Test
//    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
//        objectMapper.registerModule(new JavaTimeModule());
//        PaymentDto dto = new PaymentDto();
//        dto.setGuid(UUID.fromString("4fa85f64-5717-4562-b3fc-2c963f66afc5"));
//        dto.setAmount(new BigDecimal("123.45"));
//        dto.setCurrency("EUR");
//        dto.setInquiryRefId(UUID.fromString("4fa85f64-5717-4562-b3fc-2c963f66afc5"));
//        dto.setStatus(PaymentStatus.PENDING);
//        dto.setNote("Integration test payment");
//        dto.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
//        dto.setUpdatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
//        String json = objectMapper.writeValueAsString(dto);
//        String response = mockMvc.perform(post("/payments/add")
//                        .with(TestJwtFactory.jwtWithRole("admin", "ADMIN"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.guid").exists())
//                .andExpect(jsonPath("$.currency").value("EUR"))
//                .andExpect(jsonPath("$.amount").value(123.45))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
//        Optional<PaymentEntity> saved = paymentRepository.findById(created.getGuid());
//        assertThat(saved).isPresent();
//        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
//        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
//    }
//
//    @Test
//    void shouldReturnPaymentById() throws Exception {
//        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
//        mockMvc
//                .perform(get("/payments/" + existingId)
//                        .with(TestJwtFactory.jwtWithRole("test-user", "USER"))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.guid").value(existingId.toString()))
//                .andExpect(jsonPath("$.currency").value("EUR"))
//                .andExpect(jsonPath("$.amount").value(50.00));
//    }
//    @Test
//    void shouldReturn404ForNonexistentPayment() throws Exception {
//        UUID nonexistentId = UUID.randomUUID();
//        mockMvc
//                .perform(get("/payments/" + nonexistentId)
//                        .with(TestJwtFactory.jwtWithRole("test-user", "READER"))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.error", containsString("Платеж не найден")))
//                .andExpect(jsonPath("$.timestamp").exists())
//                .andExpect(jsonPath("$.operation").value("get"))
//                .andExpect(jsonPath("$.entityId").value(nonexistentId.toString()));
//    }
//
//}
