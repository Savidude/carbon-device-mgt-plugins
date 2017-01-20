package org.wso2.bleagent.api.util;

import org.json.simple.JSONObject;

/**
 * Created by wso2123 on 11/8/16.
 */
public class Action {
    private String type;
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("value", value);
        return jsonObject.toString();
    }
}
