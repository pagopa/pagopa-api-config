package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertWfespPluginConfToWfespPluginConf
    implements Converter<
        WfespPluginConf, it.gov.pagopa.apiconfig.model.configuration.WfespPluginConf> {
  @Override
  public it.gov.pagopa.apiconfig.model.configuration.WfespPluginConf convert(
      MappingContext<
              WfespPluginConf, it.gov.pagopa.apiconfig.model.configuration.WfespPluginConf>
          mappingContext) {
    @Valid WfespPluginConf wfespPluginConf = mappingContext.getSource();
    var output =
        it.gov.pagopa.apiconfig.model.configuration.WfespPluginConf.builder()
            .idServPlugin(wfespPluginConf.getIdServPlugin())
            .build();
    output.setIdBean(CommonUtil.deNull(wfespPluginConf.getIdBean()));
    output.setProfiloPagConstString(CommonUtil.deNull(wfespPluginConf.getProfiloPagConstString()));
    output.setProfiloPagSoapRule(CommonUtil.deNull(wfespPluginConf.getProfiloPagSoapRule()));
    output.setProfiloPagRptXpath(CommonUtil.deNull(wfespPluginConf.getProfiloPagRptXpath()));
    return output;
  }
}
