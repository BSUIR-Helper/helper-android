package ru.bsuirhelper.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Влад on 14.09.13.
 */
class ScheduleDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Schedule";
    private static final int VERSION = 1;
    private SQLiteDatabase db;

    public ScheduleDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

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

    public Lesson[] getLessonsOfDay(String grouID, int weekDay, int workWeek, int subgroup) {
        String sWeekDay = "";
        switch (weekDay) {
            case DateTimeConstants.MONDAY:
                sWeekDay = "пн";
                break;
            case DateTimeConstants.TUESDAY:
                sWeekDay = "вт";
                break;
            case DateTimeConstants.WEDNESDAY:
                sWeekDay = "ср";
                break;
            case DateTimeConstants.THURSDAY:
                sWeekDay = "чт";
                break;
            case DateTimeConstants.FRIDAY:
                sWeekDay = "пт";
                break;
            case DateTimeConstants.SATURDAY:
                sWeekDay = "сб";
                break;
            case DateTimeConstants.SUNDAY:
                sWeekDay = "вс";
                break;
        }
        //Open dataBase
        this.open();
        String scheduleGroup = "schedule_" + grouID;
        String query = "SELECT*FROM " + scheduleGroup + " WHERE (weekDay=?) AND ((weekList LIKE ?) OR (weekList LIKE '')) AND ((subgroup LIKE ?) OR (subgroup LIKE ''))  ORDER BY timePeriodStart";
        Cursor cursor = db.rawQuery(query, new String[]{sWeekDay, "%" + workWeek + "%", "%" + subgroup + "%"});
        Lesson[] lessons = new Lesson[cursor.getCount()];
        while (cursor.moveToNext()) {
            Lesson lesson = new Lesson();
            lesson.setDataFromCursor(cursor);
            lessons[cursor.getPosition()] = lesson;
        }
        //Close Database
        this.close();
        return lessons;
    }

    public void addSchedule(ArrayList<Lesson> list, String groupId) {
        final String tablePrefix = "schedule_";
        //Create table
        String tableName = tablePrefix + groupId;
        this.open();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        String createTableQuery = "CREATE table " + tablePrefix + groupId;
        //Add column names and types in table
        createTableQuery += " (id INT, updatedTime TEXT, ";
        Lesson lesson = new Lesson();
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
        this.close();
    }

    public void deleteSchedule(String groupId) {
        String tableName = "schedule_" + groupId;
        this.open();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        this.close();
    }

    public ArrayList<StudentGroup> getGroups() {
        this.open();
        Cursor tables = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //Move to position 1, because on 0 position exist android_metadata table
        ArrayList<StudentGroup> studentGroups = new ArrayList<StudentGroup>();
        if (tables.moveToPosition(1)) {
            while (!tables.isAfterLast()) {
                String tableGroupName = tables.getString(tables.getColumnIndex("name"));
                Cursor scheduleOfGroup = db.rawQuery("SELECT updatedTime FROM " + tableGroupName, null);
                scheduleOfGroup.moveToFirst();
                String updatedTime = scheduleOfGroup.getString(scheduleOfGroup.getColumnIndex("updatedTime"));
                String groupId = tableGroupName.split("_")[1];
                studentGroups.add(new StudentGroup(groupId, updatedTime));
                tables.moveToNext();
            }
        }
        this.close();
        return studentGroups;
    }

    void open() {
        db = this.getWritableDatabase();
    }

    public void close() {
        db.close();
    }


}
