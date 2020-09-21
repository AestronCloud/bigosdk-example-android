package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_1v1_live2.*

class BasicBeauty1v1VideoActivity: One2OneLiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        super.initView()

        btn_stickers.visibility = View.GONE
        btn_beauty.setOnClickListener {
            startActivity(Intent(this,BasicBeautifySettingActivity::class.java))
        }

        btn_prew_stickers.visibility = View.GONE
        btn_prew_beauty.setOnClickListener {
            startActivity(Intent(this,BasicBeautifySettingActivity::class.java))
        }

    }
}