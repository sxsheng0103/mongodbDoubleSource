package com.fileupload.yzmslide.cache;

public interface ICacheBalanceStrategy {

    /**
     * 设置均衡基于的资源列表（如文件列表或URL列表等）
     *
     * @param repositories
     */
    void setBalanceRepositories(String[] repositories);

    /**
     * 根据均衡值依据特定策略确定资源值。
     *
     * @param balanceValue
     * @return
     */
    String getBalanceRepository(int balanceValue);
}
