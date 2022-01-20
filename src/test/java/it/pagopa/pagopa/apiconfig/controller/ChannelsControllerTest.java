package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.service.ChannelsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockChannelDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockChannels;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class ChannelsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ChannelsService channelsService;

    @BeforeEach
    void setUp() {
        when(channelsService.getChannels(anyInt(), anyInt(), any(FilterAndOrder.class))).thenReturn(getMockChannels());
        when(channelsService.getChannel(anyString())).thenReturn(getMockChannelDetails());
        when(channelsService.createChannel(any(ChannelDetails.class))).thenReturn(getMockChannelDetails());
        when(channelsService.updateChannel(anyString(), any(ChannelDetails.class))).thenReturn(getMockChannelDetails());
    }

    @Test
    void getChannels() throws Exception {
        String url = "/channels?page=0";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getChannel() throws Exception {
        String url = "/channels/1234";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void createChannel() throws Exception {
        mvc.perform(post("/channels")
                        .content(TestUtil.toJson(getMockChannelDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void createChannel_400() throws Exception {
        mvc.perform(post("/channels")
                        .content(TestUtil.toJson(getMockChannelDetails().toBuilder()
                                .brokerPspCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateChannel() throws Exception {
        mvc.perform(put("/channels/1234")
                        .content(TestUtil.toJson(getMockChannelDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateChannel_400() throws Exception {
        mvc.perform(put("/channels/1234")
                        .content(TestUtil.toJson(getMockChannelDetails().toBuilder()
                                .brokerPspCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteChannel() throws Exception {
        mvc.perform(delete("/channels/1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
