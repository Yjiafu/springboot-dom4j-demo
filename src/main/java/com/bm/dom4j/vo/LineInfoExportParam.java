package com.bm.dom4j.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yin jiafu
 * @Date: 2021/9/10 16:19
 * @Description:
 */
@Data
public class LineInfoExportParam implements Serializable {
    /**
     * 大馈线标识
     */
    @ExcelProperty(value = {"大馈线id"},index = 0)
    private String feederId;

    /**
     * 大馈线标识
     */
    @ExcelProperty(value = {"大馈线名称"},index = 1)
    private String feederName;

    /**
     * 联络设备标识
     */
    @ExcelProperty(value = {"联络开关id"},index = 2)
    private String connDeviceId;

    /**
     * 联络设备标识
     */
    @ExcelProperty(value = {"联络开关名称"},index = 3)
    private String connDeviceName;

    private static final long serialVersionUID = 1L;

}
