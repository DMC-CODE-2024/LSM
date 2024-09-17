package com.eirs.lsm.alert;

import java.util.Map;

public interface AlertService {

    void sendAlert(AlertIds alertIds, Map<AlertMessagePlaceholders, String> placeHolderMap);
}
