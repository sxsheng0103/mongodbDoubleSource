package com.pangu.unicom.base.domain;

import java.util.List;

public interface BaseSz extends BaseEnum {

    String getShowName();

    void setShowName(String showName);

    List<BaseForm> getFormList();

}