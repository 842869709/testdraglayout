package com.yxd.testdraglayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yxd.mydraglayout.DragLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        final DragLayout mdl=findViewById(R.id.dl);
        iv = findViewById(R.id.iv);
        Button bt_open = findViewById(R.id.bt_open);
        Button bt_close = findViewById(R.id.bt_close);
        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.open();
            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdl.close();
            }
        });

        mdl.setOnDragLayoutStateChangedListening(new DragLayout.OnDragLayoutStateChangedListening() {
            @Override
            public void OnDragLayoutStateChanged(DragLayout.DragState state) {
                if (state== DragLayout.DragState.CLOSE){
                    Toast.makeText(MainActivity.this,"关闭",Toast.LENGTH_SHORT).show();
                }else if (state== DragLayout.DragState.OPEN){
                    Toast.makeText(MainActivity.this,"打开",Toast.LENGTH_SHORT).show();
                    setAnimation();
                }else if (state== DragLayout.DragState.DRAGING){
                    //Log.i("test","移动中");
                }
            }

            @Override
            public void OnDrag(float precent) {
                //Log.i("test","precent="+precent);
            }
        });

        //获取当前的state
        //DragLayout.DragState state=mdl.getCurrentState();
    }

    private void setAnimation(){
        TranslateAnimation translateAnimation=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.6f,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(500);
        translateAnimation.setInterpolator(new CycleInterpolator(3));
        iv.startAnimation(translateAnimation);
    }
}