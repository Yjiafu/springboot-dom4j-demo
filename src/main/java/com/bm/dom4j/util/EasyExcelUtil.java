package com.bm.dom4j.util;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EasyExcelUtil {

  public static void easyExcel(String fileName, List list, Class clazz, HttpServletResponse response) throws IOException {

    OutputStream out = null;
    ExcelWriter write = null;
    try {
      String name = fileName;
      String excelName = new String(name.getBytes("utf-8"),"iso-8859-1")+".xls";
      response.addHeader("Content-Disposition","attachment;fileName="+excelName);
      out = response.getOutputStream();
      write = new ExcelWriter(out,ExcelTypeEnum.XLS);
      List<List> lt = Lists.partition(list,50000);
      for(int i=0;i<lt.size();i++){
        Sheet sheet = new Sheet((i+1),0,clazz,"sheet"+(i+1),null);
        write.write(lt.get(i),sheet);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      out.flush();
      write.finish();
      out.close();
    }
  }

  public static <T> List easyExcelMsg(Class clazz,HttpServletRequest request) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    List<T> list = new ArrayList<>();
    List<Map<String, String>> dataList = JSONArray.fromObject(request.getParameter("data"));
    Field[] userFields = clazz.getDeclaredFields();

    if (dataList != null && dataList.size() > 0) {
      for (int i = 0; i <dataList.size(); i++) {
        Map<String,String> map = dataList.get(i);
        T t = (T) clazz.newInstance();
        for (String s : map.keySet()) {
          for(int j = 0; j<userFields.length ; j++){
            if (userFields[j].getName().equals(s)){
              Method method = clazz.getMethod("set" + s.substring(0, 1).toUpperCase() + s.substring(1), userFields[j].getType());
              method.invoke(t,map.get(s));
            }
          }
        }
        list.add(t);
      }
    }
    return list;
  }
}
