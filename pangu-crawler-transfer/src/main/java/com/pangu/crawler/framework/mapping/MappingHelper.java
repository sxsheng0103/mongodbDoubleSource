package com.pangu.crawler.framework.mapping;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class MappingHelper {
    private MappingHelper() {
    }

    public static final MappingType DEFAULT_MAPPING_TYPE = MappingType.JSON;

    public static final boolean DEFAULT_TRUNCATE = true;

    public static final int DEFAULT_PRECISION = 2;

    public static final boolean DEFAULT_WITH_NULL = false;

    public static final ValueType DEFAULT_VALUE_TYPE = ValueType.DYNAMIC;

    private static final String MAPPING_COMMENT_FLAG = "//";

    private static final String MAPPING_FROM_IGNORE_FLAG = "*";

    private static final String MAPPING_FIRST_LINE_FLAG = "##";

    private static final String MAPPING_STRING_VALUE_TYPE_FLAG = "{s}";

    private static final String MAPPING_NUMERIC_VALUE_TYPE_FLAG = "{n}";

    public static class Mapping {
        private String name;

        private String version;

        private MappingType type;

        private boolean truncate;

        private int precision;

        private String precisionPointTail;

        private boolean withNull;

        private List<MappingElement> elements;

        public Mapping(String name,
                       String version,
                       MappingType type,
                       boolean truncate,
                       int precision,
                       boolean withNull,
                       List<MappingElement> elements) {
            this.name = name;
            this.version = version;
            this.type = type;
            this.truncate = truncate;
            this.precision = precision;
            StringBuilder zeros = new StringBuilder(".");
            for (int i = 0; i < precision; i++) {
                zeros.append("0");
            }
            this.precisionPointTail = zeros.toString();
            this.withNull = withNull;
            this.elements = Collections.unmodifiableList(elements);
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public MappingType getType() {
            return type;
        }

        public boolean isTruncate() {
            return truncate;
        }

        public int getPrecision() {
            return precision;
        }

        public String getPrecisionPointTail() {
            return precisionPointTail;
        }

        public boolean isWithNull() {
            return withNull;
        }

        public List<MappingElement> getElements() {
            return elements;
        }

        @Override
        public String toString() {
            return "Mapping{" +
                    "name='" + name + '\'' +
                    ", version='" + version + '\'' +
                    ", type=" + type +
                    ", truncate=" + truncate +
                    ", precision=" + precision +
                    ", precisionPointTail='" + precisionPointTail + '\'' +
                    ", withNull=" + withNull +
                    ", elements=" + elements +
                    '}';
        }
    }

    public static class MappingElement {

        private String comment;

        private String fromJPath;

        private String toPath;

        private ValueType type;

        private MappingElement(String comment, String fromJPath, String toPath, ValueType type) {
            this.comment = comment;
            this.fromJPath = fromJPath;
            this.toPath = toPath;
            this.type = type;
        }

        public String getComment() {
            return comment;
        }

        public String getFromJPath() {
            return fromJPath;
        }

        public String getToPath() {
            return toPath;
        }

        public ValueType getType() {
            return type;
        }

        @Override
        public String toString() {
            return "MappingElement{" +
                    "comment='" + comment + '\'' +
                    ", fromJPath='" + fromJPath + '\'' +
                    ", toPath='" + toPath + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public static enum MappingType {
        JSON, XML;
    }

    public static enum ValueType {
        STRING, NUMERIC, DYNAMIC;
    }

    public static Mapping parse(String mappingName, List<String> mappingLines) throws Exception {
        System.out.printf("MappingHelper.parse start! mappingName = %s\r\n", mappingName);
        if (mappingName == null) {
            throw new IllegalArgumentException("mappingName is null in MappingHelper.parse!");
        }
        if (mappingName.trim().isEmpty()) {
            throw new IllegalArgumentException("mappingName is empty in MappingHelper.parse!");
        }
        if (mappingLines == null) {
            throw new IllegalArgumentException("mappingLines is null in MappingHelper.parse!");
        }
        if (mappingLines.isEmpty()) {
            throw new IllegalArgumentException("mappingLines is empty in MappingHelper.parse!");
        }
        if (mappingLines.size() == 1) {
            throw new IllegalArgumentException("mappingLines is only one line in MappingHelper.parse!");
        }
        String version = "";
        MappingType mappingType = DEFAULT_MAPPING_TYPE;
        boolean truncate = DEFAULT_TRUNCATE;
        int precision = DEFAULT_PRECISION;
        boolean withNull = DEFAULT_WITH_NULL;
        String firstLine = mappingLines.get(0);
        if (firstLine == null) {
            throw new Exception(String.format("mapping first line is null! mappingName = %s", mappingName));
        }
        firstLine = firstLine.trim();
        if (firstLine.isEmpty()) {
            throw new Exception(String.format("mapping first line is empty! mappingName = %s", mappingName));
        }
        if (!firstLine.startsWith(MAPPING_FIRST_LINE_FLAG)) {
            throw new Exception(String.format("mapping first line error(1-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
        }
        firstLine = firstLine.substring(MAPPING_FIRST_LINE_FLAG.length());
        String[] firstLineMaps = firstLine.split(",");
        for (String firstLineMap : firstLineMaps) {
            String[] firstLineMapKV = firstLineMap.split(":");
            if (firstLineMapKV.length != 2) {
                throw new Exception(String.format("mapping first line error(2-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
            String firstLineMapK = firstLineMapKV[0];
            if (firstLineMapK == null) {
                throw new Exception(String.format("mapping first line error(3-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
            firstLineMapK = firstLineMapK.trim();
            if (firstLineMapK.isEmpty()) {
                throw new Exception(String.format("mapping first line error(3-1)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
            String firstLineMapV = firstLineMapKV[1];
            if (firstLineMapV == null) {
                throw new Exception(String.format("mapping first line error(3-2)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
            firstLineMapV = firstLineMapV.trim();
            if (firstLineMapV.isEmpty()) {
                throw new Exception(String.format("mapping first line error(3-3)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
            if ("version".equalsIgnoreCase(firstLineMapK)) {
                version = firstLineMapV;
            } else if ("type".equalsIgnoreCase(firstLineMapK)) {
                if ("json".equalsIgnoreCase(firstLineMapV)) {
                    mappingType = MappingType.JSON;
                } else if ("xml".equalsIgnoreCase(firstLineMapV)) {
                    mappingType = MappingType.XML;
                } else {
                    throw new Exception(String.format("mapping first line error(4-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
                }
            } else if ("truncate".equalsIgnoreCase(firstLineMapK)) {
                if ("true".equalsIgnoreCase(firstLineMapV)) {
                    truncate = true;
                } else if ("false".equalsIgnoreCase(firstLineMapV)) {
                    truncate = false;
                } else {
                    throw new Exception(String.format("mapping first line error(4-1)! mappingName = %s, firstLine = %s", mappingName, firstLine));
                }
            } else if ("precision".equalsIgnoreCase(firstLineMapK)) {
                try {
                    if (Integer.parseInt(firstLineMapV) <= 0) {
                        throw new Exception("precision must more than zero!");
                    }
                } catch (Exception e) {
                    throw new Exception(String.format("mapping first line error(4-2)! mappingName = %s, firstLine = %s", mappingName, firstLine));
                }
            } else if ("withNull".equalsIgnoreCase(firstLineMapK)) {
                if ("true".equalsIgnoreCase(firstLineMapV)) {
                    withNull = true;
                } else if ("false".equalsIgnoreCase(firstLineMapV)) {
                    withNull = false;
                } else {
                    throw new Exception(String.format("mapping first line error(4-3)! mappingName = %s, firstLine = %s", mappingName, firstLine));
                }
            } else {
                throw new Exception(String.format("mapping first line error(5-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
            }
        }
        if (version.isEmpty()) {
            throw new Exception(String.format("mapping first line error(6-0)! mappingName = %s, firstLine = %s", mappingName, firstLine));
        }
        List<MappingElement> mappingElements = new ArrayList<>();
        Set<String> toPathsInMappingForDuplicationCheck = new HashSet<>();
        Set<String> fromJPathsInMappingForDuplicationCheck = new HashSet<>();
        for (int i = 1; i < mappingLines.size(); i++) {
            String mappingLine = mappingLines.get(i);
            if (mappingLine == null) {
                throw new Exception(String.format("mapping line is null! mappingName = %s, lineNo = %d", mappingName, i));
            }
            mappingLine = mappingLine.trim();
            if (mappingLine.isEmpty()) {
                System.out.printf("mapping line is empty, skip! mappingName = %s, lineNo = %d\r\n", mappingName, i);
                continue;
            }
            if (mappingLine.startsWith(MAPPING_COMMENT_FLAG)) {
                System.out.printf("mapping line is all comment, skip! mappingName = %s, lineNo = %d, line = %s\r\n", mappingName, i, mappingLine);
                continue;
            }
            String rule = "";
            String comment = "";
            String fromJPath = "";
            String toPath = "";
            ValueType type = DEFAULT_VALUE_TYPE;
            int commentIndex = mappingLine.indexOf(MAPPING_COMMENT_FLAG);
            if (commentIndex > 0) {
                rule = mappingLine.substring(0, commentIndex);
                comment = mappingLine.substring(commentIndex + MAPPING_COMMENT_FLAG.length());
            } else {
                rule = mappingLine;
                comment = mappingLine;
            }
            int splitIndex = rule.indexOf("=");
            if (splitIndex <= 0) {
                throw new Exception(String.format("mapping line error(0-0)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
            }
            fromJPath = rule.substring(0, splitIndex).trim();
            if (fromJPath.isEmpty()) {
                throw new Exception(String.format("mapping line error(1-0)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
            }
            if (fromJPathsInMappingForDuplicationCheck.contains(fromJPath)) {
                throw new Exception(String.format("mapping line error(1-1)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
            } else {
                fromJPathsInMappingForDuplicationCheck.add(fromJPath);
            }
            toPath = rule.substring(splitIndex + "=".length()).trim();
            if (toPath.isEmpty()) {
                throw new Exception(String.format("mapping line error(1-2)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
            }
            if (toPath.startsWith(MAPPING_STRING_VALUE_TYPE_FLAG)) {
                type = ValueType.STRING;
                toPath = toPath.substring(MAPPING_STRING_VALUE_TYPE_FLAG.length()).trim();
                if (toPath.isEmpty()) {
                    throw new Exception(String.format("mapping line error(2-0)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
                }
            } else if (toPath.startsWith(MAPPING_NUMERIC_VALUE_TYPE_FLAG)) {
                type = ValueType.NUMERIC;
                toPath = toPath.substring(MAPPING_NUMERIC_VALUE_TYPE_FLAG.length()).trim();
                if (toPath.isEmpty()) {
                    throw new Exception(String.format("mapping line error(2-1)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
                }
            } else {
                type = ValueType.DYNAMIC;
            }
            if (toPathsInMappingForDuplicationCheck.contains(toPath)) {
                throw new Exception(String.format("mapping line error(1-3)! mappingName = %s, lineNo = %d, line = %s", mappingName, i, mappingLine));
            } else {
                toPathsInMappingForDuplicationCheck.add(toPath);
            }
            mappingElements.add(new MappingElement(comment.trim(), fromJPath, toPath, type));
        }
        Mapping mapping = new Mapping(mappingName, version, mappingType, truncate, precision, withNull, mappingElements);
        System.out.printf("MappingHelper.parse end! mappingName = %s, mapping = %s\r\n", mappingName, mapping);
        return mapping;
    }

    public static String mapping(String uuid,
                                 Mapping mapping,
                                 SortedSet<String> jpathsFromBw,
                                 JSONObject jsonObjectFromBw,
                                 String toBw) throws Exception {
        System.out.printf("MappingHelper.mapping start! uuid = %s\r\n", uuid);
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is null in MappingHelper.mapping!");
        }
        if (jpathsFromBw == null) {
            throw new IllegalArgumentException("jpathsFromBw is null in MappingHelper.mapping!");
        }
        if (jpathsFromBw.isEmpty()) {
            throw new IllegalArgumentException("jpathsFromBw is empty in MappingHelper.mapping!");
        }
        if (jsonObjectFromBw == null) {
            throw new IllegalArgumentException("jsonObjectFromBw is null in MappingHelper.mapping!");
        }
        if (toBw == null) {
            throw new IllegalArgumentException("toBw is null in MappingHelper.mapping!");
        }
        if (toBw.trim().isEmpty()) {
            throw new IllegalArgumentException("toBw is empty in MappingHelper.mapping!");
        }
        StringBuilder unmatchFromJPaths = new StringBuilder("");
        List<MappingElement> mappingElements = mapping.getElements();
        for (MappingElement mappingElement : mappingElements) {
            String fromJPath = mappingElement.getFromJPath();
            if (!MAPPING_FROM_IGNORE_FLAG.equals(fromJPath) && !jpathsFromBw.contains(fromJPath)) {
                unmatchFromJPaths.append(fromJPath).append("; ");
            }
        }
        if (unmatchFromJPaths.length() > 0) {
            throw new Exception("unmatch from jpaths : " + unmatchFromJPaths);
        }
        if (mapping.getType() == MappingType.JSON) {
            JSONObject jsonObjectToBw = JsonHelper.jsonStringToObject(toBw);
            // SortedSet<String> jpathsToBw = JsonHelper.mappingJPaths(jsonObjectToBw);
            mappingToJson(uuid, mapping, jsonObjectFromBw, jsonObjectToBw);
            String result = JsonHelper.jsonObjectToString(jsonObjectToBw, mapping.isWithNull());
            System.out.printf("MappingHelper.mapping end! uuid = %s, result = %s\r\n", uuid, result);
            return result;
        } else if (mapping.getType() == MappingType.XML) {
            XmlHelper.DocumentNode documentNodeToBw = XmlHelper.parse(toBw);
            String result = mappingToXML(uuid, mapping, jsonObjectFromBw, documentNodeToBw);
            System.out.printf("MappingHelper.mapping end! uuid = %s, result = %s\r\n", uuid, result);
            return result;
        } else {
            throw new Exception("mapping type is error! type is " + mapping.getType());
        }
    }

    private static void mappingToJson(String uuid,
                                      Mapping mapping,
                                      JSONObject jsonObjectFromBw,
                                      JSONObject jsonObjectToBw) throws Exception {
        System.out.printf("MappingHelper.mappingToJson start! uuid = %s\r\n", uuid);
        String mappingName = mapping.getName();
        List<MappingElement> mappingElements = mapping.getElements();
        for (MappingElement mappingElement : mappingElements) {
            String comment = mappingElement.getComment();
            String fromJPath = mappingElement.getFromJPath();
            String toPath = mappingElement.getToPath();
            ValueType valueType = mappingElement.getType();
            if (MAPPING_FROM_IGNORE_FLAG.equals(fromJPath)) {
                if (!JSONPath.set(jsonObjectToBw, toPath, null)) {
                    throw new Exception(String.format("【%s】转换失败：[%s] → [%s]，值：NULL！mappingName = %s",
                            comment, fromJPath, toPath, mappingName));
                }
                System.out.printf("【%s】转换完成：[%s] → [%s]，值：NULL！uuid = %s, mappingName = %s\r\n",
                        comment, fromJPath, toPath, uuid, mappingName);
            } else {
                Object fromValueObject = JSONPath.eval(jsonObjectFromBw, fromJPath);
                if (fromValueObject == null) {
                    if (!JSONPath.set(jsonObjectToBw, toPath, null)) {
                        throw new Exception(String.format(" 【%s】转换失败：[%s] → [%s]，值：NULL！mappingName = %s",
                                comment, fromJPath, toPath, mappingName));
                    }
                    System.out.printf(" 【%s】转换完成：[%s] → [%s]，值：NULL！uuid = %s, mappingName = %s\r\n",
                            comment, fromJPath, toPath, uuid, mappingName);
                } else {
                    Object finalFromValue = null;
                    String finalValueType = null;
                    String fromValue = fromValueObject.toString();
                    Object toValueObject = JSONPath.eval(jsonObjectToBw, toPath);
                    if (valueType == null || valueType == ValueType.DYNAMIC) {
                        if (toValueObject == null) {
                            throw new Exception(String.format("【%s】转换失败，toPath[%s]对应的类型无法确定！mappingName = %s",
                                    comment, toPath, mappingName));
                        } else {
                            if (toValueObject instanceof String) {
                                valueType = ValueType.STRING;
                            } else {
                                valueType = ValueType.NUMERIC;
                            }
                        }
                    }
                    if (valueType == ValueType.STRING) {
                        finalFromValue = fromValue;
                        finalValueType = valueType.name();
                        if (!JSONPath.set(jsonObjectToBw, toPath, fromValue)) {
                            throw new Exception(String.format("【%s】转换失败：[%s] → [%s]，值：[%s]，类型：[%s]！mappingName = %s",
                                    comment, fromJPath, toPath, finalFromValue, finalValueType, mappingName));
                        }
                    } else if (valueType == ValueType.NUMERIC) {
                        BigDecimal decimal;
                        try {
                            decimal = new BigDecimal(fromValue).setScale(mapping.getPrecision(), RoundingMode.HALF_UP);
                        } catch (Exception e) {
                            throw new Exception(String.format("【%s】转换失败，fromJPath[%s]对应的类型不是数值，值：[%s]！mappingName = %s",
                                    comment, fromJPath, fromValue, mappingName));
                        }
                        if (mapping.isTruncate() && decimal.toString().endsWith(mapping.getPrecisionPointTail())) {
                            finalFromValue = decimal.toBigInteger();
                            finalValueType = valueType.name() + "_TRUNCATE[." + mapping.getPrecision() + "]";
                            if (!JSONPath.set(jsonObjectToBw, toPath, finalFromValue)) {
                                throw new Exception(String.format("【%s】转换失败：[%s] → [%s]，值：[%s]，类型：[%s]！mappingName = %s",
                                        comment, fromJPath, toPath, finalFromValue, finalValueType, mappingName));
                            }
                        } else {
                            finalFromValue = decimal;
                            finalValueType = valueType.name() + "_ORIGIN[." + mapping.getPrecision() + "]";
                            if (!JSONPath.set(jsonObjectToBw, toPath, finalFromValue)) {
                                throw new Exception(String.format("【%s】转换失败：[%s] → [%s]，值：[%s]，类型：[%s]！mappingName = %s",
                                        comment, fromJPath, toPath, finalFromValue, finalValueType, mappingName));
                            }
                        }
                    }
                    System.out.printf("【%s】转换完成：[%s] → [%s]，值：[%s]，类型：[%s]！uuid = %s, mappingName = %s\r\n",
                            comment, fromJPath, toPath, finalFromValue, finalValueType, uuid, mappingName);
                }
            }
        }
        System.out.printf("MappingHelper.mappingToJson end! uuid = %s\r\n", uuid);
    }

    private static String mappingToXML(String uuid,
                                       Mapping mapping,
                                       JSONObject jsonObjectFromBw,
                                       XmlHelper.DocumentNode documentNodeToBw) throws Exception {
        System.out.printf("MappingHelper.mappingToXML start! uuid = %s\r\n", uuid);
        String mappingName = mapping.getName();
        List<MappingElement> mappingElements = mapping.getElements();
        for (MappingElement mappingElement : mappingElements) {
            String comment = mappingElement.getComment();
            String fromJPath = mappingElement.getFromJPath();
            String toPath = mappingElement.getToPath();
            ValueType valueType = mappingElement.getType();
            if (MAPPING_FROM_IGNORE_FLAG.equals(fromJPath)) {
                XmlHelper.removeElement(documentNodeToBw.getDocument(), toPath);
                System.out.printf("【%s】转换完成：[%s] → [%s]，值：NULL！uuid = %s, mappingName = %s\r\n",
                        comment, fromJPath, toPath, uuid, mappingName);
            } else {
                Object fromValueObject = JSONPath.eval(jsonObjectFromBw, fromJPath);
                if (fromValueObject == null) {
                    XmlHelper.removeElement(documentNodeToBw.getDocument(), toPath);
                    System.out.printf(" 【%s】转换完成：[%s] → [%s]，值：NULL！uuid = %s, mappingName = %s\r\n",
                            comment, fromJPath, toPath, uuid, mappingName);
                } else {
                    String fromValue = fromValueObject.toString();
                    XmlHelper.setElementValue(documentNodeToBw.getDocument(), toPath, fromValue);
                    System.out.printf("【%s】转换完成：[%s] → [%s]，值：[%s]，类型：[%s]！uuid = %s, mappingName = %s\r\n",
                            comment, fromJPath, toPath, fromValueObject, valueType, uuid, mappingName);
                }
            }
        }
        System.out.printf("MappingHelper.mappingToXML end! uuid = %s\r\n", uuid);
        return documentNodeToBw.getDocument().asXML();
    }
}
