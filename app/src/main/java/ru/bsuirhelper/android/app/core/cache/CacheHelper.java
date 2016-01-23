package ru.bsuirhelper.android.app.core.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.bsuirhelper.android.app.core.models.Lesson;
import ru.bsuirhelper.android.app.core.models.StudentGroup;
import ru.bsuirhelper.android.app.core.models.Teacher;

/**
 * Created by vladislav on 4/18/15.
 */
public class CacheHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "cache_db";
    public static final int DB_VERSION = 4;

    public static class Lessons {
        public static final String TABLE_NAME = "table_lesson";
        public static final String _ID = "lesson_id";
        public static final String AUDITORY = "auditory";
        public static final String LESSON_TYPE = "lesson_type";
        public static final String LESSON_TIME = "lesson_time";
        public static final String SUBGROUP = "subgroup";
        public static final String STUDENT_GROUP_ID = "studentgroup_id";
        public static final String SUBJECT_NAME = "subject_name";
        public static final String WEEK_NUMBERS = "week_numbers";
        public static final String WEEK_DAY = "week_day";
        public static final String TEACHER_ID = "lesson_teacher_id";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + " ;";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" +
                _ID + " integer primary key," +
                AUDITORY + " text," +
                LESSON_TYPE + " text," +
                LESSON_TIME + " text," +
                SUBGROUP + " integer," +
                STUDENT_GROUP_ID + " integer," +
                SUBJECT_NAME + " text," +
                WEEK_NUMBERS + " text," +
                WEEK_DAY + " integer," +
                TEACHER_ID + " integer" +
                ");";

        public static int insertLessons(Context context, long groupId, List<Lesson> lessons) {
            if (context != null && lessons != null && lessons.size() > 0) {
                ContentValues[] contentValues = new ContentValues[lessons.size()];
                int index = 0;
                for (Lesson lesson : lessons) {
                    lesson.getStudentGroup().setId(groupId);
                    ContentValues cv = toContentValues(lesson);
                    if (cv != null) {
                        contentValues[index] = cv;
                        index++;
                    }
                }
               /* for(int i = 0; i < contentValues.length; i++) {
                    context.getContentResolver().insert(CacheContentProvider.LESSON_URI, contentValues[i]);
                }
                return contentValues.length;*/
                //TODO REPAIR BULK INSERT
                return context.getContentResolver().bulkInsert(CacheContentProvider.LESSON_URI, contentValues);

            }
            return 0;
        }

        public static ContentValues toContentValues(Lesson lesson) {
            ContentValues cv = null;
            if (lesson != null) {
                cv = new ContentValues();
                //cv.put(_ID, 5);
                cv.put(AUDITORY, lesson.getAuditory());
                cv.put(LESSON_TYPE, lesson.getType());
                cv.put(LESSON_TIME, lesson.getLessonTime());
                cv.put(SUBGROUP, lesson.getSubgroup());
                cv.put(STUDENT_GROUP_ID, lesson.getStudentGroup().getId());
                cv.put(SUBJECT_NAME, lesson.getSubjectName());
                cv.put(WEEK_DAY, lesson.getWeekDay());
                if (lesson.getTeacher() != null) {
                    cv.put(TEACHER_ID, lesson.getTeacher().getId());
                }
                //WEEK NUMBERS -> 1|2|3|
                StringBuilder sb = new StringBuilder();
                for (Integer integer : lesson.getWeekNumbers()) {
                    sb.append(integer).append("|");
                }
                cv.put(WEEK_NUMBERS, sb.toString());
            }
            return cv;
        }

        public static Lesson fromCursor(Cursor cursor) {
            if (cursor != null) {
                String auditory = cursor.getString(cursor.getColumnIndex(AUDITORY));
                String lessonType = cursor.getString(cursor.getColumnIndex(LESSON_TYPE));
                int subgroup = cursor.getInt(cursor.getColumnIndex(SUBGROUP));
                long studentGroup = cursor.getLong(cursor.getColumnIndex(STUDENT_GROUP_ID));
                String subjectName = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME));
                //Week numbers to List
                String strWeekNumbers = cursor.getString(cursor.getColumnIndex(WEEK_NUMBERS));
                List<Integer> weekNumbers = new ArrayList<>();
                String[] aWeekNumbers = strWeekNumbers.split("\\|");
                for (String number : aWeekNumbers) {
                    weekNumbers.add(Integer.parseInt(number));
                }

                int weekDay = cursor.getInt(cursor.getColumnIndex(WEEK_DAY));
                int employeeId = cursor.getInt(cursor.getColumnIndex(TEACHER_ID));
                String lessonTime = cursor.getString(cursor.getColumnIndex(LESSON_TIME));
                int id = cursor.getInt(cursor.getColumnIndex(_ID));

                Teacher teacher = Teachers.fromCursor(cursor);
                return new Lesson(id, auditory, lessonTime, lessonType, subgroup, new StudentGroup(studentGroup, null, null), subjectName,
                        weekNumbers, teacher, weekDay);
            }
            return null;
        }
    }

    public static class StudentGroups {
        public static final String TABLE_NAME = "table_studentgroup";
        public static final String _ID = "studentgroup_id";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + " ;";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" +
                _ID + " integer primary key, " +
                NAME + " text, " +
                NUMBER + " text" +
                ");";

        public static ContentValues toContentValues(StudentGroup studentGroup) {
            ContentValues cv = null;
            if (studentGroup != null) {
                cv = new ContentValues();
                cv.put(NAME, studentGroup.getGroupName());
                cv.put(NUMBER, studentGroup.getGroupNumber());
                if (studentGroup.getId() >= 0) {
                    cv.put(_ID, studentGroup.getId());
                }
            }
            return cv;
        }

        public static StudentGroup fromCursor(Cursor cursor) {
            if (cursor != null) {
                long id = cursor.getLong(cursor.getColumnIndex(_ID));
                String groupName = cursor.getString(cursor.getColumnIndex(NAME));
                String groupNumber = cursor.getString(cursor.getColumnIndex(NUMBER));
                return new StudentGroup(id, groupName, groupNumber);
            }
            return null;
        }

        public static StudentGroup getById(Context context, long id) {
            StudentGroup studentGroup = null;
            if (context != null) {
                Cursor cursor = context.getContentResolver().query(CacheContentProvider.STUDENTGROUP_URI, null,
                        CacheHelper.StudentGroups._ID + " = " + id, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    studentGroup = fromCursor(cursor);
                }
            }
            return studentGroup;
        }

       /* public static boolean isExists(Context context, String groupId) {
            if (context != null) {
                Cursor cursor = context.getContentResolver().query(CacheContentProvider.STUDENTGROUP_URI, null, NUMBER + " = " + groupId, null, null);
                boolean isExists = cursor != null && cursor.moveToNext();
                if(cursor != null) {
                    cursor.close();
                }
                return isExists;
            }
            return false;
        }*/
        }

        public static class Teachers {
            public static final String TABLE_NAME = "table_teacher";
            public static final String _ID = "teacher_id";
            public static final String FIRST_NAME = "first_name";
            public static final String LAST_NAME = "last_name";
            public static final String MIDDLE_NAME = "middle_name";
            public static final String ACADEMIC_DEPARTMENTS = "academic_departments";
            public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
            public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                    " (" +
                    _ID + " integer primary key, " +
                    FIRST_NAME + " text, " +
                    LAST_NAME + " text, " +
                    MIDDLE_NAME + " text, " +
                    ACADEMIC_DEPARTMENTS + " text" +
                    " );";


            public static int insertTeachers(Context context, List<Teacher> teachers) {
                if (context != null && teachers != null && teachers.size() > 0) {
                    List<ContentValues> contentValues = new ArrayList<>();
                    for (Teacher teacher : teachers) {
                        ContentValues cv = toContentValues(teacher);
                        if (cv != null) {
                            contentValues.add(cv);
                        }
                    }
                    return context.getContentResolver().bulkInsert(CacheContentProvider.TEACHER_URI, contentValues.toArray(new ContentValues[contentValues.size()]));
                }
                return 0;
            }

            public static Teacher fromCursor(Cursor cursor) {
                if (cursor != null) {
                    long id = cursor.getLong(cursor.getColumnIndex(_ID));
                    String firstName = cursor.getString(cursor.getColumnIndex(FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndex(LAST_NAME));
                    String middleName = cursor.getString(cursor.getColumnIndex(MIDDLE_NAME));
                    String academicDepartments = cursor.getString(cursor.getColumnIndex(ACADEMIC_DEPARTMENTS));
                    String[] aDepartments = academicDepartments.split("\\|");
                    List<String> departments = new ArrayList<String>();
                    for (String department : departments) {
                        departments.add(department);
                    }
                    return new Teacher(id, firstName, lastName, middleName, departments);
                }
                return null;
            }

            public static ContentValues toContentValues(Teacher teacher) {
                if (teacher != null) {
                    ContentValues cv = new ContentValues();
                    cv.put(_ID, teacher.getId());
                    cv.put(FIRST_NAME, teacher.getFirstName());
                    cv.put(LAST_NAME, teacher.getLastName());
                    cv.put(MIDDLE_NAME, teacher.getMiddleName());
                    StringBuilder sb = new StringBuilder();
                    for (String department : teacher.getAcademicDepartments()) {
                        sb.append(department).append("|");
                    }
                    cv.put(ACADEMIC_DEPARTMENTS, sb.toString());
                    return cv;
                }
                return null;
            }
        }


        public CacheHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Lessons.CREATE_TABLE);
            db.execSQL(StudentGroups.CREATE_TABLE);
            db.execSQL(Teachers.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
