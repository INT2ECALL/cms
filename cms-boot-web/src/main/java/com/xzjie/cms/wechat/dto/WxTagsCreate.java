package com.xzjie.cms.wechat.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.HashMap;

@Data
public class WxTagsCreate {
    private Tag tag = new Tag();

    public static WxTagsCreate builder() {
        return new WxTagsCreate();
    }

    public String build() {
        return JSON.toJSONString(this);
    }


    public WxTagsCreate setId(Long id) {
        tag.put("id", id);
        return this;
    }

    public WxTagsCreate setName(String name) {
        tag.put("name", name);
        return this;
    }

    private class Tag extends HashMap<String, Object> {
        public Tag() {
        }

    }

}
