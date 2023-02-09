package com.example.pos_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.NotificationsAdapter
import com.example.pos.data.repository.NotificationRepository
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos.model.NotificationsViewModel
import com.example.pos.model.NotificationsViewModelFactory
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Notification
import com.example.pos_admin.databinding.AdminCommonHeaderBinding
import com.example.pos_admin.databinding.FragmentNotificationsBinding
import java.util.*

/** 通知をRecyclerViewで表示する
 *
 */
class NotificationsFragment : Fragment() {
    private var binding: FragmentNotificationsBinding? = null
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter
    private lateinit var headerHelper: CommonAdminHeaderHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val dao = PosAdminRoomDatabase.getDatabase(requireContext()).notificationDao()
        val repository = NotificationRepository(dao)
        val factory = NotificationsViewModelFactory(repository)
        notificationsViewModel =
            ViewModelProvider(this, factory)[NotificationsViewModel::class.java]
        binding = fragmentBinding
        val headerBinding = AdminCommonHeaderBinding.inflate(inflater, container, false)
        headerHelper = CommonAdminHeaderHelper(headerBinding, requireContext())
        headerHelper.bindHeader()
        val headerContainer = binding?.headerContainer
        headerContainer?.addView(headerBinding.root)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.notificationsFragment = this
        binding?.notificationsViewModel = notificationsViewModel
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        recyclerView = (binding?.notiRecyclerview ?: return)
        notificationsViewModel.getAllNotifications()
            .observe(viewLifecycleOwner) { notifications ->
                adapter = NotificationsAdapter(requireContext(), notifications)
                recyclerView.adapter = adapter
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun createNewNotification() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
        val textInput = dialogView.findViewById<EditText>(R.id.text_input)
        val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        datePicker.minDate = today.timeInMillis
        builder.setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val date = "${datePicker.year}/${datePicker.month + 1}/${datePicker.dayOfMonth}"
                val text = textInput.text.toString()
                // 全てのフィールドに記入されたかチェックする。されないとエラーメッセージを表示する。されたらテーブルに保存する
                if (text.isEmpty()) {
                    val builder = android.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Error")
                    builder.setMessage("Please fill in the content.")
                    builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    val dialog: android.app.AlertDialog = builder.create()
                    dialog.show()
                } else {
                    notificationsViewModel.insertNewNoti(Notification(0, date, text))
                    textInput.text = null
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.show()

    }

}