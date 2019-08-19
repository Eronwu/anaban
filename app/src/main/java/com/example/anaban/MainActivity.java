package com.example.anaban;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
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
    final private int HANDLER_DELETE_BUTTON = 1;
    private Context context;
    private DisplayMetrics displayMetrics;
    private FrameLayout windowLyaout;
    private int screenWidth;
    private int screenHeight;
    private int buttonWidth, buttonHeight;
    private List<Button> buttonList, buttonList2;
    private int lastX[], lastY[];
    private String writeName[];
    private int index;
    private long lastTime, lastLongTouchTime;
    private Button lastButton;
    private TextView noteMsg;
    private AlertDialog setNameAlertDialog, deleteDialog;
    private PositionSavingClass savingClass;
    private NameSavingClass nameSavingClass;

    //    private PersonMsg[] personMsgs;
    private List<PersonMsg> personMsgList;
//    private final static int buttonID = View.generateViewId();

    private String[][] lists = {
            {"管理员", "0"},
            {"明星脸", "101"},
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

    private List<String> knownNameList;
    private String[] KNOWN_NAME_LIST = {
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
            int i;
            switch (msg.what) {
                case HANDLER_UPDATE_BUTTON_TEXT:
                    i = msg.arg1;
                    if (writeName[i].length() > 0) {
                        buttonList.get(i).setMaxWidth(buttonWidth);
                        buttonList.get(i).setTextColor(Color.RED);
                        buttonList.get(i).setText(writeName[i]);

                        buttonList2.get(i).setMaxWidth(buttonWidth);
                        buttonList2.get(i).setTextColor(Color.RED);
                        buttonList2.get(i).setText(writeName[i]);

                        knownNameList.set(i, writeName[i]);
                        // todo: save name
                    }
                    break;
                case HANDLER_DELETE_BUTTON:
                    i = msg.arg1;
                    if (buttonList.get(i) != null) {
                        windowLyaout.removeView(buttonList.get(i));
                        windowLyaout.removeView(buttonList2.get(i));
                        buttonList.remove(i);
                        buttonList2.remove(i);
                        knownNameList.remove(i);
                        deleteDialog = null;
                    }
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
                    if (buttonList.size() + 1 >= PERSON_NUM) {
                        Toast.makeText(context, "max button num :" + PERSON_NUM, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Button button = new Button(context);
                    buttonList.add(button);
                    button.setGravity(Gravity.CENTER);
                    // todo: 为什么加了背景宽度就变了?
//                    buttons[buttonNum].setBackgroundColor(Color.RED);
//                    buttons[buttonNum].setBackgroundResource(R.drawable.button_bg);
                    button.setTextColor(Color.LTGRAY);
                    button.setText(R.string.write_name);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);
//                    buttons[buttonNum].setWidth(buttonWidth);
//                    buttons[buttonNum].setHeight(buttonHeight);
                    Button button2 = new Button(context);
                    buttonList2.add(button2);
                    button2.setGravity(Gravity.CENTER);
//                    buttons2[buttonNum].setBackgroundResource(R.drawable.button_bg);
                    button2.setTextColor(Color.LTGRAY);
                    button2.setText(R.string.get_name);
                    button2.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);
//                    buttonList.add(button);
//                    button.setId(R.id.button_id1);
                    knownNameList.add(getString(R.string.write_name));
                    windowLyaout.addView(button, buttonWidth, buttonHeight);
                    windowLyaout.addView(button2, buttonWidth, buttonHeight);
                    setViewLocation(button, screenWidth / 4 - buttonWidth, screenHeight / 5 * 3, 0, 0);
                    setViewLocation(button2, screenWidth / 4 + buttonWidth / 2, screenHeight / 5 * 3, 0, 0);
                    button.setOnTouchListener(MainActivity.this);
                    button.setOnClickListener(MainActivity.this);
                    button2.setOnTouchListener(MainActivity.this);
                    button2.setOnClickListener(MainActivity.this);
//                    Toast.makeText(context, getString(R.string.info_type_name), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_auto_sort:
                    generateAllTheName(false);
                    return true;
                case R.id.navigation_about:
                    postAboutDialog();
            }
            return false;
        }
    };

    private void postAboutDialog() {
        AlertDialog aboutAlertDialog = new AlertDialog.Builder(this)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle(R.string.title_about)
                .setMessage(R.string.about_me)
                .create();
        aboutAlertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        initView();
        playMonkeyTimeAudio();
        initData();
        queryToSetData();
    }

    private void initView() {
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
    }

    private void initData() {
        buttonList = new ArrayList<Button>();
        buttonList2 = new ArrayList<Button>();
        knownNameList = new ArrayList<String>();
        lastX = new int[PERSON_NUM];
        lastY = new int[PERSON_NUM];
        writeName = new String[PERSON_NUM];

        setNameAlertDialog = null;
        deleteDialog = null;

        initKnownNameList();
    }

    private void initKnownNameList() {
        nameSavingClass = new NameSavingClass(this);
        if (nameSavingClass.queryNameDataHasValue()) {
            int length = nameSavingClass.getNameDataLength();
            for (int i = 0; i < length; i++) {
                knownNameList.add(nameSavingClass.queryNameData(i));
            }
        } else {
            for (int i = 0; i < KNOWN_NAME_LIST.length; i++) {
                knownNameList.add(i, KNOWN_NAME_LIST[i]);
            }
        }

    }

    private void queryToSetData() {
        savingClass = new PositionSavingClass(this);
        Log.d(TAG, "onCreate: query");
//        savingClass.printAllData();
        if (savingClass.queryPositionDataHasValue()) {
            Log.d(TAG, "onCreate: query has value");
            noteMsg.setVisibility(View.VISIBLE);
            generateAllTheName(true);

            for (int p = 0; p < buttonList.size(); p++) {
                if (buttonList.get(p) != null) {
                    int position[] = savingClass.queryPositionData(2 * p);
                    setViewLocation(buttonList.get(p), position[0], position[1], 0, 0);

                    int position2[] = savingClass.queryPositionData(1 + 2 * p);
                    setViewLocation(buttonList2.get(p), position2[0], position2[1], 0, 0);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.save_data)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            savingClass.deleteAllPositionData();
                            for (int p = 0; p < buttonList.size(); p++) {
                                if (buttonList.get(p) != null) {
                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) buttonList.get(p).getLayoutParams();
                                    savingClass.insertPositionData(2 * p, params.leftMargin, params.topMargin);
//                                    Log.d(TAG, "insertPositionData:" + p + " l:" + params.leftMargin + " t:" + params.topMargin);
                                    ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) buttonList2.get(p).getLayoutParams();
                                    savingClass.insertPositionData(1 + 2 * p, params2.leftMargin, params2.topMargin);
//                                    Log.d(TAG, "insertPositionData2:" + p + " l:" + params2.leftMargin + " t:" + params2.topMargin);
                                    nameSavingClass.insertNameData(p, knownNameList.get(p));
                                }
                            }
                            MainActivity.this.finish();
//                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton(R.string.no_save, null)
                    .create();
            alertDialog.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void playMonkeyTimeAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.monkey_time);
                mediaPlayer.start();
            }
        }).start();
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
        for (Button button : buttonList) {
            if (button != null)
                windowLyaout.removeView(button);
        }
        for (Button button : buttonList2) {
            if (button != null)
                windowLyaout.removeView(button);
        }
        buttonList.clear();
        buttonList2.clear();
        index = 0;
    }

    private void generateAllTheName(boolean fromSharedPreference) {
        clearButton();

        int buttonLength = 0;
        if (fromSharedPreference) {
            // 使用button长度而非名字长度因为有可能增加了button但是没有写名字
            buttonLength = savingClass.getPositionDataLength() / 2;
        } else {
            buttonLength = knownNameList.size();
        }

        for (int buttonNum = 0; buttonNum < buttonLength; buttonNum++) {
            Button button = new Button(context);
            buttonList.add(buttonNum, button);
            button.setGravity(Gravity.CENTER);
            button.setTextColor(Color.RED);
            button.setText(knownNameList.get(buttonNum));
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);

            Button button2 = new Button(context);
            buttonList2.add(buttonNum, button2);
            button2.setGravity(Gravity.CENTER);
            button2.setTextColor(Color.RED);
            button2.setText(knownNameList.get(buttonNum));
            button2.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_VIEW_FONT_SIZE - 2);

            windowLyaout.addView(button, buttonWidth, buttonHeight);
            windowLyaout.addView(button2, buttonWidth, buttonHeight);
            setViewLocation(button, screenWidth / 2, buttonHeight * (1 + buttonNum), 0, 0);
            setViewLocation(button2, screenWidth / 4 * 3, buttonHeight * (1 + buttonNum), 0, 0);
            button.setOnTouchListener(MainActivity.this);
            button.setOnClickListener(MainActivity.this);
            button2.setOnTouchListener(MainActivity.this);
            button2.setOnClickListener(MainActivity.this);
        }
