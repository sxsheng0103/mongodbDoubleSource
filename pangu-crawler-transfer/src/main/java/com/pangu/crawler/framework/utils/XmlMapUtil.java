package com.pangu.crawler.framework.utils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.*;

//import net.chnbs.delta.framework.json.XML;


/**
 * xml辅助类，提供map对象转换成xml字符串，xml字符串转换成map对象方法
 *
 * @author hant
 * @Description
 * @version V1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class XmlMapUtil {
    public static Map xml2Map(Document doc) throws DocumentException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();

        if (root.attributeValue("size") != null) {
            List rootList = new ArrayList();

            Map<String, Object> rootMap = Dom2Map(root);

            if (rootMap.size() == 1) {
                for (Map.Entry<String, Object> entry : rootMap.entrySet()) {
//					System.out.println(entry.getKey() + ": " + entry.getValue());
                    Object obj = entry.getValue();
                    if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                        rootList.add(entry.getValue());
                    } else {
                        rootList = (List) entry.getValue();
                    }
                }
            }
            map.put(root.getName(), rootList);
        } else {
            map = Dom2Map(root);
        }

        return map;
    }

    /**
     * 将原始xml字符串转换成map对象，
     * 如果节点中包含有size属性，该节点用List集合进行封装，含有size属性的节点作为key值存存储该集合
     * 如果同一节点下有多个同名节点，以数组接收
     *
     * @param xml
     * @return
     */
    public static Map xml2Map(String xml) throws DocumentException {
        Document doc = null;
        int result = StringUtils.indexOfAny(xml, new String[]{"&"});
        if (result != -1) {
            //含有特殊字符
            xml = xml.replace("&", "&amp;");
        }
        doc = DocumentHelper.parseText(xml);
/*		
          try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
*/
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();

        if (root.attributeValue("size") != null) {
            List rootList = new ArrayList();

            Map<String, Object> rootMap = Dom2Map(root);

            if (rootMap.size() == 1) {
                for (Map.Entry<String, Object> entry : rootMap.entrySet()) {
//					System.out.println(entry.getKey() + ": " + entry.getValue());
                    Object obj = entry.getValue();
                    if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                        rootList.add(entry.getValue());
                    } else {
                        rootList = (List) entry.getValue();
                    }
                }
            }
            map.put(root.getName(), rootList);
        } else {
            map = Dom2Map(root);
        }

        return map;
    }

    private static Map Dom2Map(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.attributeValue("size") != null) {
                    if (map.containsKey(iter.getName())) {
                        ((List) map.get(iter.getName())).add(Dom2Map(iter));
                    } else {
                        Map<String, Map> itemMap = Dom2Map(iter);

                        if (itemMap.size() == 1) {
                            Map tmp = new HashMap();
                            for (Map.Entry<String, Map> entry : itemMap.entrySet()) {
//								System.out.println(entry.getKey() + ": " + entry.getValue());
//								tmp.put(iter.getName(), entry.getValue());
                                Object ob = entry.getValue();
//								String ob_key = entry.getKey();
                                String ob_class = ob.getClass().getName();

                                if (ob.getClass().getName().equals("java.util.ArrayList")) {
//									tmp.put(iter.getName(), entry.getValue());
                                    map.put(iter.getName(), entry.getValue());
                                } else if (ob_class.equals("java.util.HashMap")) {
                                    tmp.putAll(entry.getValue());
                                    itemMap = tmp;
                                    List nlist = new ArrayList();
                                    nlist.add(itemMap);
                                    map.put(iter.getName(), nlist);
                                } else if (ob_class.equals("java.lang.String")) {

                                    map.put(iter.getName(), new ArrayList());    //如果该数组为空，则添加空数组实现
                                } else {


                                }
                            }
                        }
                    }
                } else if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    /**
     * xml转json格式字符
     *
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static String xml2json(String xml) throws DocumentException {
        Document doc = null;
        int result = StringUtils.indexOfAny(xml, new String[]{"&"});
        if (result != -1) {
            //含有特殊字符
            xml = xml.replace("&", "&amp;");
        }
        doc = DocumentHelper.parseText(xml);
/*
          try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
*/
        Map<String, Object> map = new HashMap<String, Object>();
        if (doc == null) {
            return "{}";
        }
        Element e = doc.getRootElement();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.attributeValue("size") != null) {
                    if (map.containsKey(iter.getName())) {
                        ((List) map.get(iter.getName())).add(Dom2Map(iter));
                    } else {
                        Map<String, Map> itemMap = Dom2Map(iter);

                        if (itemMap.size() == 1) {
                            Map tmp = new HashMap();
                            for (Map.Entry<String, Map> entry : itemMap.entrySet()) {
//								System.out.println(entry.getKey() + ": " + entry.getValue());
//								tmp.put(iter.getName(), entry.getValue());
                                Object ob = entry.getValue();
//								String ob_key = entry.getKey();
                                String ob_class = ob.getClass().getName();

                                if (ob.getClass().getName().equals("java.util.ArrayList")) {
//									tmp.put(iter.getName(), entry.getValue());
                                    map.put(iter.getName(), entry.getValue());
                                } else if (ob_class.equals("java.util.HashMap")) {
                                    tmp.putAll(entry.getValue());
                                    itemMap = tmp;
                                    List nlist = new ArrayList();
                                    nlist.add(itemMap);
                                    map.put(iter.getName(), nlist);
                                } else if (ob_class.equals("java.lang.String")) {

                                    map.put(iter.getName(), new ArrayList());    //如果该数组为空，则添加空数组实现
                                } else {


                                }
                            }
                        }
                    }
                } else if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());//"".equals(iter.getText())?"\"\"":
                }
            }
        } else {
            map.put(e.getName(), e.getText());//"".equals(e.getText())?"\"\"":
        }
        JSONObject json = new JSONObject();
        json.put(e.getName(), JSONObject.fromObject(map));
        return json.toString();
    }

    /**
     * xml-attribute转map
     * static String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><SCHEMA NAME=\"Result\" CNNAME=\"结果信息\" VERSION=\"1.0\"><DJRDXX NSRSBH=\"91370211730636852D\" NSRMC=\"青岛润凯机械制造有限公司\" CWFZRXM=\"张梅\" CYRS=\"20\" DJRQ=\"2001-12-04 00:00:00.0\" LRRQ=\"2001-12-04 10:37:00.0\" DJXH=\"10113702000071448522\" DJZCLXDM=\"159\" FDDBRXM=\"薛亮堂\" FJMQYBZ=\"N\" HSFSDM=\"11\" HYDM=\"3329\" KJZDZZDM=\"102\" NSRZTDM=\"03\" SCJYDZ=\"青岛市黄岛区隐珠街道办事处驻地\" SHXYDM=\"91370211730636852D\" SSGLYDM=\"23702840159\" WHSYJSFJFXXDJBZ=\"\" YGZNSRLXDM=\"32\" ZCDZ=\"青岛市黄岛区隐珠街道办事处驻地\" ZCZB=\"1020000.0\" ZFJGLXDM=\"0\" ZGSWJDM=\"13702840000\" ZGSWJMC=\"国家税务总局青岛市黄岛区税务局\" ZGSWSKFJDM=\"13702844000\" ZZJGLXDM=\"1\" ZZSJYLB=\"\" ZZSQYLXDM=\"\" DQRQ=\"2019-05-07\" CWBBNB_BZ=\"Y\" CKTSZG=\"Y\" NSRLX=\"Y\" XGMNSRJMBL_FCS=\"50\" XGMNSRJMBL_ZYS=\"50\" XGMNSRJMBL_WHJSF=\"50\" XGMNSRJMBL_CZTDSYS=\"50\" XGMNSRJMBL_JYFJS=\"50\" SFHDZSXGM=\"N\" SFXYH=\"3702600600088718\" JKZH=\"90209042020100046266\" SFWCS=\"N\" NSRHYFWJDCZG=\"Y\" SDSNDZDYLB=\"0\" SFQHJT=\"N\" SFQHJTYJD=\"N\" DWXZ_DM=\"1\" IS_SDH=\"\"/><DJSZ ZSXMDM=\"10101\" RDYXQQ=\"2016-05-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610606\" SZNSQX=\"06\" SZNSQXBYY=\"06\" SFXGMZYBNSR=\"N\" KKZT=\"N\" SBZT=\"N\" YZPZXH=\"\" SQSBZT=\"Y\" sb_sbqx0=\"N\" sb_jkqx0=\"N\"/><ZZSQCSXX zb_qmwjse1=\"0\" zb_bqybtse1=\"0\" zb_qmwjse3=\"0\" zb_bqybtse3=\"0\"/><BQZZSFP BQZZSFPFS=\"27\" BQZZSFPJE=\"1296029.04\" BQZZSFPSE=\"164968.94\" BQZZSZYFPFS=\"27\" BQZZSZYFPJE=\"1296029.04\" BQZZSZYFPSE=\"164968.94\" BQHYZYFPFS=\"0\" BQHYZYFPJE=\"0\" BQHYZYFPSE=\"0\" BQJDCFPFS=\"0\" BQJDCFPJE=\"0\" BQJDCFPSE=\"0\" BQTXFDZFPFS=\"0\" BQTXFDZFPJE=\"0\" BQTXFDZFPSE=\"0\"/><FDQDATA/><BQCBDATA ZYFPCXCODE=\"0\" ZYFPCXMSG=\"\" ZYFPSKZG=\"Y\" ZYFPSFCS=\"Y\" ZYFPCSJE=\"828458.41\" ZYFPCSSE=\"107699.59\" HYFPCXCODE=\"0\" HYFPCXMSG=\"\" HYFPZG=\"N\" HYFPJDCZG=\"N\" HYFPJDFPZG=\"N\" HYFPDZFPZG=\"N\" HYFPJDCCS=\"N\" HYFPJSFPCS=\"N\" HYFPDZFPCS=\"N\" HYFPJDCJE=\"0\" HYFPJDCSE=\"0\" HYFPJSFPJE=\"0\" HYFPJSFPSE=\"0\" HYFPDZFPJE=\"0\" HYFPDZFPSE=\"0\"/><DJSZ ZSXMDM=\"10104\" RDYXQQ=\"2003-01-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610994\" SZNSQX=\"10\" KKZT=\"N\" SBZT=\"N\" YZPZXH=\"\" sb_sbqx0=\"2019-05-31\" sb_jkqx0=\"2019-05-31\" nd_blkcjyjfndkce=\"0\"/><NDSDSQCSXX A105040SQTXBZ=\"N\" A105070SQTXBZ=\"N\"/><NDSDSJBXX/><DJSZ ZSXMDM=\"10104\" RDYXQQ=\"2003-01-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610922\" SZNSQX=\"10\" KKZT=\"N\" SBZT=\"N\" YZPZXH=\"\" sb_sbqx0=\"2019-05-31\" sb_jkqx0=\"2019-05-31\"/><GLYW/><DJSZ ZSXMDM=\"10111\" RDYXQQ=\"2010-03-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610794\" SZNSQX=\"06\" SZNSQXBYY=\"06\" SBZT=\"N\" YZPZXH=\"\" sb_zsxmDm0=\"10111\" sb_zspmDm0=\"101110101\" sb_hyDm0=\"3329\" sb_nsqxDm0=\"06\" sb_zsdlfsDm0=\"0\" sb_jkqxDm0=\"04\" sb_sbqxDm0=\"04\" sb_jkqx0=\"2019-05-21\" sb_yjze0=\"0.0\" sb_skssqq0=\"2019-04-01\" sb_skssqz0=\"2019-04-30\" sb_sbqx0=\"2019-05-21\" sb_sl10=\"3.0E-4\" sb_zsl0=\"3.0E-4\" sb_zgswskfjDm0=\"13702844000\" sb_swjgDm0=\"13702000000\" sb_yjse0=\"0.0\" sb_rdzsuuid0=\"0\" sb_rdpzuuid0=\"367DEA341E340082E0538C1B47192774\" sb_zfsbz0=\"0\" sb_hdbl0=\"0.5\" sb_hdse0=\"0.0\" sb_ysbje0=\"0.0\" sb_sfysb0=\"Y\" sb_rdyxqq0=\"2010-03-01 00:00:00\" sb_rdyxqz0=\"9999-12-31 00:00:00\" sb_wzsyhdjsyj0=\"0.0\" sb_qzd0=\"0.0\" sb_djxh0=\"10113702000071448522\" sb_wzsyjsyj0=\"0.0\" sb_wzsyhdse0=\"0.0\" sb_zsxmDm1=\"10111\" sb_zspmDm1=\"101110501\" sb_hyDm1=\"3329\" sb_nsqxDm1=\"06\" sb_zsdlfsDm1=\"0\" sb_jkqxDm1=\"04\" sb_sbqxDm1=\"04\" sb_jkqx1=\"2019-05-21\" sb_yjze1=\"0.0\" sb_skssqq1=\"2019-04-01\" sb_skssqz1=\"2019-04-30\" sb_sbqx1=\"2019-05-21\" sb_sl11=\"5.0E-4\" sb_zsl1=\"5.0E-4\" sb_zgswskfjDm1=\"13702844000\" sb_swjgDm1=\"13702000000\" sb_yjse1=\"0.0\" sb_rdzsuuid1=\"0\" sb_rdpzuuid1=\"367DEA341E350082E0538C1B47192774\" sb_zfsbz1=\"0\" sb_hdbl1=\"0.0\" sb_hdse1=\"0.0\" sb_ysbje1=\"0.0\" sb_sfysb1=\"Y\" sb_rdyxqq1=\"2010-03-01 00:00:00\" sb_rdyxqz1=\"9999-12-31 00:00:00\" sb_wzsyhdjsyj1=\"0.0\" sb_qzd1=\"0.0\" sb_djxh1=\"10113702000071448522\" sb_wzsyjsyj1=\"0.0\" sb_wzsyhdse1=\"0.0\" sb_zsxmDm2=\"10111\" sb_zspmDm2=\"101110599\" sb_hyDm2=\"3329\" sb_nsqxDm2=\"06\" sb_zsdlfsDm2=\"0\" sb_jkqxDm2=\"04\" sb_sbqxDm2=\"04\" sb_jkqx2=\"2019-05-21\" sb_yjze2=\"0.0\" sb_skssqq2=\"2019-04-01\" sb_skssqz2=\"2019-04-30\" sb_sbqx2=\"2019-05-21\" sb_sl12=\"5.0\" sb_zsl2=\"5.0\" sb_zgswskfjDm2=\"13702844000\" sb_swjgDm2=\"13702000000\" sb_yjse2=\"0.0\" sb_rdzsuuid2=\"0\" sb_rdpzuuid2=\"367DEA341E360082E0538C1B47192774\" sb_zfsbz2=\"0\" sb_hdbl2=\"0.0\" sb_hdse2=\"0.0\" sb_ysbje2=\"0.0\" sb_sfysb2=\"Y\" sb_rdyxqq2=\"2010-03-01 00:00:00\" sb_rdyxqz2=\"9999-12-31 00:00:00\" sb_wzsyhdjsyj2=\"0.0\" sb_qzd2=\"0.0\" sb_djxh2=\"10113702000071448522\" sb_wzsyjsyj2=\"0.0\" sb_wzsyhdse2=\"0.0\" kdqsssrfpBz=\"N\"/><YHSQC yhshd_hdjguuid0=\"77DD8F745306013EE0538C1B480C1F99\" yhshd_sqspuuid0=\"29400000000000237210000364580872\" yhshd_djxh0=\"10113702000071448522\" yhshd_zsxmDm0=\"10111\" yhshd_zspmDm0=\"101110101\" yhshd_hdqxq0=\"2018-10-01 00:00:00\" yhshd_hdqxz0=\"9999-12-31 00:00:00\" yhshd_sjzxqz0=\"9999-12-31 00:00:00\" yhshd_yhszspzlxDm0=\"99\" yhshd_hdlx20=\"2\" yhshd_hdde0=\"0.0\" yhshd_hdbl0=\"0.5\" yhshd_zgswskfjDm0=\"13702844000\" yhshd_lrrq0=\"2018-10-10 17:34:26\" yhshd_lrrDm0=\"23702000000\" yhshd_xgrq0=\"2018-10-10 17:34:26\"/><DJSZ ZSXMDM=\"10109\" RDYXQQ=\"2001-12-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610678\" SZNSQX=\"06\" SBZT=\"N\" YZPZXH=\"\"/><DJSZ ZSXMDM=\"30203\" RDYXQQ=\"2001-12-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610678\" SZNSQX=\"06\" SBZT=\"N\" YZPZXH=\"\"/><DJSZ ZSXMDM=\"30216\" RDYXQQ=\"2001-12-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610678\" SZNSQX=\"06\" SBZT=\"N\" YZPZXH=\"\"/><DJSZ ZSXMDM=\"30221\" RDYXQQ=\"2001-12-01 00:00:00\" YXBZ=\"Y\" PZZLDM=\"BDA0610678\" SZNSQX=\"06\" SBZT=\"N\" YZPZXH=\"\"/></SCHEMA>";
     */
    public static Map xmlAttr2Map(String xml) {
        Document doc = null;
        int result = StringUtils.indexOfAny(xml, new String[]{"&"});
        if (result != -1) {
            //含有特殊字符
            xml = xml.replace("&", "&amp;");
        }
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        List<Element> le = root.elements();
        int len = le.size();
        Map<String, Object> resultMap = new HashMap();
        for (int i = 0; i < len; i++) {
            Element e = le.get(i);
            String key = e.getQName().getName();
            if (resultMap.containsKey(key)) {
                //若含该key，说明为list
                if ("java.util.ArrayList".equals(resultMap.get(key).getClass().getName())) {
                    //已经是list
                    List l = (List) resultMap.get(key);
                    Map valMap = new HashMap();
                    int c = e.attributeCount();
                    for (int j = 0; j < c; j++) {
                        valMap.put(e.attribute(j).getQName().getName(), e.attribute(j).getText());
                    }
                    l.add(valMap);
                    resultMap.put(key, l);
                } else {
                    //还是map
                    List l = new ArrayList();
                    Map valMap = new HashMap();
                    int c = e.attributeCount();
                    for (int j = 0; j < c; j++) {
                        valMap.put(e.attribute(j).getQName().getName(), e.attribute(j).getText());
                    }
                    l.add(resultMap.get(key));
                    l.add(valMap);
                    resultMap.put(key, l);
                }
            } else {
                //反之为map
                Map valMap = new HashMap();
                int c = e.attributeCount();
                for (int j = 0; j < c; j++) {
                    valMap.put(e.attribute(j).getQName().getName(), e.attribute(j).getText());
                }
                resultMap.put(key, valMap);
            }

        }
        return resultMap;
    }

    /**
     * Map对象toXml字符串
     *
     * @param _obj     map对象
     * @param rootName xml根节点
     * @return
     */
    public static String map2XML(Map _obj, String rootName) {
        // log.info("---MapToXmlString--map-->"+_obj);
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append("<" + rootName + ">");
        xml.append(toXML(_obj));
        xml.append("</" + rootName + ">");
        // log.info("--返回客户端----xml-->"+xml.toString());
        return xml.toString();
    }

    public static String map2XML(Map _obj, String rootName, String attr, String attValue) {
        // log.info("---MapToXmlString--map-->"+_obj);
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append("<" + rootName + " " + attr + "=\"" + attValue + "\">");
        xml.append(toXML(_obj));
        xml.append("</" + rootName + ">");
        // log.info("--返回客户端----xml-->"+xml.toString());
        return xml.toString();
    }

    /**
     * Map对象toXml字符串
     *
     * @param _obj
     * @return
     */
    public static String map2XML(Map _obj) {
        // log.info("---MapToXmlString--map-->"+_obj);
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append(toXML(_obj));
        // log.info("--返回客户端----xml-->"+xml.toString());
        return xml.toString();
    }

    public static String toXML(Map _obj) {
        StringBuffer sb = new StringBuffer();
        if (_obj != null) {
            Set keySet = _obj.keySet();
            for (Iterator it = keySet.iterator(); it.hasNext(); ) {
                Object key = it.next();
                Object value = _obj.get(key);
                if (value instanceof Map) {
                    sb.append(toXMLMap((Map) value, key));
                } else if (value instanceof Collection) {
                    sb.append(toXMLCollection((Collection) value, key));
                }
                //else if(value instanceof String){
                else {
                    sb.append(toXMLOtherObject(value, key));
                }
            }
        }
        return sb.toString();
    }

    private static String toXMLCollection(Collection _list, Object key) {
        StringBuffer sb = new StringBuffer();
        if (_list != null) {
            //sb.append("<").append(key).append(" size='"+_list.size()+"'>").append("\n");
//			sb.append("<").append(key).append("LIST>").append("\n");
            for (Iterator it = _list.iterator(); it.hasNext(); ) {
                sb.append("<").append(key).append(">").append("\n");
                sb.append(toXML((Map) it.next()));
                sb.append("</").append(key).append(">").append("\n");
            }
            //sb.append("</").append(key).append(">").append("\n ");
//			sb.append("</").append(key).append("LIST>").append("\n ");
        }
        return sb.toString();
    }

    private static String toXMLMap(Map _map, Object node) {
        StringBuffer sb = new StringBuffer();
        if (_map != null) {
            sb.append("<").append(node).append(">").append("\n");
            sb.append(toXML(_map));
            sb.append("</").append(node).append(">").append("\n");
        }
        return sb.toString();
    }

    private static String toXMLOtherObject(Object _obj, Object key) {
        StringBuffer sb = new StringBuffer();
        if (_obj != null) {
            sb.append("<").append(key).append(">");
//			sb.append(XML.escape(_obj.toString()));
            sb.append(_obj.toString());
            sb.append("</").append(key).append("> ").append("\n ");
        }
        return sb.toString();
    }

    /**
     * 实体转xml
     *
     * @param obj
     * @return
     */
    public static String voToXml(Object obj) {
        String xml = "";
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            String jg = writer.toString();
            xml = jg.replaceAll(" standalone=\"yes\"", "");
        } catch (JAXBException e) {
            log.error("数据VO转XML报错", e);
            throw new RuntimeException("数据VO转XML报错" + e);
        }
        return xml;
    }

    /***
     * 格式化xml为string
     *
     * @param document
     * @return
     * @throws
     */
    public static String prettysString(Document document) throws Exception {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(document.getXMLEncoding());
        StringWriter stringWriter = new StringWriter();
        XMLWriter writer = new XMLWriter(stringWriter, format);
        writer.write(document);
        writer.close();
        return stringWriter.toString();
    }
}
