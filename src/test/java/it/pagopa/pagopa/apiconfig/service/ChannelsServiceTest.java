package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCanali;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockChannelDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIntermediariePsp;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockWfespPluginConf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class ChannelsServiceTest {

    @MockBean
    private CanaliRepository canaliRepository;

    @MockBean
    private IntermediariPspRepository intermediariPspRepository;

    @MockBean
    private WfespPluginConfRepository wfespPluginConfRepository;

    @Autowired
    @InjectMocks
    private ChannelsService channelsService;


    @Test
    void getChannels() throws IOException, JSONException {
        Page<Canali> page = TestUtil.mockPage(Lists.newArrayList(getMockCanali()), 50, 0);
        when(canaliRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        Channels result = channelsService.getChannels(50, 0, getMockFilterAndOrder(Order.Channel.CODE));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_channels_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getChannel() throws IOException, JSONException {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));

        ChannelDetails result = channelsService.getChannel("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_channel_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getChannel_notFound() {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());

        try {
            channelsService.getChannel("1234");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createChannel() throws IOException, JSONException {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());
        when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
        when(intermediariPspRepository.findByIdIntermediarioPsp(anyString())).thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
        when(wfespPluginConfRepository.findByIdServPlugin(anyString())).thenReturn(Optional.ofNullable(getMockWfespPluginConf()));


        ChannelDetails result = channelsService.createChannel(getMockChannelDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_channel_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createChannel_conflict() {
        when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
        when(intermediariPspRepository.findByIdIntermediarioPsp(anyString())).thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
        when(wfespPluginConfRepository.findByIdServPlugin(anyString())).thenReturn(Optional.ofNullable(getMockWfespPluginConf()));

        ChannelDetails mockChannelDetails = getMockChannelDetails();
        try {
            channelsService.createChannel(mockChannelDetails);
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateChannel() throws IOException, JSONException {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
        when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
        when(intermediariPspRepository.findByIdIntermediarioPsp(anyString())).thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
        when(wfespPluginConfRepository.findByIdServPlugin(anyString())).thenReturn(Optional.ofNullable(getMockWfespPluginConf()));


        ChannelDetails result = channelsService.updateChannel("1234", getMockChannelDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_channel_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateChannel_notFound() {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());
        try {
            channelsService.updateChannel("1234", getMockChannelDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteChannel() {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));

        channelsService.deleteChannel("1234");
        assertTrue(true);
    }

    @Test
    void deleteChannel_notfound() {
        when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());

        try {
            channelsService.deleteChannel("1234");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

}
