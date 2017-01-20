package org.wso2.bleagent.api.database;

import org.wso2.bleagent.api.util.Action;

/**
 * Created by wso2123 on 11/8/16.
 */
public interface ActionTable {
    Action getAction(int profileId, int locationId);
}
