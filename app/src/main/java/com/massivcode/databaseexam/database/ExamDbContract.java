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
