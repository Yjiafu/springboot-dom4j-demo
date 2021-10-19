package com.bm.dom4j.service;

import com.alibaba.excel.EasyExcel;
import com.bm.dom4j.dao.TPsscLineConnectInfoDao;
import com.bm.dom4j.entity.TPsscLineConnectInfo;
import com.bm.dom4j.util.EasyExcelUtil;
import com.bm.dom4j.vo.LineInfoExportParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Author: yin jiafu
 * @Date: 2021/9/9 11:25
 * @Description:
 */
@Service
@Slf4j
public class XmlOperateService implements ApplicationRunner {

    @Value("${xml.path:D:/xml}")
    public String xmlPath;

    @Autowired
    private TPsscLineConnectInfoDao lineConnectInfoDao;

    private static final List<String> stringList = new ArrayList<>();

    /**
     * 获取所有的.svg格式的文件路径
     * @param file
     */
    private void getSvgPath(File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                //System.out.println("Dir==>" + f.getAbsolutePath());
                getSvgPath(f);
            } else {
                //这里将列出所有的文件
                if (f.getName().endsWith(".xml")){
                    stringList.add(f.getAbsolutePath());
                }
                //System.out.println("file==>" + f.getAbsolutePath());
            }
        }
    }

    /**
     * 获取指定文件的流
     * @param fileName
     * @return
     */
    public String getFileString(String fileName){
        try {
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            return new String(data, "utf-8");
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public LineInfoExportParam getNodeInfo(String fileString) {
        LineInfoExportParam exportParam=new LineInfoExportParam();
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(fileString.toString().getBytes("utf-8")));
            //获取根节点
            Element root = doc.getRootElement();

            List<Element> elements = root.elements();
            Iterator<Element> iterators = elements.iterator();
            while (iterators.hasNext()){
                Element element = iterators.next();
                String qualifiedName = element.getQualifiedName();
                if (qualifiedName.equals("cim:Feeder")){
                    List<Element> elements1 = element.elements();
                    Iterator<Element> iterators1 = elements1.iterator();
                    while (iterators1.hasNext()){
                        Element element1 = iterators1.next();
                        if (element1.getQualifiedName().equals("sgcim:Feeder.IsCurrentFeeder")){
                            String text = element1.getText();
                            if (text.equals("true")){
                                List<Attribute> attributes = element.attributes();
                                Iterator<Attribute> iterator = attributes.iterator();
                                while (iterator.hasNext()){
                                    Attribute next = iterator.next();
                                    boolean equals = next.getQualifiedName().equals("rdf:ID");
                                    if (equals){
                                        String value = next.getValue();
                                        exportParam.setFeederId(value);
                                        System.out.println("线路的id是"+value);
                                        System.out.println("大馈线的id是29DKX-"+value.split("_")[2]);
                                    }
                                }
                                List<Element> elements11 = element1.getParent().elements();
                                for (Element element2:elements11){
                                    boolean equals = element2.getQualifiedName().equals("cim:IdentifiedObject.name");
                                    if (equals){
                                        String graphName=element2.getText();
                                        exportParam.setFeederName(graphName);
                                        //String lineName=StringUtils.substringAfterLast(graphName, "kV");
                                        //String feederId = lineConnectInfoDao.getLineIdByLineName(lineName);
                                        //lineConnectInfo.setFeederId(feederId);
                                        System.out.println("线路名称是---------"+graphName);
                                    }
                                }
                            }
                        }
                    }
                }
                if (qualifiedName.equals("cim:Breaker")){
                    List<Element> elements1 = element.elements();
                    Iterator<Element> iterators1 = elements1.iterator();
                    while (iterators1.hasNext()){
                        Element element1 = iterators1.next();
                        if (element1.getQualifiedName().equals("cim:Switch.normalOpen")) {
                            String text = element1.getText();
                            if (text.equals("true")){

                                List<Attribute> attributes = element.attributes();
                                Iterator<Attribute> iterator = attributes.iterator();
                                while (iterator.hasNext()){
                                    Attribute next = iterator.next();
                                    boolean equals = next.getQualifiedName().equals("rdf:ID");
                                    if (equals){
                                        String value = next.getValue();
                                        exportParam.setConnDeviceId(value);
                                        System.out.println("联络开关的id是"+value);
                                    }
                                }

                                List<Element> elements11 = element1.getParent().elements();
                                for (Element element2:elements11){
                                    boolean equals = element2.getQualifiedName().equals("cim:IdentifiedObject.name");
                                    if (equals){
                                        String connName=element2.getText();
                                        exportParam.setConnDeviceName(connName);
                                        System.out.println("联络开关的名称是---------"+connName);
                                    }
                                }
                            }else {
                                exportParam.setConnDeviceId("无");
                                exportParam.setConnDeviceName("无");
                            }
                        }

                    }
                }

            }

        }catch (DocumentException d){
            d.printStackTrace();
        }catch (UnsupportedEncodingException u){
            u.printStackTrace();
        }
        return exportParam;
    }

    public TPsscLineConnectInfo getEntityInfo(String fileString) {
        TPsscLineConnectInfo exportParam=new TPsscLineConnectInfo();
        exportParam.setObjId(UUID.randomUUID().toString().replaceAll("-","").toUpperCase());
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(fileString.toString().getBytes("utf-8")));
            //获取根节点
            Element root = doc.getRootElement();

            List<Element> elements = root.elements();
            Iterator<Element> iterators = elements.iterator();
            while (iterators.hasNext()){
                Element element = iterators.next();
                String qualifiedName = element.getQualifiedName();
                if (qualifiedName.equals("cim:Feeder")){
                    List<Element> elements1 = element.elements();
                    Iterator<Element> iterators1 = elements1.iterator();
                    while (iterators1.hasNext()){
                        Element element1 = iterators1.next();
                        if (element1.getQualifiedName().equals("sgcim:Feeder.IsCurrentFeeder")){
                            String text = element1.getText();
                            if (text.equals("true")){
                                List<Attribute> attributes = element.attributes();
                                Iterator<Attribute> iterator = attributes.iterator();
                                while (iterator.hasNext()){
                                    Attribute next = iterator.next();
                                    boolean equals = next.getQualifiedName().equals("rdf:ID");
                                    if (equals){
                                        String value = next.getValue();
                                        String feederId = lineConnectInfoDao.getFeederIdByOidAndSbzlx(value.split("_")[2], value.split("_")[1]);
                                        exportParam.setFeederId(feederId);
                                    }
                                }
                            }
                        }
                    }
                }
                if (qualifiedName.equals("cim:Breaker")){
                    List<Element> elements1 = element.elements();
                    Iterator<Element> iterators1 = elements1.iterator();
                    while (iterators1.hasNext()){
                        Element element1 = iterators1.next();
                        if (element1.getQualifiedName().equals("cim:Switch.normalOpen")) {
                            String text = element1.getText();
                            if (text.equals("true")) {
                                List<Attribute> attributes = element.attributes();
                                Iterator<Attribute> iterator = attributes.iterator();
                                while (iterator.hasNext()) {
                                    Attribute next = iterator.next();
                                    boolean equals = next.getQualifiedName().equals("rdf:ID");
                                    if (equals) {
                                        String value = next.getValue();
                                        String sbid = lineConnectInfoDao.getSbidByOid(value.split("_")[2]);
                                        String sblx = lineConnectInfoDao.getSblxAndTxbmBySblxZx(value.split("_")[1].substring(0, 6));
                                        exportParam.setConnDeviceType(sblx);
                                        exportParam.setConnDeviceId(sbid);
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }catch (DocumentException d){
            d.printStackTrace();
        }catch (UnsupportedEncodingException u){
            u.printStackTrace();
        }
        return exportParam;
    }

    public void insertConnInfo(){
        /*File file=new File(xmlPath);
        if (stringList.isEmpty()){
            getSvgPath(file);
            List<LineInfoExportParam> exportParamList=new ArrayList<>();
            for (String s : stringList){
                String fileString = getFileString(s);
                LineInfoExportParam exportParam = getNodeInfo(fileString);

                exportParamList.add(exportParam);
            }
            String pathName = "C:/Users/admin/Desktop/write.xlsx";
            EasyExcel.write(pathName,LineInfoExportParam.class).sheet("单线图信息").doWrite(exportParamList);
        }*/
        File file=new File(xmlPath);
        if (stringList.isEmpty()){
            getSvgPath(file);
            for (String s : stringList){
                String fileString = getFileString(s);
                TPsscLineConnectInfo exportParam = getEntityInfo(fileString);
                String connDeviceId = exportParam.getConnDeviceId();
                List<TPsscLineConnectInfo> connInfo = lineConnectInfoDao.getLineConnInfoByDeviceID(connDeviceId);
                if (connInfo.isEmpty()){
                    int i = lineConnectInfoDao.insertSelective(exportParam);
                    if (i == 1){
                        log.info("插入成功");
                    }else {
                        log.error("插入失败");
                    }
                }else {
                    for (TPsscLineConnectInfo info : connInfo) {
                        if (info.getFeederId().equals(exportParam.getFeederId())) {
                            log.info("不做处理");
                        } else {
                            if (info.getConnFeederId() == null || info.getConnFeederId() == "") {
                                int i = lineConnectInfoDao.updateByPrimaryKeySelective(new TPsscLineConnectInfo(info.getObjId(), info.getFeederId()));
                                if (i == 1) {
                                    log.info("更新失败");
                                } else {
                                    log.error("更新失败");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        insertConnInfo();
    }

    public static void main(String[] args) {
        String uuid=UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
        //System.out.println(uuid);

        String kV = StringUtils.substringAfterLast("10kV黄羊滩变542基荣线", "kV");
        System.out.println(kV);
    }
}
