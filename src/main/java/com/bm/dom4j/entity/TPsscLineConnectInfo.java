package com.bm.dom4j.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * T_PSSC_LINE_CONNECT_INFO
 * @author 
 */
@Data
public class TPsscLineConnectInfo implements Serializable {
    /**
     * 唯一标识
     */
    private String objId;

    /**
     * 大馈线标识
     */
    private String feederId;

    /**
     * 联络大馈线标识
     */
    private String connFeederId;

    /**
     * 联络设备标识
     */
    private String connDeviceId;

    /**
     * 联络设备类型
     */
    private String connDeviceType;

    /**
     * 数据创建时间
     */
    private Date insTime = new Date();

    /**
     * 数据更新时间
     */
    private Date updateTime = new Date();

    private static final long serialVersionUID = 1L;

    public TPsscLineConnectInfo() {

    }

    public TPsscLineConnectInfo(String objId, String connFeederId) {
        this.objId = objId;
        this.connFeederId = connFeederId;
        this.updateTime = new Date();
    }
}