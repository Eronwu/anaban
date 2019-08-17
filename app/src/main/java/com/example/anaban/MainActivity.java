package com.example.anaban;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    public static final String TAG = "MainActivity";
    final private int PERSON_NUM = 18;
    final private int TEXT_VIEW_FONT_SIZE = 14;
    final private int HANDLER_UPDATE_BUTTON_TEXT = 0;
    private Context context;
    private DisplayMetrics displayMetrics;
    private FrameLayout windowLyaout;
    private int screenWidth;
    private int screenHeight;
    private int buttonWidth, buttonHeight;
    private List<Button> buttonList;
    private Button[] buttons, buttons2;
    private int buttonNum = 0;
    boolean buttonb = false;
    private int lastX[], lastY[];
    private String writeName[];
    private int index;
    private long lastTime;
    private Button lastButton;
    private int firstX[], firstY[];
    private TextView noteMsg;

    //    private PersonMsg[] personMsgs;
    private List<PersonMsg> personMsgList;
//    private final static int buttonID = View.generateViewId();

    private String[][] lists = {
            {"管理员", "0"},
            {"明星脸", "101"},
            {"疯婆子", "102"},
            {"认真男", "103"},
            {"自杀女", "104"},
            {"古惑仔", "201"},
            {"数学女", "202"},
            {"华裔女", "203"},
            {"追求女", "301"},
            {"女主", "302"},
            {"工作女", "304"},
            {"居委会主任", "402"},
            {"变态医生", "403"},
            {"儿媳", "502"}
    };

    private String[] knownName = {
            "管理员",
            "医生",
            "店老板",
            "儿媳",
            "明星",
            "前夫",
            "手下",
            "疯婆子",
            "自杀女",
            "早川教授",
            "垃圾不分类人",
            "吉村",
            "古惑仔",
            "女主"
    };

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_UPDATE_BUTTON_TEXT:
                    int i = msg.arg1;
                    if (writeName[i].length() > 0) {
                        buttons[i].setMaxWidth(buttonWidth);
                        buttons[i].setTextColor(Color.RED);
                        buttons[i].setText(writeName[i]);
                        buttons2[i].setMaxWidth(buttonWidth);
                        buttons2[i].setTextColor(Color.RED);
                        buttons2[i].setText(writeName[i]);
                    }
                    break;
            }
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            noteMsg.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.navigation_add_button:
                    if (buttonNum >= PERSON_NUM) {
                        Toast.makeText(context, "max button num :" + PERSON_NUM, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    buttons[buttonNum] = new Button(context);
                    buttons[buttonNum].setGravity(Gravity.CENTER);
                    // todo: 为什么加了背景宽度就变了?
//                    buttons[buttonNum].setBackgroundColor(Color.RED);
//                    buttons[buttonNum].setBackgroundResource(R.drawable.button_bg);
                    buttons[buttonNum].setTextColor(Color.LTGRAY);
                    buttons[buttonNum].setText(R.string.write_name);
                    buttons[buttonNum].setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);
//                    buttons[buttonNum].setWidth(buttonWidth);
//                    buttons[buttonNum].setHeight(buttonHeight);
                    buttons2[buttonNum] = new Button(context);
                    buttons2[buttonNum].setGravity(Gravity.CENTER);
//                    buttons2[buttonNum].setBackgroundResource(R.drawable.button_bg);
                    buttons2[buttonNum].setTextColor(Color.LTGRAY);
                    buttons2[buttonNum].setText(R.string.get_name);
                    buttons2[buttonNum].setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);
//                    buttonList.add(button);
//                    button.setId(R.id.button_id1);
                    windowLyaout.addView(buttons[buttonNum], buttonWidth, buttonHeight);
                    windowLyaout.addView(buttons2[buttonNum], buttonWidth, buttonHeight);
                    setViewLocation(buttons[buttonNum], screenWidth / 4 - buttonWidth, screenHeight / 5 * 3, 0, 0);
                    setViewLocation(buttons2[buttonNum], screenWidth / 4 + buttonWidth / 2, screenHeight / 5 * 3, 0, 0);
                    buttons[buttonNum].setOnTouchListener(MainActivity.this);
                    buttons[buttonNum].setOnClickListener(MainActivity.this);
//                    buttons[buttonNum].setOnLongClickListener(MainActivity.this);
                    buttons2[buttonNum].setOnTouchListener(MainActivity.this);
//                    buttons2[buttonNum].setOnLongClickListener(MainActivity.this);
                    buttonNum++;
//                    Toast.makeText(context, getString(R.string.info_type_name), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_auto_sort:
                    generateAllTheName();
                    return true;
                case R.id.navigation_about:
                    postAboutDialog();
            }
            return false;
        }
    };

    private void postAboutDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle(R.string.title_about)
                .setMessage(R.string.about_me)
                .create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        getSupportActionBar().hide();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        windowLyaout = findViewById(R.id.windowFrameLayout);
        displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        Log.d(TAG, "onCreate: screen width:" + screenWidth + " height:" + screenHeight);

        buttonWidth = screenWidth / 4 / 10 * 9;
        buttonHeight = buttonWidth / 2;

        adaptListView();

