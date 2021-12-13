package me.leefeng.verificationview;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.leefeng.libverify.VerificationView;

/**
 * Created by lilifeng on 2019/4/9
 *
 * java demo
 *
 */
public class Main2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VerificationView verificationView = findViewById(R.id.verificationView);
        verificationView.setFinish(new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                Toast.makeText(Main2Activity.this, s, Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
}
