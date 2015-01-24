package ru.bsuirhelper.android.core.schedule;

import android.util.Log;
import org.w3c.dom.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import ru.bsuirhelper.android.ui.ActivityDrawerMenu;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Влад on 10.10.13.
 */
public class ScheduleParser {

    public static ArrayList<Lesson> parseXmlSchedule(File xmlFile) throws Exception {
        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
        xpp.setInput(new FileReader(xmlFile));
        int eventType = xpp.getEventType();
        Lesson lesson = null;
        String startTag = null;
        String endTag = null;

        String weekDay = "Понедельник";
        String teacherFirstName = null;
        String teacherLastName = null;
        String teacherSecondName = null;

        int counter = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {

            } else if (eventType == XmlPullParser.START_TAG) {
                startTag = xpp.getName();
                if (startTag.equals("schedule")) {
                    lesson = new Lesson();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                endTag = xpp.getName();
                if (endTag.equals("schedule")) {
                    lesson.fields.put("weekDay", weekDay);
                    lessons.add(lesson);
                } else if(endTag.equals("weekDay")) {
                    for(int i = counter; i < lessons.size(); i++) {
                        lessons.get(i).fields.put("weekDay", weekDay);
                    }
                    counter = lessons.size();
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String text = xpp.getText();

                if (startTag.equals("auditory")) {
                    lesson.fields.put("auditorium", text);
                } else if (startTag.equals("lessonTime")) {
                    lesson.fields.put("timePeriod", text);
                } else if (startTag.equals("lessonType")) {
                    lesson.fields.put("subjectType", text);
                } else if (startTag.equals("numSubgroup")) {
                    lesson.fields.put("subgroup", text);
                } else if (startTag.equals("studentGroup")) {
                    lesson.fields.put("s_group", text);
                } else if (startTag.equals("subject")) {
                    lesson.fields.put("subject", text);
                } else if (startTag.equals("weekNumber")) {
                    String weekList = lesson.fields.get("weekList") == null ? text : lesson.fields.get("weekList") + " " + text;
                    lesson.fields.put("weekList", weekList);
                    //Make teacher name, example: "Метельский В.М"
                } else if (startTag.equals("firstName")) {
                    teacherFirstName = text.substring(0, 1);
                } else if (startTag.equals("lastName")) {
                    teacherLastName = text;
                } else if (startTag.equals("weekDay")) {
                    weekDay = text;
                } else if (startTag.equals("middleName")) {
                    teacherSecondName = text.substring(0, 1);
                    lesson.fields.put("teacher", teacherLastName + " " + teacherFirstName + "." + teacherSecondName);
                }
            }
            eventType = xpp.next();
        }
        for(Lesson les : lessons) {
            Log.d(ActivityDrawerMenu.LOG_TAG, les.toString());
            Log.d(ActivityDrawerMenu.LOG_TAG, "--------------------");
        }
        return lessons;
    }


}