//        buttonList = new List<Button>[26];
        buttons = new Button[PERSON_NUM];
        buttons2 = new Button[PERSON_NUM];
        lastX = new int[PERSON_NUM];
        lastY = new int[PERSON_NUM];
        firstX = new int[PERSON_NUM];
        firstY = new int[PERSON_NUM];
        writeName = new String[PERSON_NUM];
    }

    private void adaptListView() {
        LinearLayout columnLinearLayout = new LinearLayout(this);
        columnLinearLayout.setOrientation(LinearLayout.VERTICAL);
        windowLyaout.addView(columnLinearLayout,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // add title:
        String[] titleString = {"NAME", "ROOM", " WRITE", "GET"};
        LinearLayout titleLinearLayout = new LinearLayout(this);
        for (int i = 0; i < titleString.length; i++) {
            TextView nameTitleTextView = new TextView(this);
            nameTitleTextView.setText(titleString[i]);
            nameTitleTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            nameTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE + 2);
            nameTitleTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            nameTitleTextView.setGravity(Gravity.CENTER);
            nameTitleTextView.setBackgroundResource(R.drawable.text_view_border);
            titleLinearLayout.addView(nameTitleTextView,
                    screenWidth / 2 / 2, buttonHeight);
        }
        columnLinearLayout.addView(titleLinearLayout,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < lists.length; i++) {
            LinearLayout rawLinearLayout = new LinearLayout(this);
            TextView nameTextView = new TextView(this);
            nameTextView.setText(lists[i][0]);
            nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE);
            nameTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            nameTextView.setGravity(Gravity.CENTER);
            nameTextView.setBackgroundResource(R.drawable.text_view_border);
            TextView roomNumTextView = new TextView(this);
            roomNumTextView.setText(lists[i][1]);
            roomNumTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE);
            roomNumTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            roomNumTextView.setGravity(Gravity.CENTER);
            roomNumTextView.setBackgroundResource(R.drawable.text_view_border);
            rawLinearLayout.addView(nameTextView,
                    screenWidth / 2 / 2, buttonHeight);
            rawLinearLayout.addView(roomNumTextView,
                    screenWidth / 2 / 2, buttonHeight);
            TextView nullTextView = new TextView(this);
            TextView null2TextView = new TextView(this);
            nullTextView.setBackgroundResource(R.drawable.text_view_border);
            null2TextView.setBackgroundResource(R.drawable.text_view_border);
            nullTextView.setGravity(Gravity.CENTER);
            null2TextView.setGravity(Gravity.CENTER);
            rawLinearLayout.addView(nullTextView, screenWidth / 2 / 2, buttonHeight);
            rawLinearLayout.addView(null2TextView, screenWidth / 2 / 2, buttonHeight);
            columnLinearLayout.addView(rawLinearLayout,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        noteMsg = new TextView(this);
        noteMsg.setText(R.string.info_type_name);
//        noteMsg.setTextColor(Color.BLACK);
        noteMsg.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        noteMsg.setVisibility(View.GONE);
        noteMsg.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        noteMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE + 2);
        TextView nn = new TextView(this);
        columnLinearLayout.addView(nn,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        columnLinearLayout.addView(noteMsg,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void clearButton() {
        for (Button button : buttons) {
            if (button != null)
                windowLyaout.removeView(button);
        }
        for (Button button : buttons2) {
            if (button != null)
                windowLyaout.removeView(button);
        }
        buttonNum = 0;
        index = 0;
    }

    private void generateAllTheName() {
        clearButton();

        for (buttonNum = 0; buttonNum < knownName.length; buttonNum++) {

            buttons[buttonNum] = new Button(context);
            buttons[buttonNum].setGravity(Gravity.CENTER);
            buttons[buttonNum].setTextColor(Color.RED);
            buttons[buttonNum].setText(knownName[buttonNum]);
            buttons[buttonNum].setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);

            buttons2[buttonNum] = new Button(context);
            buttons2[buttonNum].setGravity(Gravity.CENTER);
            buttons2[buttonNum].setTextColor(Color.RED);
            buttons2[buttonNum].setText(knownName[buttonNum]);
            buttons2[buttonNum].setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);

            windowLyaout.addView(buttons[buttonNum], buttonWidth, buttonHeight);
            windowLyaout.addView(buttons2[buttonNum], buttonWidth, buttonHeight);
            setViewLocation(buttons[buttonNum], screenWidth / 2, buttonHeight * (1 + buttonNum), 0, 0);
            setViewLocation(buttons2[buttonNum], screenWidth / 4 * 3, buttonHeight * (1 + buttonNum), 0, 0);
            buttons[buttonNum].setOnTouchListener(MainActivity.this);
            buttons[buttonNum].setOnClickListener(MainActivity.this);
            buttons2[buttonNum].setOnTouchListener(MainActivity.this);
        }
//        Toast.makeText(context, getString(R.string.info_type_name), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v.getClass().equals(Button.class)) {
            int i;
            for (i = 0; i < PERSON_NUM; i++) {
                if (buttons[i] == v)
                    break;
                if (buttons2[i] == v)
                    break;
            }
            if (i == PERSON_NUM) {
                Log.d(TAG, "onTouch: person num error!");
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouch: is Button[" + i + "]");
//                    lastTime =  System.currentTimeMillis();
                    lastX[i] = (int) event.getRawX();
                    lastY[i] = (int) event.getRawY();
//                    firstX[i] = (int) event.getRawX();
//                    firstY[i] = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
//                    if ((Math.abs(firstX[i] - lastX[i]) < 20) && (Math.abs(firstY[i] - lastY[i]) < 20) &&
//                            (System.currentTimeMillis() - lastTime > 2 * 1000)) {
//                        postEditNameDialog();
//                        Log.d(TAG, "onTouch: move distance:" + (Math.abs(firstX[i] - lastX[i]))
//                                + " time :" + System.currentTimeMillis() + " lasttime:" + lastTime );
//                    }
                    int dx = (int) event.getRawX() - lastX[i];
                    int dy = (int) event.getRawY() - lastY[i];
                    int l = v.getLeft() + dx;
                    int b = v.getBottom() + dy;
                    int r = v.getRight() + dx;
                    int t = v.getTop() + dy;
                    // 下面判断移动是否超出屏幕
                    if (l < 0) {
                        l = 0;
                        r = l + v.getWidth();
                    }
                    if (t < 0) {
                        t = 0;
                        b = t + v.getHeight();
                    }
                    if (r > screenWidth) {
                        r = screenWidth;
                        l = r - v.getWidth();
                    }
                    if (b > screenHeight) {
                        b = screenHeight;
                        t = b - v.getHeight();
                    }
//                    v.layout(l, t, r, b);
                    setViewLocation(v, l, t, r, b);
                    lastX[i] = (int) event.getRawX();
                    lastY[i] = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    autoMagnetEffect(v);
                    v.requestLayout();
                    break;
                default:
                    break;
            }
//            default :
//                break;
        }

        return false;
    }

    private void autoMagnetEffect(View v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        int w = layoutParams.width;
        int h = layoutParams.height;
        int lo[] = new int[2];
        v.getLocationOnScreen(lo);
        Log.d(TAG, "autoMagnetEffect: w:" + w + " h:" + h + " l:" + lo[0] + " t:" + lo[1]);

        for (int i = 0; i < lists.length; i++) {
            if (lo[1] <= (buttonHeight * (i + 2) + buttonHeight / 2) && lo[1] > (buttonHeight * (i + 1) + buttonHeight / 2)) {
                if (lo[0] <= (screenWidth / 4 * 3 - buttonWidth / 2) && lo[0] > (screenWidth / 2 - buttonWidth / 2)) {
                    setViewLocation(v, screenWidth / 2, buttonHeight * (i + 1), 0, 0);
                } else if (lo[0] > (screenWidth / 4 * 3 - buttonWidth / 2)) {
                    setViewLocation(v, screenWidth / 4 * 3, buttonHeight * (i + 1), 0, 0);
                }
            }
        }

    }

    AlertDialog alertDialog;

    private void postEditNameDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            return;
        }
        final EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        alertDialog = new AlertDialog.Builder(this)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            writeName[index] = editText.getText().toString();
                            Message msg = new Message();
                            msg.what = HANDLER_UPDATE_BUTTON_TEXT;
                            msg.arg1 = index;
                            myHandler.sendMessage(msg);
                        }
                    }
                })
                .setView(editText)
                .create();
        alertDialog.show();
    }

    private void setViewLocation(View view, int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(right - left, bottom - top);
//        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.setMargins(left, top, 0, 0);
        view.setLayoutParams(params);
    }

    ArrayList<String> getArrayList() {
        ArrayList<String> arrayList = new ArrayList<>();
        PersonMsg personMsg = new PersonMsg();

        return arrayList;
    }

    void initPersonList() {
//        personMsgs = new PersonMsg[PERSON_NUM];
        for (int i = 0; i < PERSON_NUM; i++) {
            personMsgList.add(new PersonMsg());
//            personMsgList.get(i).name =
        }
    }

    @Override
    public void onClick(View v) {
        for (index = 0; index < PERSON_NUM; index++) {
            if (buttons[index] == v)
                break;
            if (buttons2[index] == v)
                break;
        }
        if (index == PERSON_NUM) {
            Log.d(TAG, "onTouch: person num error!");
            return;
        }
        if (lastButton == null) {
            lastButton = (Button) v;
            lastTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastTime < 300) {
            if (lastButton == v) {
                postEditNameDialog();
            } else {
                lastButton = (Button) v;
                lastTime = System.currentTimeMillis();
            }
        } else {
            lastButton = (Button) v;
            lastTime = System.currentTimeMillis();
        }
    }
}
