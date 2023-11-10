package com.example.tiktak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private ImageView menu;
    private TextView username;
    private boolean isShowMenu =false;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userEmail = currentUser.getEmail().toString();

        menu = findViewById(R.id.menu);
        username=findViewById(R.id.username);
        username.setText(userEmail);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_profile) {
                            Toast.makeText(ProfileActivity.this, "Show Profile", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.menu_permission) {
                            Toast.makeText(ProfileActivity.this, "Show Permission", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.menu_security) {
                            Toast.makeText(ProfileActivity.this, "Show Security", Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (itemId == R.id.menu_logout) {
                            LogOut();
                            Toast.makeText(ProfileActivity.this, "Show LogOut", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        resetMenuImage();
                    }
                });
                popupMenu.inflate(R.menu.profile_menu);
                popupMenu.show();
                if (!isShowMenu){
                    menu.setImageResource(R.drawable.menu_popup);
                    isShowMenu= true;
                }else {
                    menu.setImageResource(R.drawable.menu);
                    isShowMenu = false;
                }
            }
        });
    }
    public void LogOut(){
        firebaseAuth.signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSlideUp(this);
        finish();
    }


    private void resetMenuImage() {
        menu.setImageResource(R.drawable.menu);
        isShowMenu = false;
    }
    public void backFromProfile(View view){
        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSlideRight(this);
        finish();
    }
}