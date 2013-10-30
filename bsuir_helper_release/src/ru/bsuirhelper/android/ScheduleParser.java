package ru.bsuirhelper.android;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Влад on 10.10.13.
 */
public class ScheduleParser {

    public static ArrayList<Lesson> parseXmlSchedule(File xmlFile) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();

        NodeList list = doc.getElementsByTagName("ROW");
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        for (int i = 0; i < list.getLength(); i++) {
            Lesson lesson = new Lesson();
            Element element = (Element) list.item(i);
            NamedNodeMap attrs = element.getAttributes();
            for (int j = 0; j < attrs.getLength(); j++) {
                Node attribute = attrs.item(j);
                //In xml file attribute name group, but group it is keyword of sqlite
                if (attribute.getNodeName().equals("group")) {
                    lesson.fields.put("s_group", attribute.getNodeValue());
                    continue;
                }
                lesson.fields.put(attribute.getNodeName(), attribute.getNodeValue());
            }
            lessons.add(lesson);
        }
        return lessons;
    }
}
