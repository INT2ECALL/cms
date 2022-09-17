package com.xzjie.cms.service;

import com.xzjie.cms.core.service.BaseService;
import com.xzjie.cms.enums.VerifyCodeScenes;
import com.xzjie.cms.enums.VerifyCodeType;
import com.xzjie.cms.model.VerifyCode;

import java.util.List;
import java.util.Map;

public interface VerifyCodeService extends BaseService<VerifyCode> {

    VerifyCode save(String target, String value, VerifyCodeScenes scenes, VerifyCodeType type, String message);

    void sendMail(String email, VerifyCodeScenes scenes, String verifyValue, Map<String, Object> data, String templateName);

    void sendMail(String email);

    boolean validated(String code, String email, VerifyCodeScenes emailChange, VerifyCodeType email1);

    List<VerifyCode> getVerifyCodeExpiration();
}
