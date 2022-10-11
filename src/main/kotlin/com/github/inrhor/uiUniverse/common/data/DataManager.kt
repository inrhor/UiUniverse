package com.github.inrhor.uiUniverse.common.data

import com.github.inrhor.uiUniverse.api.frame.UiFrame
import java.util.concurrent.ConcurrentHashMap

object DataManager {

    /**
     * 空间容器
     */
    val spaceContainer = ConcurrentHashMap<String, NameSpace>()

    /**
     * 模板容器
     */
    val templateContainer = ConcurrentHashMap<String, UiFrame>()

}