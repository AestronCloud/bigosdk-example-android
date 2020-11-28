package sg.bigo.common.utils

import sg.bigo.common.BuildConfig
import sg.bigo.opensdk.rtm.TestEnv

class Env : TestEnv{
    override fun isTestMode(): Boolean {
        return BuildConfig.USE_TEST_MODE
    }

    override fun isTestEnv(): Boolean {
        return BuildConfig.IS_TEST_ENV
    }

    override fun getTestLbsIp(): String {
        return BuildConfig.IP
    }

    override fun getTestLbsPort(): Int {
        return BuildConfig.PORT
    }
}