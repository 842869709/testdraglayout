# DragLayout
自定义view DragLayout 实现侧滑缩放效果
类似SlidingMenu效果

示例图片，图片如果不展示请出国即可

![](https://github.com/842869709/testdraglayout/blob/master/test.gif)

![](https://img-blog.csdnimg.cn/20200806144032425.gif)

## 1.用法
使用前，对于Android Studio的用户，可以选择添加:

方法一：Gradle： 在dependencies中添加引用：
```gradle
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
	dependencies {
	        implementation 'com.github.842869709:testdraglayout:Tag'
	}
```
方法二：Maven仓库
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
```
```
<dependency>
	    <groupId>com.github.842869709</groupId>
	    <artifactId>testdraglayout</artifactId>
	    <version>Tag</version>
</dependency>
```

## 2.功能参数与含义
配置参数|参数含义|参数类型|默认值
-|-|-|-
open|	展开布局|	无|	无
close|	关闭布局|	无|	无
getCurrentState|	获取当前布局状态|	DragLayout.DragState|	CLOSE
setmOnDragLayoutStateChangedListening|	设置展开关闭监听，以及返回滑动百分比|	无| 	无

## 3.代码参考
布局文件
将菜单布局与主布局嵌套在DragLayout内
右边的布局即主布局放在上
左边的布局即菜单布局放在下
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.yxd.mydraglayout.DragLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@mipmap/bg"
        android:id="@+id/dl">
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#0d777d"
            android:orientation="vertical">

            <ImageView
                android:id="@+id/iv"
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:src="@mipmap/bg"
                android:scaleType="centerCrop"
                android:layout_margin="50dp"/>

            <Button
                android:id="@+id/bt_open"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="打开"/>

            <Button
                android:id="@+id/bt_close"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="关闭"/>

        </LinearLayout>

        <LinearLayout
            android:layout_width="200dp"
            android:layout_height="match_parent"
            android:background="#fff">
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textSize="25sp"
                android:textColor="@android:color/black"
                android:text="ABCDEFGHIJKLMNOPQRSTUVWXYZ"/>
        </LinearLayout>

    </com.yxd.mydraglayout.DragLayout>

</LinearLayout>
```

配置及初始化
```
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

```
## v1.0.1 修改状态监听的方法名
## v1.0.0 初始化提交
