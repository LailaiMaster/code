package me.ztiany.androidq

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import me.ztiany.androidq.scopestorage.ScopeStorageActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews()
    }

    private fun setViews() {
        mainBtnScopeStorage.setOnClickListener {
            startActivity(Intent(this, ScopeStorageActivity::class.java))
        }
    }

}
