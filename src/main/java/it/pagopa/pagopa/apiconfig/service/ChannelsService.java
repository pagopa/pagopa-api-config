package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.Channel;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getFilters;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getSort;

@Service
public class ChannelsService {

    @Autowired
    CanaliRepository canaliRepository;

    @Autowired
    IntermediariPspRepository intermediariPspRepository;

    @Autowired
    WfespPluginConfRepository wfespPluginConfRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Channels getChannels(@NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, getSort(filterAndOrder));
        Example<Canali> filters = getFilters(filterAndOrder, Canali.class);
        Page<Canali> page = canaliRepository.findAll(filters, pageable);
        return Channels.builder()
                .channelList(getChannelList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public ChannelDetails getChannel(@NotBlank String channelCode) {
        Canali canali = getCanaliIfExists(channelCode);
        return modelMapper.map(canali, ChannelDetails.class);
    }

    public ChannelDetails createChannel(ChannelDetails channelDetails) {
        if (canaliRepository.findByIdCanale(channelDetails.getChannelCode()).isPresent()) {
            throw new AppException(AppError.CHANNEL_CONFLICT, channelDetails.getChannelCode());
        }

        // add info for model mapper
        setInfoMapper(channelDetails);

        // convert and save
        var entity = modelMapper.map(channelDetails, Canali.class);
        canaliRepository.save(entity);
        return channelDetails;
    }


    public ChannelDetails updateChannel(String channelCode, ChannelDetails channelDetails) {
        Long objId = getCanaliIfExists(channelCode).getId();

        // add info for model mapper
        setInfoMapper(channelDetails);

        var entity = modelMapper.map(channelDetails, Canali.class)
                .toBuilder()
                .id(objId)
                .build();
        canaliRepository.save(entity);
        return channelDetails;
    }

    public void deleteChannel(String channelCode) {
        Canali canale = getCanaliIfExists(channelCode);

        canaliRepository.delete(canale);
    }

    /**
     * @param channelCode code of the channel
     * @return search on DB using the {@code channelCode} and return the Canali if it is present
     * @throws AppException if not found
     */
    private Canali getCanaliIfExists(String channelCode) {
        return canaliRepository.findByIdCanale(channelCode)
                .orElseThrow(() -> new AppException(AppError.CHANNEL_NOT_FOUND, channelCode));
    }

    /**
     * Maps Canali objects stored in the DB in a List of Channel
     *
     * @param page page of {@link Canali} returned from the database
     * @return a list of {@link Channel}.
     */
    private List<Channel> getChannelList(Page<Canali> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Channel.class))
                .collect(Collectors.toList());
    }

    /**
     * Add info for model mapper
     *
     * @param channelDetails channel details form request
     */
    private void setInfoMapper(ChannelDetails channelDetails) {
        var intermediariPsp = intermediariPspRepository.findByIdIntermediarioPsp(channelDetails.getBrokerPspCode())
                .orElseThrow(() -> new AppException(AppError.BROKER_PSP_NOT_FOUND, channelDetails.getBrokerPspCode()));
        channelDetails.setFkIntermediarioPsp(intermediariPsp);

        if (channelDetails.getServPlugin() != null) {
            var wfespPluginConf = wfespPluginConfRepository.findByIdServPlugin(channelDetails.getServPlugin())
                    .orElseThrow(() -> new AppException(AppError.SERV_PLUGIN_NOT_FOUND, channelDetails.getBrokerPspCode()));

            channelDetails.setFkWfespPluginConf(wfespPluginConf);
        }
    }
}
