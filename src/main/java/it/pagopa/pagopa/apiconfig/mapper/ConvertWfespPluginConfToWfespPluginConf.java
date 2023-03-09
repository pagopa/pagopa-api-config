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
    var output =
        it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf.builder()
            .idServPlugin(wfespPluginConf.getIdServPlugin())
            .build();
    output.setIdBean(CommonUtil.deNull(wfespPluginConf.getIdBean()));
    output.setProfiloPagConstString(CommonUtil.deNull(wfespPluginConf.getProfiloPagConstString()));
    output.setProfiloPagSoapRule(CommonUtil.deNull(wfespPluginConf.getProfiloPagSoapRule()));
    output.setProfiloPagRptXpath(CommonUtil.deNull(wfespPluginConf.getProfiloPagRptXpath()));
    return output;
  }
}
