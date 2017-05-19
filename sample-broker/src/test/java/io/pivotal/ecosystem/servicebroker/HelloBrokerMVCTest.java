package io.pivotal.ecosystem.servicebroker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.ecosystem.servicebroker.service.CatalogService;
import io.pivotal.ecosystem.servicebroker.service.InstanceService;
import io.pivotal.ecosystem.servicebroker.service.ServiceInstanceRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.servicebroker.controller.ServiceInstanceController;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloBrokerMVCTest {

    private static final String ID = "deleteme";

    private MockMvc mockMvc;

    @Autowired
    private HelloBroker helloBroker;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    private HelloBrokerRepository mockBrokerRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ServiceInstanceController(catalogService, new InstanceService(catalogService, helloBroker, serviceInstanceRepository)))
                .build();
        serviceInstanceRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        serviceInstanceRepository.deleteAll();
    }

    @Test
    public void testLifecycle() throws Exception {
        User user = new User("test", "pw", User.Role.Broker);
        when(mockBrokerRepository.provisionUser(any(User.class))).thenReturn(user);
        this.mockMvc.perform(put("/v2/service_instances/" + ID)
                .content(toJson(TestConfig.createRequest(ID, false)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        when(mockBrokerRepository.updateUser(user)).thenReturn(user);
        this.mockMvc.perform(patch("/v2/service_instances/" + ID)
                .content(toJson(TestConfig.updateRequest(ID, false)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        this.mockMvc.perform(delete("/v2/service_instances/" + ID + "?service_id=" + TestConfig.SD_ID + "&plan_id=" + TestConfig.PLAN_ID)
                .content(toJson(TestConfig.deleteRequest(ID, false)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private String toJson(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
