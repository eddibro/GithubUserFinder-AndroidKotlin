package com.edbro.githubuserfinder

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edbro.githubuserfinder.UserListAdapter.UserListAdapterCallBack

class MainActivity : AppCompatActivity(), UserListAdapterCallBack {
    private lateinit var mSearchButton: Button
    private lateinit var mUserNameText: EditText
    private lateinit var mRecyclerUserList: RecyclerView
    private lateinit var mUserListAdapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initScreenComponent()
    }

    private fun initScreenComponent() {
        mSearchButton = findViewById(R.id.bt_search)
        mUserNameText = findViewById(R.id.et_username)
        mRecyclerUserList = findViewById(R.id.rv_userlist)
        mRecyclerUserList.setLayoutManager(LinearLayoutManager(applicationContext))
        mUserListAdapter = UserListAdapter(this)
        mRecyclerUserList.setAdapter(mUserListAdapter)
        mSearchButton.setOnClickListener(View.OnClickListener {
            val searchKeyword = mUserNameText.getText().toString()
            mUserListAdapter!!.initUserNameList(searchKeyword)
        })
    }

    override fun onErrorOccurs(error_caused: Int) {
        val builder = AlertDialog.Builder(this)
        var dialogTitle: String? = null
        var dialogMessage: String? = null
        if (error_caused == UserListAdapter.ERROR_SERVER_SIDE) {
            dialogTitle = getString(R.string.str_title_server_error)
            dialogMessage = getString(R.string.str_msg_server_error)
        } else if (error_caused == UserListAdapter.ERROR_NETWORK) {
            dialogTitle = getString(R.string.str_title_network_error)
            dialogMessage = getString(R.string.str_msg_network_error)
        } else if (error_caused == UserListAdapter.ERROR_DATA_NOT_FOUND) {
            dialogTitle = getString(R.string.str_title_data_notfound_error)
            dialogMessage = getString(R.string.str_msg_data_notfound_error)
        } else if (error_caused == UserListAdapter.ERROR_WRONG_INPUT_PARAM) {
            dialogTitle = getString(R.string.str_title_data_notvalid_error)
            dialogMessage = getString(R.string.str_msg_data_notvalid_error)
        } else {
            dialogTitle = getString(R.string.str_title_data_process_error)
            dialogMessage = getString(R.string.str_msg_data_process_error)
        }
        builder.setPositiveButton("OK") { dialog, which -> }
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.show()
    }
}