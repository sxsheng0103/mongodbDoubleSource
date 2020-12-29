package com.fileupload.iec.app.commons.cache;

public interface ICacheBalanceValue {
    /**
     * 按照缓存的KEY计算均衡的值，注意：该方法不考虑实际的均衡策略。
     *
     * @return
     */
    int getBalanceVaule();
}
