package sg.bigo.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.transcoding_user_item.view.*
import sg.bigo.common.fragment.TranscodingCfgFragment
import sg.bigo.common.utils.ToastUtils
import sg.bigo.common.utils.WindowUtil
import sg.bigo.opensdk.api.struct.BigoTranscodingUser


class TranscodingListEditActivity : BaseActivity() {
    private lateinit var adapter: MyRVAdapter
    private var editedPos = -1;

    companion object {
        private const val TAG = "AddableListActivity"
        fun emptyTranscodingUser(): BigoTranscodingUser {
            return BigoTranscodingUser(0, 0, 0, 0, 0, 0, 0f, 0)
        }
        const val intent_key_user_list = "intent_key_user_list"

    }

    lateinit var users:ArrayList<BigoTranscodingUser>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        users = ArrayList(TranscodingCfgFragment.liveTranscoding.transcodingUsers.values)

        rv_transcoding_users_list.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        adapter = MyRVAdapter()
        rv_transcoding_users_list.adapter = adapter


        tv_add_transcoding_user.setOnClickListener {
            editedPos = -1
            TranscodingUserEditActivity.go(this, emptyTranscodingUser())
        }

        WindowUtil.hideWindowStatusBar(window)
    }


    private class VH(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this);
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.run {
                setHeaderTitle(v!!.context.getString(R.string.title_for_users_op));
                add(ContextMenu.NONE, 1, ContextMenu.NONE, "删除");
            }
        }

    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        users.removeAt(adapter.getMenuPos())
        adapter.notifyDataSetChanged()
        return super.onContextItemSelected(item)
    }

    private inner class MyRVAdapter : RecyclerView.Adapter<VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.transcoding_user_item, parent, false))
        }

        override fun getItemCount(): Int {
            return users.size
        }

        override fun onViewRecycled(holder: VH) {
            holder.itemView.setOnLongClickListener(null);
            super.onViewRecycled(holder)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            users[position].run {
                val msg = "uid: $uid x:$x y:$y w:$width h:$height"
                holder.itemView.transcoding_user_item.text = msg
            }

            holder.itemView.setOnClickListener {
                users[position].let {
                    editedPos = position
                    TranscodingUserEditActivity.go(this@TranscodingListEditActivity, it)
                }
            }

            holder.itemView.setOnLongClickListener {
                setMenuPos(holder.layoutPosition)
                return@setOnLongClickListener false
            }
        }


        private var menuPos = 0

        fun getMenuPos(): Int {
            return menuPos
        }

        fun setMenuPos(position: Int) {
            this.menuPos = position
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == TranscodingUserEditActivity.code_start_activity) {
            data?.run {
                val editedUser = getParcelableExtra(TranscodingUserEditActivity.key_transcoding_user)
                        ?: emptyTranscodingUser();
                if (editedUser.uid != 0L) {
                    if(editedPos >= 0) {
                        users[editedPos] = editedUser
                    } else {
                        users.add(editedUser)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    ToastUtils.show("uid 别为0")
                }
            } ?: let {
                ToastUtils.show("没有数据返回")
            }
        }
        Log.d(TAG, "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $data editedPos = $editedPos")
    }

    override fun onPause() {
        super.onPause()
        if(!isChangingConfigurations && isFinishing) {
            TranscodingCfgFragment.replaceTranscodingUsers(users)
        }
    }

    override fun onStop() {
        super.onStop()
        if(!isChangingConfigurations && isFinishing) {
            TranscodingCfgFragment.replaceTranscodingUsers(users)
        }
    }
}