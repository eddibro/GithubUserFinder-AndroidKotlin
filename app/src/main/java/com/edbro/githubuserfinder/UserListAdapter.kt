package com.edbro.githubuserfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.edbro.githubuserfinder.MyApplication
import com.edbro.githubuserfinder.UserListAdapter.UserListViewHolder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class UserListAdapter(private val mListener: UserListAdapterCallBack) :
    RecyclerView.Adapter<UserListViewHolder>() {
    private val mUserDataList = ArrayList<UsernameData>()
    private val mVolleyRequestQueue: RequestQueue
    private var mUserName: String? = null
    private var mTotalUserFound = 0
    private var mPageCount = 0
    private var isOnUpdate = false

    companion object {
        private const val BASE_API_SEARCH_URL =
            "https://api.github.com/search/users?q=%s&per_page=%d&page=%d"
        private const val ONETIME_REQUEST_DATA_LENGHT = 100
        const val ERROR_NETWORK = 1
        const val ERROR_SERVER_SIDE = 2
        const val ERROR_DATA_NOT_FOUND = 3
        const val ERROR_FAILED_PROCESS_DATA_RESPONSE = 4
        const val ERROR_WRONG_INPUT_PARAM = 5
    }

    interface UserListAdapterCallBack {
        fun onErrorOccurs(error_caused: Int)
    }

    private fun createDummyData() {
        for (i in 0..9) {
            val dummyUserData = UsernameData()
            dummyUserData.userImageUrl = "url number$i"
            dummyUserData.usernameText = "User Name$i"
            mUserDataList.add(dummyUserData)
        }
    }

    fun initUserNameList(userName: String) {
        if (userName.isEmpty()) {
            mListener.onErrorOccurs(ERROR_WRONG_INPUT_PARAM)
        } else {
            mUserName = userName
            mPageCount = 1
            isOnUpdate = true
            mUserDataList.clear()
            notifyDataSetChanged()
            requestUserDataFromServer(mUserName, mPageCount)
        }
    }

    private fun requestUserDataFromServer(userName: String?, pageCount: Int) {
        val url =
            String.format(BASE_API_SEARCH_URL, userName, ONETIME_REQUEST_DATA_LENGHT, pageCount)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                buildUserDataFromResponseAPI(response)
                isOnUpdate = false
            }
        ) { error ->
            val errorCause = error.javaClass.name
            var errorCode = ERROR_NETWORK
            if (errorCause.contains("ClientError") || errorCause.contains("AuthFailureError")) {
                errorCode = ERROR_SERVER_SIDE
            } else if (errorCause.contains("NoConnectionError")) {
                errorCode = ERROR_NETWORK
            }
            mListener.onErrorOccurs(errorCode)
            isOnUpdate = false
        }
        mVolleyRequestQueue.add(stringRequest)
    }

    private fun buildUserDataFromResponseAPI(response: String) {
        var htmlDataJSONObject: JSONObject? = null
        try {
            htmlDataJSONObject = JSONObject(response)
            mTotalUserFound = htmlDataJSONObject.getInt("total_count")
            if (mTotalUserFound == 0) {
                mListener.onErrorOccurs(ERROR_DATA_NOT_FOUND)
            } else {
                val isObjectExist = htmlDataJSONObject.has("items")
                if (isObjectExist) {
                    val productDataArray = htmlDataJSONObject["items"] as JSONArray
                    for (i in 0 until productDataArray.length()) {
                        val detailData = productDataArray.getJSONObject(i)
                        val userData = UsernameData()
                        userData.usernameText = detailData.getString("login")
                        userData.userImageUrl = detailData.getString("avatar_url")
                        mUserDataList.add(userData)
                    }
                    notifyDataSetChanged()
                    mPageCount++
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            mListener.onErrorOccurs(ERROR_FAILED_PROCESS_DATA_RESPONSE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_userlist, parent, false)
        return UserListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        //Add new page data to user data list.
        if ((position == mUserDataList.size - 10)
                && (mUserDataList.size < mTotalUserFound) && !this.isOnUpdate
        ) {
            isOnUpdate = true
            requestUserDataFromServer(mUserName, mPageCount)
        }
        holder.bind(mUserDataList[position])
    }

    override fun getItemCount(): Int {
        return mUserDataList.size
    }

    inner class UserListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userNameText: TextView
        private val userImageView: ImageView

        fun bind(usernameDetail: UsernameData) {
            //set username to username list
            userNameText.text = usernameDetail.usernameText

            //Process Image at Background using Glide library
            Glide.with(userImageView.context)
                .load(usernameDetail.userImageUrl)
                .into(userImageView)
        }

        init {
            userNameText = view.findViewById<View>(R.id.tv_userName) as TextView
            userImageView = view.findViewById<View>(R.id.iv_userImage) as ImageView
        }
    }

    init {
        // createDummyData();

        // Init the RequestQueue.
        mVolleyRequestQueue = Volley.newRequestQueue(MyApplication.myApplicationContext)
    }
}