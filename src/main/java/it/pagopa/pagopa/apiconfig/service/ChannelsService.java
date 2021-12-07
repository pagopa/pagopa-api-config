package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.Channel;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelsService {

    @Autowired
    CanaliRepository canaliRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Channels getChannels(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Canali> page = canaliRepository.findAll(pageable);
        return Channels.builder()
                .channelList(getChannelList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public ChannelDetails getChannel(@NotBlank String channelCode) {
        Canali canali = canaliRepository.findByIdCanale(channelCode)
                .orElseThrow(() -> new AppException(AppError.CHANNEL_NOT_FOUND, channelCode));
        return modelMapper.map(canali, ChannelDetails.class);
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


}
