package com.example.pos_admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.adapter.NotificationsAdapter
import com.example.pos.data.repository.NotificationRepository
import com.example.pos.model.NotificationsViewModel
import com.example.pos.model.NotificationsViewModelFactory
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.data.entity.Notification
import com.example.pos_admin.databinding.FragmentNotificationsBinding
import java.util.*

class NotificationsFragment : Fragment() {
    private var binding: FragmentNotificationsBinding? = null
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationsAdapter
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

        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.notificationsFragment = this
        binding?.notificationsViewModel = notificationsViewModel
        recyclerView = binding?.notiRecyclerview!!
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

    fun createNewNoti() {
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