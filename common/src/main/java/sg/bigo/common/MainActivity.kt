package sg.bigo.common

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import sg.bigo.common.utils.PremissionProcesser
import sg.bigo.common.utils.ViewHelper

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ViewHelper.mutuallyExclusiveEnableView(etUsername, arrayListOf(btnGo))
        etUsername.requestFocus()
        btnGo.setOnClickListener {
            LiveNameInputActivity.go(this,etUsername.text.toString())
        }

        tips_version.text = getString(R.string.tips_powered,LiveApplication.avEngine().sdkVersion)
        PremissionProcesser.checkSelfPermissions(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PremissionProcesser.onRequestPermissionsResult(this,requestCode, grantResults)
    }
}
