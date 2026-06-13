package com.emi.alertengine.service;

import java.util.Map;
//uses freemarker template engine to render templates based on templateId and payload
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateEngine {

  private final Configuration freemarkerConfig;

  public String render(String templateId, Map<String, String>payload) {
    try {
      Template template = freemarkerConfig.getTemplate(templateId + ".ftl");
      Map<String, String> safePayload = payload != null ? payload : Map.of();
     return FreeMarkerTemplateUtils.processTemplateIntoString(template, safePayload);
    } catch (Exception e) {
      throw new RuntimeException("Error occurred while rendering template", e);
    }
  }

}