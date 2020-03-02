package com.study91.audiobook.update;

/**
 * 更新接口
 */
public interface IUpdate {
    /**
     * 更新
     * @param isStarted 是否启动状态（true=启动状态，false=不是启动状态）
     */
    void update(boolean isStarted);
}
