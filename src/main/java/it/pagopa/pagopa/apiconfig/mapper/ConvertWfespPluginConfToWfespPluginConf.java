package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertWfespPluginConfToWfespPluginConf
    implements Converter<
        WfespPluginConf, it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf> {
  @Override
  public it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf convert(
      MappingContext<
              WfespPluginConf, it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf>
          mappingContext) {
    @Valid WfespPluginConf wfespPluginConf = mappingContext.getSource();
    return it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf.builder()
        .idBean(CommonUtil.deNull(wfespPluginConf.getIdBean()))
        .idServPlugin(wfespPluginConf.getIdServPlugin())
        .profiloPagConstString(CommonUtil.deNull(wfespPluginConf.getProfiloPagConstString()))
        .profiloPagSoapRule(CommonUtil.deNull(wfespPluginConf.getProfiloPagSoapRule()))
        .profiloPagRptXpath(CommonUtil.deNull(wfespPluginConf.getProfiloPagRptXpath()))
        .build();
  }
}
