package com.pangu.crawler.framework.mapping;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlHelper {
    private XmlHelper() {
    }

    public static class XmlNode {
        private boolean leaf;

        private String name;

        private String text;

        private String originalXpath;

        private Map<String, String> attributes = new HashMap<>();

        private String xpath;

        private List<XmlNode> children = new ArrayList<>();

        private void buildXpath() {
            if (attributes.isEmpty()) {
                xpath = originalXpath;
            } else {
                final String INDEX_END_MATCH_REG = "\\[[\\d+]]$";
                List<String> attributeExpressionParts = new ArrayList<>();
                attributes.forEach((k, v) -> attributeExpressionParts.add(String.format("@%s='%s'", k, v)));
                String attributeExpression = "[" + String.join(" and ", attributeExpressionParts) + "]";
                if (Pattern.compile(INDEX_END_MATCH_REG).matcher(originalXpath).find()) {
                    xpath = originalXpath.replaceAll(INDEX_END_MATCH_REG, attributeExpression);
                } else {
                    xpath = originalXpath + attributeExpression;
                }
            }
        }

        public boolean isLeaf() {
            return leaf;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }

        public String getOriginalXpath() {
            return originalXpath;
        }

        public String getXpath() {
            return xpath;
        }

        public int childCount() {
            return children.size();
        }

        public XmlNode getChild(int i) {
            return children.get(i);
        }

        @Override
        public String toString() {
            return "XmlNode{" +
                    "leaf=" + leaf +
                    ", name='" + name + '\'' +
                    ", text='" + text + '\'' +
                    ", originalXpath='" + originalXpath + '\'' +
                    ", attributes=" + attributes +
                    ", xpath='" + xpath + '\'' +
                    ", children=" + children +
                    '}';
        }
    }

    public static class DocumentNode {
        private Document document;

        private XmlNode rootNode;

        private SortedSet<String> allXpaths;

        private SortedSet<String> mappingXpaths;

        public Document getDocument() {
            return document;
        }

        public XmlNode getRootNode() {
            return rootNode;
        }

        public SortedSet<String> getAllXpaths() {
            return Collections.unmodifiableSortedSet(allXpaths);
        }

        public SortedSet<String> getMappingXpaths() {
            return Collections.unmodifiableSortedSet(mappingXpaths);
        }

        @Override
        public String toString() {
            return "DocumentNode{" +
                    "document=" + document +
                    ", rootNode=" + rootNode +
                    ", allXpaths=" + allXpaths +
                    ", mappingXpaths=" + mappingXpaths +
                    '}';
        }
    }

    public static DocumentNode parse(String xml) throws Exception {
        XmlNode rootXmlNode = new XmlNode();
        Map<String, String> xpathReplacements = new HashMap<>();
        TreeMap<String, XmlNode> xpathXmlNodeMap = new TreeMap<>();
        Document document = DocumentHelper.parseText(xml);
        elementProcess(document.getRootElement(), rootXmlNode, xpathReplacements);
        xmlNodeXpathProcess(rootXmlNode, xpathReplacements);
        xpathXmlNodeMapBuilding(rootXmlNode, xpathXmlNodeMap);
        SortedSet<String> allXpaths = new TreeSet<>();
        SortedSet<String> mappingXpaths = new TreeSet<>();
        for (Map.Entry<String, XmlNode> entry : xpathXmlNodeMap.entrySet()) {
            String xpath = entry.getKey();
            XmlNode xmlNode = entry.getValue();
            allXpaths.add(xpath);
            if (xmlNode.isLeaf()) {
                mappingXpaths.add(xpath);
                String text0 = xmlNode.getText();
                String text1 = document.selectSingleNode(xpath).getText().trim();
                if (!text0.equals(text1)) {
                    throw new Exception("leaf xpath error : " + xpath);
                }
            }
        }
        DocumentNode documentNode = new DocumentNode();
        documentNode.document = document;
        documentNode.rootNode = rootXmlNode;
        documentNode.allXpaths = allXpaths;
        documentNode.mappingXpaths = mappingXpaths;
        return documentNode;
    }

    private static void elementProcess(Element element, XmlNode xmlNode, Map<String, String> xpathReplacements) throws Exception {
        List<Element> childElements = element.elements();
        xmlNode.name = element.getName();
        xmlNode.text = element.getText().trim();
        xmlNode.leaf = childElements.isEmpty();
        xmlNode.originalXpath = element.getUniquePath();
        element.attributes().forEach(attribute -> xmlNode.attributes.put(attribute.getName(), attribute.getValue()));
        xmlNode.buildXpath();
        if (xmlNode.getXpath() != null) {
            xpathReplacements.put(xmlNode.getOriginalXpath(), xmlNode.getXpath());
        }
        for (Element childElement : childElements) {
            XmlNode childXmlNode = new XmlNode();
            xmlNode.children.add(childXmlNode);
            elementProcess(childElement, childXmlNode, xpathReplacements);
        }
    }

    private static void xmlNodeXpathProcess(XmlNode xmlNode, Map<String, String> xpathReplacements) throws Exception {
        if (xmlNode.getXpath() == null) {
            for (Map.Entry<String, String> entry : xpathReplacements.entrySet()) {
                String originalXpath = entry.getKey();
                String xpath = entry.getValue();
                if (xmlNode.getOriginalXpath().startsWith(originalXpath)) {
                    xmlNode.xpath = xpath + xmlNode.getOriginalXpath().substring(originalXpath.length());
                    break;
                }
            }
        }
        for (XmlNode xmlNodeChild : xmlNode.children) {
            xmlNodeXpathProcess(xmlNodeChild, xpathReplacements);
        }
    }

    private static void xpathXmlNodeMapBuilding(XmlNode xmlNode, TreeMap<String, XmlNode> xpathXmlNodeMap) throws Exception {
        xpathXmlNodeMap.put(xmlNode.getXpath(), xmlNode);
        for (XmlNode xmlNodeChild : xmlNode.children) {
            xpathXmlNodeMapBuilding(xmlNodeChild, xpathXmlNodeMap);
        }
    }

    public static void removeElement(Document document, String xpath) throws Exception {
        Node node = document.selectSingleNode(xpath);
        if (node == null) {
            return;
        }
        node.detach();
    }

    public static void setElementValue(Document document, String xpath, String value) throws Exception {
        final String BRACE_MATCH_REG = "\\[.*]$";
        final String INDEX_MATCH_REG = "^[1-9]\\d*$";
        final String ATTRIBUTE_MATCH_REG = "@(\\S+)='(.*)'$";
        String currentXPath = "";
        String[] xpathParts = xpath.split("/");
        Element parent = document.getRootElement();
        for (int i = 0; i < xpathParts.length; i++) {
            final String xpathPart = xpathParts[i].trim();
            if (xpathPart.isEmpty()) {
                continue;
            }
            Matcher matchBrace = Pattern.compile(BRACE_MATCH_REG).matcher(xpathPart);
            if (!matchBrace.find()) {
                String elementName = xpathPart;
                parent = checkAndAddElement(document, parent, currentXPath, xpathPart, elementName, new HashMap<>());
            } else {
                String brace = matchBrace.group();
                String elementName = xpathPart.replace(brace, "");
                String expression = brace.substring(1, brace.length() - 1);
                Matcher matchIndex = Pattern.compile(INDEX_MATCH_REG).matcher(expression);
                if (matchIndex.find()) {
                    int index = Integer.parseInt(matchIndex.group());
                    if (index <= 0) {
                        throw new Exception("index must more than 0 : " + index);
                    }
                    for (int j = 1; j <= index; j++) {
                        Element element = checkAndAddElement(document, parent, currentXPath, elementName + "[" + j + "]", elementName, new HashMap<>());
                        if (j == index) {
                            parent = element;
                        }
                    }
                } else {
                    Map<String, String> attributes = new HashMap<>();
                    for (String attribute : expression.contains("and") ? expression.split("and") : new String[]{expression}) {
                        Matcher matchAttribute = Pattern.compile(ATTRIBUTE_MATCH_REG).matcher(attribute.trim());
                        if (matchAttribute.find()) {
                            String attributeKey = matchAttribute.group(1);
                            String attributeValue = matchAttribute.group(2);
                            attributes.put(attributeKey, attributeValue);
                        } else {
                            throw new Exception("attribute in xpath error : " + attribute);
                        }
                    }
                    parent = checkAndAddElement(document, parent, currentXPath, xpathPart, elementName, attributes);
                }
            }
            currentXPath += "/" + xpathPart;
            if (i == xpathParts.length - 1) {
                parent.setText(value);
            }
        }
    }

    private static Element checkAndAddElement(Document document,
                                              Element parent,
                                              String parentXPath,
                                              String xpathPart,
                                              String elementName,
                                              Map<String, String> attributes) throws Exception {
        if (!parentXPath.isEmpty()) {
            if (document.selectSingleNode(parentXPath) == null) {
                throw new Exception("parent node is null : " + parentXPath);
            }
        }
        String xpath = parentXPath + "/" + xpathPart;
        Node node = document.selectSingleNode(xpath);
        if (node == null) {
            System.out.printf("this element need add : %s, xpath : %s\r\n", elementName, xpath);
            Element element = parent.addElement(elementName);
            attributes.forEach(element::addAttribute);
            node = document.selectSingleNode(xpath);
            if (node != element) {
                throw new Exception("add element failed : " + xpath);
            } else {
                parent = element;
            }
        } else {
            System.out.printf("this element has exist : %s, xpath : %s\r\n", elementName, xpath);
            if (node instanceof Element) {
                parent = (Element) node;
            } else {
                throw new Exception("error node type :" + node.getClass() + ", xpath : " + xpath);
            }
        }
        return parent;
    }
}
