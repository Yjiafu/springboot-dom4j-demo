package com.bm.dom4j.dao;

import com.bm.dom4j.entity.TPsscLineConnectInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface TPsscLineConnectInfoDao {

    int insertSelective(TPsscLineConnectInfo record);

    TPsscLineConnectInfo selectByPrimaryKey(String objId);

    int updateByPrimaryKeySelective(TPsscLineConnectInfo record);

    List<TPsscLineConnectInfo> getLineConnInfoByDeviceID(String deviceId);

    String getLineIdByLineName(String lineName);

    String getFeederIdByOidAndSbzlx(@Param("oid") String oid, @Param("sbzlx") String sbzlx);

    String getSbidByOid(String oid);

    String getSblxAndTxbmBySblxZx(String SblxZx);
}