package com.retailcore.tests;

import com.retailcore.orders.controller.AdminOrdersController;
import com.retailcore.orders.controller.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "retailcore.runtime-mode=h2")
@ActiveProfiles("h2")
class WebEndpointsIntegrationTest {
    @Autowired OrderController orders;
    @Autowired AdminOrdersController admin;

    // Health must identify RetailCore, and an unknown order must return HTTP 404.
    @Test
    void shouldReturnHealthResponseAndNotFoundForUnknownOrder() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(orders).build();
        mvc.perform(get("/health")).andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("retailcore"));
        mvc.perform(get("/orders/999999")).andExpect(status().isNotFound());
    }

    // The admin controller must provide the orders view and a model attribute for its table.
    @Test
    void shouldRenderAdminOrdersViewWithOrdersModel() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(admin).build();
        mvc.perform(get("/admin/orders")).andExpect(status().isOk())
                .andExpect(view().name("admin/orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
