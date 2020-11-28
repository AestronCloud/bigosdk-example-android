package sg.bigo.common.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.filter_item.view.*
import kotlinx.android.synthetic.main.fragment_item.*
import kotlinx.coroutines.*
import sg.bigo.common.LiveApplication
import sg.bigo.common.R
import sg.bigo.common.data.FilterInfo
import sg.bigo.common.data.FilterUtils
import sg.bigo.common.data.ModelUtils
import sg.bigo.common.utils.Decompress
import sg.bigo.common.utils.DownloadUtil
import java.io.File
import kotlin.coroutines.resume


private const val TAG = "TabFragment"
class FilterFragment : BeautifyFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        GlobalScope.launch(Dispatchers.IO) {
//            File("/sdcard/filters/")?.let {
//                if (!it.exists()) {
//                    it.mkdirs()
//                }
//            }
//
//            var filters = FilterUtils.prase()
//            Log.e(TAG, "onCreate: ${filters.size}")
//            for (filter in filters) {
//                if(filter.iconUrl.isNotEmpty()) {
//                    FilterFragment.downLoad(filter.iconUrl,"/sdcard/filters/" + "icon_${filter.resourceName}")
//                } else {
//                    Log.e(TAG, "onCreate: empty iconUrl")
//                }
//
//                if(filter.resourceUrl.isNotEmpty()) {
//                    downLoad(filter.resourceUrl,"/sdcard/filters/" + filter.resourceName)
//                }
//            }
//        }

    }

    private fun onDataInited(ctx: Context) {
        if(isStoped) return

        rv_list.layoutManager = LinearLayoutManager(ctx).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        rv_list.adapter = object : RecyclerView.Adapter<VH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.filter_item,parent,false)
                return VH(v)
            }

            override fun getItemCount(): Int {
                return allFilters.size
            }

            override fun onBindViewHolder(holder: VH, position: Int) {
                val root = holder.itemView

                val filter = allFilters[position]

                root.iv_filter_icon.setOnClickListener {
                    selectPos = position
                    getSeekBar().progress = filter.defaultStrength
                    LiveApplication.avEngine().setBeautifyFilter(filter.getResourcePath(),filter.defaultStrength)

                    LiveApplication.avEngine().setMediaSideFlags(true,true,0)
                    LiveApplication.avEngine().sendMediaSideInfo("click filter,SendSeiInAudio.")

                    notifyDataSetChanged()
                }


                Log.e(TAG, "onBindViewHolder: ====>>>>" + filter.getIconPath())
                val uri: Uri = Uri.fromFile(File(filter.getIconPath()))

                root.iv_filter_icon.setImageURI(uri)

                if(selectPos == position) {
                    root.iv_filter_icon.setColorFilter(R.color.blue)
                } else {
                    root.iv_filter_icon.clearColorFilter()
                }
                root.iv_filter_tips.text = filter.name
            }

            override fun getItemViewType(position: Int): Int {
                return position
            }

        }.apply {
            setHasStableIds(true)
        }
    }


    override fun enable() {
        if(selectPos >= 0) {
            allFilters[selectPos].let {
                LiveApplication.avEngine().setBeautifyFilter(it.getResourcePath(),it.defaultStrength)
            }
        }
    }

    override fun disable() {
        if(allFilters.isNotEmpty()) {
            //选择第一幅原画滤镜
            allFilters[0].let {
                LiveApplication.avEngine().setBeautifyFilter(it.getResourcePath(),it.defaultStrength)
            }
        }
    }


    companion object {
        private var selectPos = -1

        private val allFilters = mutableListOf<FilterInfo>()

        /**
         * 从服务器下载文件
         * @param url 下载文件的地址
         * @param fullPath 文件名字
         */
        suspend fun downLoad(url: String, fullPath: String) = withContext(Dispatchers.IO){
            suspendCancellableCoroutine<Int> {
                it.invokeOnCancellation {

                }
                DownloadUtil.get().download(url,fullPath,object :DownloadUtil.OnDownloadListener{
                    override fun onDownloading(progress: Int) {
                    }

                    override fun onDownloadFailed() {
                        Log.e(TAG, "onDownloadFailed: ====>>> $url $fullPath")
                        it.resume(0)
                    }

                    override fun onDownloadSuccess() {
                        Log.e(TAG, "onDownloadSuccess: ====>>> $url $fullPath")
                        it.resume(0)
                    }
                })

            }
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_item
                , container, false)
        return v
    }



    fun onSeekProgress(progress: Int) {
        if(!userVisibleHint) return

        if(selectPos != -1) {
            val filter = allFilters[selectPos]
            filter.defaultStrength = progress
            LiveApplication.avEngine().setBeautifyFilter(filter.getResourcePath(),filter.defaultStrength)
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GlobalScope.launch(Dispatchers.IO) {
            val dir = "${activity!!.getExternalFilesDir(null)}/filter/"
            val dirFile = File(dir)
            if (!dirFile.isDirectory) {
                dirFile.delete()
                dirFile.mkdirs()
            }

            if(!dirFile.exists()) {
                dirFile.mkdirs()
            }


            var filters = FilterUtils.prase()
            //从assets目录下解压zip文件
            Decompress.unzipFromAssets(activity,"filter.zip",dir)
            Decompress.unzipFromAssets(activity,"models.zip",ModelUtils.dir);
            LiveApplication.avEngine().enableVenusEngine(ModelUtils.dir)
            filters = filters.filter {
                it.iconUrl.isNotEmpty()
            }

            if(allFilters.isEmpty()) {
                allFilters.addAll(filters)
            }

            mUIHandler.post {
                onDataInited(view.context)
            }

        }

    }

    private val mUIHandler = Handler(Looper.getMainLooper())
}