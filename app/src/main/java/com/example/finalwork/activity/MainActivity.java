package com.example.finalwork.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.finalwork.R;
import com.example.finalwork.fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static String pendingAiPrompt = "";
    private static int sCurrentNavId = R.id.nav_home;

    @Override protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_main);BottomNavigationView nav=findViewById(R.id.bottomNavigation);nav.setOnItemSelectedListener(item->{Fragment f;if(item.getItemId()==R.id.nav_course){f=new CourseFragment();}else if(item.getItemId()==R.id.nav_todo){f=new TodoFragment();}else if(item.getItemId()==R.id.nav_ai){f=new AiChatFragment();}else if(item.getItemId()==R.id.nav_profile){f=new ProfileFragment();}else{f=new HomeFragment();}sCurrentNavId=item.getItemId();getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,f).commit();return true;});nav.setSelectedItemId(sCurrentNavId);}
}