//        Toast.makeText(context, getString(R.string.info_type_name), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getClass().equals(Button.class)) {
            if (buttonList.contains(v)) {
                Log.d(TAG, "onTouch: contains v list");
                index = buttonList.indexOf(v);
            } else if (buttonList2.contains(v)) {
                Log.d(TAG, "onTouch: 2 contains v list");
                index = buttonList2.indexOf(v);
            } else {
                Log.d(TAG, "onTouch: contains nothing");
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouch: is Button[" + index + "]");
                    lastLongTouchTime = System.currentTimeMillis();
                    lastX[index] = (int) event.getRawX();
                    lastY[index] = (int) event.getRawY();
//                    firstX[i] = (int) event.getRawX();
//                    firstY[i] = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX[index];
                    int dy = (int) event.getRawY() - lastY[index];
//                    Log.d(TAG, "onTouch: enter delete:" + Math.abs(dx) + " " +
//                            (dy) + " " + (System.currentTimeMillis() - lastLongTouchTime)
//                            + " " + (deleteDialog == null));
                    if ((Math.abs(dx) < 10) && (Math.abs(dy) < 10) &&
                            (System.currentTimeMillis() - lastLongTouchTime > 1 * 1000) && (deleteDialog == null)) {
//                        Log.d(TAG, "onTouch: enter delete");
                        deleteDialog = new AlertDialog.Builder(this)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Message msg = new Message();
                                        msg.what = HANDLER_DELETE_BUTTON;
                                        msg.arg1 = index;
                                        myHandler.sendMessage(msg);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .setMessage("确认删除？")
                                .create();
                        deleteDialog.show();
                        return false;
                    }
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
                    lastX[index] = (int) event.getRawX();
                    lastY[index] = (int) event.getRawY();
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

    private void postEditNameDialog() {
        if (setNameAlertDialog != null && setNameAlertDialog.isShowing()) {
            return;
        }
        final EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setNameAlertDialog = new AlertDialog.Builder(this)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
        setNameAlertDialog.show();
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
        if (v.getClass().equals(Button.class)) {
            if (buttonList.contains(v)) {
                index = buttonList.indexOf(v);
            } else if (buttonList2.contains(v)) {
                index = buttonList2.indexOf(v);
            } else
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
