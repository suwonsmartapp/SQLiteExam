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
