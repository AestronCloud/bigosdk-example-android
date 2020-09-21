package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_multi_live.*

class BasicBeautyLiveActivity: MultiLiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun initView() {
        super.initView()

        live_stickers.visibility = View.GONE
        live_beautify_face.setOnClickListener {
            startActivity(Intent(this,BasicBeautifySettingActivity::class.java))
        }
    }

    override fun showAudienceView() {
        super.showAudienceView()
        live_stickers.visibility = View.GONE
    }

    override fun showBroadCastView() {
        super.showBroadCastView()
        live_stickers.visibility = View.GONE
    }
}