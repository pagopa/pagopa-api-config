package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;

public class ConvertWfespPluginConfToWfespPluginConf
    implements Converter<
        WfespPluginConf, it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf> {
  @Override
  public it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf convert(
      MappingContext<
              WfespPluginConf, it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf>
          mappingContext) {
    @Valid WfespPluginConf wfespPluginConf = mappingContext.getSource();
    var output =
        it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf.builder()
            .idServPlugin(wfespPluginConf.getIdServPlugin())
            .build();
    output.setIdBean(CommonUtil.deNull(wfespPluginConf.getIdBean()));
    output.setProfiloPagConstString(CommonUtil.deNull(wfespPluginConf.getProfiloPagConstString()));
    output.setProfiloPagSoapRule(CommonUtil.deNull(wfespPluginConf.getProfiloPagSoapRule()));
    output.setProfiloPagRptXpath(CommonUtil.deNull(wfespPluginConf.getProfiloPagRptXpath()));
    return output;
  }
}
