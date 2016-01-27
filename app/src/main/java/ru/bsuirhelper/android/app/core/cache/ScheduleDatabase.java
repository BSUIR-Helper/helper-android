package ru.bsuirhelper.android.app.core.cache;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;

import ru.bsuirhelper.android.R;
import ru.bsuirhelper.android.app.core.StudentCalendar;
import ru.bsuirhelper.android.app.core.models.Lesson;
import ru.bsuirhelper.android.app.core.models.StudentGroup;

/**
 * Created by Влад on 14.09.13.
 */
public class ScheduleDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Schedule";
    private static final int VERSION = 1;
    private SQLiteDatabase db;
    private String _ID = "id";
    private boolean isDatabaseOpen = false;
    private Context mContext;

    public ScheduleDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        /*
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(sqLiteDatabase);
        */
    }

    public ArrayList<Lesson> fetchAllLessons(String groupId) {
        this.open();
        String scheduleGroup = "schedule_" + groupId;
        Cursor c = db.rawQuery("SELECT*FROM " + scheduleGroup, null);
        ArrayList<Lesson> lessons = new ArrayList<Lesson>(c.getCount());
        while (c.moveToNext()) {

            Lesson lesson = new Lesson();
            createLessonFromCursor(lesson, c);
            lessons.add(lesson);
        }
        return lessons;
    }

    public Lesson[] getLessonsOfDay(String groupID, DateTime dayOfYear, int subgroup) {
        String sWeekDay = "";
        int weekDay = dayOfYear.getDayOfWeek();
        int workWeek = StudentCalendar.getWorkWeek(dayOfYear);
        String[] weekDays = mContext.getResources().getStringArray(R.array.week_days_id);
        switch (weekDay) {
            case DateTimeConstants.MONDAY:
                sWeekDay = weekDays[0];
                break;
            case DateTimeConstants.TUESDAY:
                sWeekDay = weekDays[1];
                break;
            case DateTimeConstants.WEDNESDAY:
                sWeekDay = weekDays[2];
                break;
            case DateTimeConstants.THURSDAY:
                sWeekDay = weekDays[3];
                break;
            case DateTimeConstants.FRIDAY:
                sWeekDay = weekDays[4];
                break;
            case DateTimeConstants.SATURDAY:
                sWeekDay = weekDays[5];
                break;
            case DateTimeConstants.SUNDAY:
                sWeekDay = weekDays[6];
                break;
        }
        this.open();
        String scheduleGroup = "schedule_" + groupID;
        String query = "SELECT*FROM " + scheduleGroup + " WHERE (weekDay=?) AND ((weekList LIKE ?) OR (weekList LIKE '')) AND ((subgroup LIKE ?) OR (subgroup LIKE '0'))  ORDER BY timePeriod";
        Cursor cursor = db.rawQuery(query, new String[]{sWeekDay, "%"+workWeek+"%", "%" + subgroup + "%"});
        Lesson[] lessons = new Lesson[cursor.getCount()];
        while (cursor.moveToNext()) {
            Lesson lesson = new Lesson();
            createLessonFromCursor(lesson, cursor);
            //Для заметок
            //lesson.id = new String(dayOfYear.getDayOfYear() + lesson.fields.get("timePeriodStart") + lesson.fields.get("timePeriodEnd") + lesson.fields.get("teacher")).hashCode();
            lessons[cursor.getPosition()] = lesson;
        }
        return lessons;
    }

    public void addSchedule(ArrayList<Lesson> list, String groupId) {
        /*
        final String tablePrefix = "schedule_";
        //Create table
        String tableName = tablePrefix + groupId;
        this.open();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        String createTableQuery = "CREATE table " + tableName;
        //Add column names and types in table
        createTableQuery += " ( " + _ID + " INT, updatedTime TEXT, ";
        ScheduleLesson lesson = new ScheduleLesson();
        Map<String, String> columns = lesson.fields;
        for (String columnName : columns.keySet()) {
            createTableQuery += columnName + " TEXT,";
        }
        createTableQuery = createTableQuery.substring(0, createTableQuery.length() - 1);
        createTableQuery += ");";
        db.execSQL(createTableQuery);
        //tableValue - переменная которая будет содержать всё расписание для создания новой таблицы
        ArrayList<ContentValues> tableValues = new ArrayList<ContentValues>();
        for (int i = 0; i < list.size(); i++) {
            ContentValues contentValues = new ContentValues();
            lesson = list.get(i);
            contentValues.put("id", i);
            //Time - when loaded schedule
            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm d.M.Y");
            contentValues.put("updatedTime", DateTime.now().toString(dtf));
            for (String columnName : columns.keySet()) {
                contentValues.put(columnName, lesson.fields.get(columnName));
            }
            tableValues.add(contentValues);
        }

        //Add values in created table
        for (ContentValues cv : tableValues) {
            db.insert(tableName, null, cv);
        }
        */
    }

    public void deleteSchedule(String groupId) {
        String tableName = "schedule_" + groupId;
        this.open();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);

    }

    public Cursor getCursorWithGroups() {
        this.open();
        Cursor tables = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        return tables;
    }

    public ArrayList<StudentGroup> getGroups() {
 /*       this.open();
        Cursor tables = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //Move to position 1, because on 0 position exist android_metadata table
        ArrayList<StudentGroup> studentGroups = new ArrayList<StudentGroup>();
        if (tables.moveToPosition(1)) {
            while (!tables.isAfterLast()) {
                String tableGroupName = tables.getString(tables.getColumnIndex("name"));
                Cursor scheduleOfGroup = db.rawQuery("SELECT updatedTime, faculty FROM " + tableGroupName, null);
                scheduleOfGroup.moveToFirst();
                String updatedTime = scheduleOfGroup.getString(scheduleOfGroup.getColumnIndex("updatedTime"));
                String faculty = scheduleOfGroup.getString(scheduleOfGroup.getColumnIndex("faculty"));
                String groupId = tableGroupName.split("_")[1];
                studentGroups.add(new StudentGroup(groupId, faculty, updatedTime));
                tables.moveToNext();
            }
        }
        return studentGroups;*/
        return null;
    }

    void open() {
        if (!isDatabaseOpen) {
            db = this.getWritableDatabase();
            isDatabaseOpen = true;
        }
    }

    private void createLessonFromCursor(Lesson lesson, Cursor cursor) {
      /*  for (String key : lesson.fields.keySet()) {
            lesson.fields.put(key, cursor.getString(cursor.getColumnIndex(key)));
        }*/
    }

}
