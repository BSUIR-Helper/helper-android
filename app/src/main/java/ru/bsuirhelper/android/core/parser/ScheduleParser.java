package ru.bsuirhelper.android.core.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import ru.bsuirhelper.android.core.models.Lesson;
import ru.bsuirhelper.android.core.models.StudentGroup;

/**
 * Created by Влад on 10.10.13.
 */
public class ScheduleParser {

    public static List<Lesson> parseXmlSchedule(File xmlFile) throws Exception {
        List<Lesson> lessons = new ArrayList<>();
        ArrayList<String> weekDays = new ArrayList<>(7);
        weekDays.add("понедельник");
        weekDays.add("вторник");
        weekDays.add("среда");
        weekDays.add("четверг");
        weekDays.add("пятница");
        weekDays.add("суббота");
        weekDays.add("воскресенье");
        XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
        xpp.setInput(new FileReader(xmlFile));
        int eventType = xpp.getEventType();
        Lesson lesson = null;
        String startTag = null;
        String endTag = null;

        int weekDay = 1;

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
                    if(lesson != null) {
                        lesson.setWeekDay(weekDay);
                        lessons.add(lesson);
                    }
                } else if (endTag.equals("weekDay")) {
                    for (int i = counter; i < lessons.size(); i++) {
                        lessons.get(i).setWeekDay(weekDay);
                    }
                    counter = lessons.size();
                }
            } else if (eventType == XmlPullParser.TEXT && startTag != null) {
                String text = xpp.getText();
                if(lesson != null) {
                    if (startTag.equals("auditory")) {
                        lesson.setAuditory(text);
                    } else if (startTag.equals("lessonTime")) {
                        lesson.setLessonTime(text);
                    } else if (startTag.equals("lessonType")) {
                        lesson.setType(text);
                    } else if (startTag.equals("numSubgroup")) {
                        lesson.setSubgroup(Integer.parseInt(text));
                    } else if (startTag.equals("studentGroup")) {
                        lesson.setStudentGroup(new StudentGroup(-1, text, text));
                    } else if (startTag.equals("subject")) {
                        lesson.setSubjectName(text);
                    } else if (startTag.equals("weekNumber")) {
                        lesson.getWeekNumbers().add(Integer.parseInt(text));
                        //Make teacher name, example: "Метельский В.М"
                    } else if (startTag.equals("firstName")) {
                        lesson.getTeacher().setFirstName(text);
                    } else if (startTag.equals("lastName")) {
                        lesson.getTeacher().setLastName(text);
                    } else if (startTag.equals("weekDay")) {
                        int pos = weekDays.indexOf(text.toLowerCase()) + 1;
                        weekDay = pos;
                        lesson.setWeekDay(pos);
                    } else if (startTag.equals("middleName")) {
                        lesson.getTeacher().setMiddleName(text);
                    } else if(startTag.equals("id")) {
                        lesson.getTeacher().setId(Long.parseLong(text));
                    } else if(startTag.equals("academicDepartment")) {
                        lesson.getTeacher().getAcademicDepartments().add(text);
                    }
                }
            }
            eventType = xpp.next();
        }
        for (Lesson les : lessons) {
       //    Logger.i(les.toString());
        }
        return lessons;
    }


}
