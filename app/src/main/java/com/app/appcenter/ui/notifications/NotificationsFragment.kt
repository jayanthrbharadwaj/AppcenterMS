package com.app.appcenter.ui.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.appcenter.BuildConfig
import com.app.appcenter.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        binding.textReview.setOnClickListener{
            Intent(Intent.ACTION_VIEW).apply {
                setData(Uri.parse("https://play.google.com/store/apps/details?id=com.app.appcenter"));
                startActivity(this);
            }
        }

        binding.textShare.setOnClickListener{
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "AppCenterMS")
                var shareMessage = "\nI want to recommend you this Android App\n\n"
                shareMessage ="${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(this, "Choose one"));
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}