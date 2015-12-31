package com.massivcode.databaseexam.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Facade 패턴을 구현한 클래스입니다.
 *
 * Db 헬퍼 클래스에서 Db와 관련된 작업을 하는 메소드를 구현해서 사용해도 되지만,
 * 한 곳에 모든 것이 몰리면 코드가 복잡해지고, 사용성이 떨어집니다.
 *
 * 퍼사드 패턴을 구현하면 Db 헬퍼는 db의 생성과 삭제, 업그레이드, 다운그레이드만을 구현하고,
 * 이곳에서는 db를 이용하는 메소드만을 구현하여 보다 사용성이 좋아집니다.
 *
 */
public class ExamDbFacade {

    private ExamDbHelper mHelper;
    private Context mContext;

    // 퍼사드의 생성자에선 컨텍스트를 받아서 헬퍼 객체를 생성합니다.
    public ExamDbFacade(Context context) {
        mHelper = ExamDbHelper.getInstance(context);
        mContext = context;
    }

    /**
     * 데이터를 삽입하는 메서드 입니다.
     *
     * 데이터를 삽입한 뒤, 결과를 확인하기 위해 db에 존재하는 모든 데이터들을 리턴합니다.
     * @param insert
     * @return
     */
    public ArrayList<String> insert(String insert) {

        // db는 읽기만 가능한 것과 읽고 쓸 수 있는 것이 있습니다.
        // 삽입은 쓰는 것이므로 getWritableDatabase() 메소드를 이용해야 합니다.
        SQLiteDatabase db = mHelper.getWritableDatabase();

        // 삽입할 데이터는 ContentValues 객체에 담깁니다.
        // 맵과 동일하게 key 와 value 로 데이터를 저장합니다.
        ContentValues values = new ContentValues();

        // 삽입할 문자열을 파라메터로 받아서 저장합니다.
        values.put("data", insert);
        /**
         * 안드로이드에서는 기본적으로 삽입, 갱신, 삭제, 조회의 기능을 하는 메소드를 구현해놓았습니다.
         * insert 메소드의 파라메터는 다음과 같습니다.
         *
         * public long insert (String table, String nullColumnHack, ContentValues values)
         *
         * table : 삽입할 테이블, nullColumnHack : null , values : 삽입할 데이터들
         */
        db.insert(ExamDbContract.ExamDbEntry.TABLE_NAME, null, values);

        return select();
    }

    /**
     * 테이블에 존재하는 모든 데이터들을 리턴합니다.
     * @return
     */
    public ArrayList<String> select() {
        ArrayList<String> result = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase();

        /**
         * public Cursor query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
         * table : 접근할 테이블명, columns : 가져올 데이터들의 컬럼명,
         * selection : where 절의 key 들, selectionsArgs : where 절의 value 들
         *
         * Cursor 객체는 해당 쿼리의 결과가 담기는 객체입니다.
         */
        Cursor cursor = db.query(ExamDbContract.ExamDbEntry.TABLE_NAME,
                new String[]{ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA},
                null, null, null, null, null);

        /**
         * 커서 객체는 최초 생성시 포지션이 -1 로 지정되어 있습니다.
         * cursor.moveToFirst() 메소드를 이용하면 포지션이 0으로 이동하는데,
         * moveToNext() 메소드를 이용하면 일단 0으로 이동한 뒤, 포지션을 1씩 이동합니다.
         *
         * 아래의 반복문은 커서를 0 부터 끝까지 탐색하여 각각의 위치에서 사용자가 입력한 문자열을
         * 얻어오고, 그것을 리스트에 하나씩 저장합니다.
         */
        while(cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndexOrThrow(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA));
            result.add(data);
        }

        return result;
    }

    /**
     * 테이블에 존재하는 data 컬럼의 모든 값들을 사용자가 입력한 값으로 업데이트 합니다.
     * @param update
     * @return
     */
    public ArrayList<String> update(String update) {
        // 업데이트는 쓰기 기능이 필요합니다.
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("data", update);

        /**
         * public int update (String table, ContentValues values, String whereClause, String[] whereArgs)
         * table : 갱신할 데이터가 존재하는 테이블, values : 변경할 값들,
         * whereClause : 조건절 (key), whereArgs : 값들
         */
        db.update(ExamDbContract.ExamDbEntry.TABLE_NAME, values, null, null);

        return select();
    }

    public ArrayList<String> delete(String delete) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        /**
         * public int delete (String table, String whereClause, String[] whereArgs)
         * table : 지울 데이터가 위치하는 테이블, whereClause : 조건절, whereArgs : 조건절
         */
        db.delete(ExamDbContract.ExamDbEntry.TABLE_NAME, ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA + " = ?", new String[]{delete});


        return select();
    }


}
