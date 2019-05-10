package me.leefeng.verificationview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        verificationView.finish = {
            Toast.makeText(this,"$it",Toast.LENGTH_SHORT).show()
        }
        verificationView2.listener = {s,b->
            println("$s,$b")
//            Toast.makeText(this,"$it",Toast.LENGTH_SHORT).show()
        }
        button.setOnClickListener {
            verificationView.clear()
        }
        button2.setOnClickListener {
            verificationView2.clear()
        }
    }
}
