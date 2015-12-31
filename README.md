# SQLiteDatabase, SQLiteOpenHelper, Contract, Facade 예제

![예제](https://raw.githubusercontent.com/suwonsmartapp/SQLiteExam/master/doc/layout-2015-12-31-153704.png)

# 1. Layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="com.massivcode.databaseexam.MainActivity">

    <EditText
        android:id="@+id/input_et"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="입력하세요" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <Button
            android:id="@+id/insert_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="삽입" />

        <Button
            android:id="@+id/select_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="조회" />

        <Button
            android:id="@+id/update_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="갱신" />

        <Button
            android:id="@+id/delete_btn"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="삭제" />

    </LinearLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <TextView
            android:id="@+id/result_tv"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </ScrollView>

</LinearLayout>

```

# 2. Contract

```java
package com.massivcode.databaseexam.database;

import android.provider.BaseColumns;

/**
 * 생성할 db 테이블의 이름과 데이터 구조에 대한 명세서 같은 클래스 입니다.
 * 이너 클래스인 엔트리 클래스에 테이블의 이름과 컬럼명을 선언해놓습니다.
 */
public class ExamDbContract {

    /**
     * 아래의 엔트리 클래스가 구현하는 BaseColumns 는 모든 테이블이 기본적으로 구현해야 하는
     * 식별자인 id 값과 추후 데이터의 개수를 카운트하는데 사용하는 count 가 담겨있습니다.
     */
    public static class ExamDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "ExamDb";
        public static final String COLUMN_NAME_DATA = "data";
    }

}

```

# 3. SQLiteOpenHelper
```java
package com.massivcode.databaseexam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 안드로이드에서 DB를 여는데 도움을 주는 클래스가 아래의 SQLiteOpenHelper 클래스 입니다.
 */
public class ExamDbHelper extends SQLiteOpenHelper {

    // 테이블을 만들기 전에 db 파일을 만들어야 합니다. db 파일에 관련된 테이블들을 생성해서 저장할 수 있습니다.
    public static final String DATABASE_NAME = "ExamDb.db";
    // 데이터베이스의 버전입니다. db 를 업데이트할 때 사용됩니다.
    public static final int DATABASE_VERSION = 1;
    // 예제 테이블인 ExamDB 테이블을 생성하는 쿼리 입니다.
    private static final String SQL_CREATE_EXAM_DB_ENTRY = "CREATE TABLE IF NOT EXISTS " +
            ExamDbContract.ExamDbEntry.TABLE_NAME + " ( " +
            ExamDbContract.ExamDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA + " TEXT NOT NULL " +
            " );";

    // db 헬퍼는 싱글톤 패턴으로 구현하는 것이 좋습니다.
    // 싱글톤 패턴은 프로그램 내에서 객체의 개수가 1개로 고정되게 하는 패턴입니다.
    private static ExamDbHelper sSingleton = null;

    // 싱글톤 패턴을 구현할 때, 주로 메소드를 getInstance 로 명명합니다.
    // 여러 곳에서 동시에 db 를 열면 동기화 문제가 생길 수 있기 때문에 synchronized 키워드를 이용합니다.
    public static synchronized ExamDbHelper getInstance(Context context) {
        // 객체가 없을 경우에만 객체를 생성합니다.
        if(sSingleton == null) {
            sSingleton = new ExamDbHelper(context);
        }

        // 객체가 이미 존재할 경우, 기존 객체를 리턴합니다.
        return sSingleton;
    }

    // 싱글톤 패턴을 구현할 때, 해당 클래스의 생성자는 private 로 선언하여 외부에서의 직접 접근을 막아야 합니다.
    private ExamDbHelper(Context context) {
        // 파라메터는 다음과 같습니다.
        // Context context : db를 만들거나 열 때 사용함
        // String name : 접근할 데이터베이스의 이름입니다.
        // CursorFactory factory : Cursor 객체를 만들어야 할 때 사용하는데, 기본값은 null 입니다.
        // int version : 데이터베이스의 버전입니다. 이 값을 이용하여 업그레이드나 다운그레이드의 여부를 판단합니다.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * DbHelper 클래스가 생성된 다음에 바로 실행되는 부분입니다.
     * db가 존재하지 않을 경우 db를 생성합니다.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // db.execSQL(String query) 는 입력한 쿼리문을 실행하는 메소드 입니다.
        db.execSQL(SQL_CREATE_EXAM_DB_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}

```

# 4. Facade
```java
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

```

# 5. Activity
```java
package com.massivcode.databaseexam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.massivcode.databaseexam.database.ExamDbFacade;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mInputEditText;
    private Button mInsertButton, mSelectButton, mUpdateButton, mDeleteButton;
    private TextView mResultTextView;
    private ExamDbFacade mFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mInputEditText = (EditText) findViewById(R.id.input_et);
        mInsertButton = (Button) findViewById(R.id.insert_btn);
        mSelectButton = (Button) findViewById(R.id.select_btn);
        mUpdateButton = (Button) findViewById(R.id.update_btn);
        mDeleteButton = (Button) findViewById(R.id.delete_btn);
        mResultTextView = (TextView) findViewById(R.id.result_tv);

        mInsertButton.setOnClickListener(this);
        mSelectButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        mFacade = new ExamDbFacade(getApplicationContext());
    }

    private boolean checkString(String str) {
        boolean result = false;

        if(TextUtils.isEmpty(str)) {
            result = true;
        }

        return result;
    }

    @Override
    public void onClick(View v) {

        String str = mInputEditText.getText().toString();
        boolean isNull = checkString(str);

        if(v.getId() != R.id.select_btn) {
            if(isNull == true) {
                Toast.makeText(MainActivity.this, "입력해주세요!", Toast.LENGTH_SHORT).show();
            }
        }

        switch (v.getId()) {
            // DB에 데이터를 삽입하는 버튼입니다.
            case R.id.insert_btn:
                if(isNull == false) {
                    ArrayList<String> insertResult = mFacade.insert(str);
                    mResultTextView.setText("");
                    for(String string : insertResult) {
                        mResultTextView.append("\n" + string);
                    }
                }
                break;
            // DB에서 모든 데이터를 조회하여 출력하는 버튼입니다.
            case R.id.select_btn:
                    ArrayList<String> selectResult = mFacade.select();
                    mResultTextView.setText("");
                    for(String string : selectResult) {
                        mResultTextView.append("\n" + string);
                    }
                break;
            // DB의 모든 데이터를 입력한 값으로 변경하는 버튼입니다.
            case R.id.update_btn:
                if(isNull == false) {
                    ArrayList<String> updateResult = mFacade.update(str);
                    mResultTextView.setText("");
                    for(String string : updateResult) {
                        mResultTextView.append("\n" + string);
                    }
                }
                break;
            // 입력한 값을 가지는 데이터를 삭제하는 버튼입니다.
            case R.id.delete_btn:
                if(isNull == false) {
                    ArrayList<String> deleteResult = mFacade.delete(str);
                    mResultTextView.setText("");
                    for(String string : deleteResult) {
                        mResultTextView.append("\n" + string);
                    }
                }
                break;
        }

    }
}

```
