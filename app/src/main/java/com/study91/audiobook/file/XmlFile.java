package com.study91.audiobook.file;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Xml文件
 */
public class XmlFile implements IXmlFile {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param xmlUrl Xml文件Url地址
     */
    public XmlFile(String xmlUrl) {
        m.xmlUrl = xmlUrl;
    }

    @Override
    public String getValue(String key) {
        return getXmlMap().get(key);
    }

    /**
     * 获取Xml文件输入流
     */
    private InputStream getXmlInputStream() {
        if (m.xmlInputStream == null) {
            try {
                URL url = new URL(getXmlUrl()); //实例化URL对象
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //实例化Http连接对象
                m.xmlInputStream = new DataInputStream(connection.getInputStream()); //实例化输入流
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return m.xmlInputStream;
    }

    /**
     * 获取Xml文件Url地址
     */
    private String getXmlUrl() {
        return m.xmlUrl;
    }

    /**
     * 获取Xml文件内容集合
     */
    private HashMap<String, String> getXmlMap() {
        if (m.xmlMap == null) {
            m.xmlMap = new HashMap<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); //实例化一个文档构建器工厂

            Document document;
            try {
                DocumentBuilder builder = factory.newDocumentBuilder(); //通过文档构建器工厂获取一个文档构建器
                document = builder.parse(getXmlInputStream()); //通过文档构建器构建一个文档实例
            } catch (ParserConfigurationException | SAXException | IOException e) {
                throw new RuntimeException(e);
            }

            Element root = document.getDocumentElement(); //获取XML文件根节点
            NodeList nodeList = root.getChildNodes(); //获得所有子节点

            //遍历将Xml节点键值加入集合
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    m.xmlMap.put(element.getNodeName(), element.getFirstChild().getNodeValue());
                }
            }
        }

        return m.xmlMap;
    }

    /**
     * 私有变量类
     */
    private class Field {
        /**
         * Xml文件Url地址
         */
        String xmlUrl;

        /**
         * Xml文件输入流
         */
        InputStream xmlInputStream;

        /**
         * Xml文件内容集合
         */
        HashMap<String, String> xmlMap;
    }
}
