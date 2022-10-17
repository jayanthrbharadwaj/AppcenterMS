package com.app.appcenter.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.appcenter.R
import com.app.appcenter.databinding.FragmentHomeBinding
import com.app.appcenter.ui.home.listeners.DownloadClickListener
import com.app.appcenter.ui.util.DOWNLOADS_STORE_NAME
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Method

@AndroidEntryPoint
class HomeFragment : Fragment(), DownloadClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val SEARCH_MENU_ID = Menu.FIRST

    lateinit var sharedPref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences(
            DOWNLOADS_STORE_NAME, Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)
        val holWebView: WebView = binding.holWebView
        holWebView.webViewClient = AppCenterWebViewClient(this, binding, homeViewModel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        holWebView.settings.apply {
            domStorageEnabled = true
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            cacheMode = WebSettings.LOAD_DEFAULT
            displayZoomControls = false
        }
        holWebView.setBackgroundColor(Color.parseColor("#f8f8f8"));

        val url = "https://install.appcenter.ms/apps"
        holWebView.loadUrl(url)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.add(0, SEARCH_MENU_ID, 0, "Search")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            SEARCH_MENU_ID -> search()
        }
        return true
    }

    private fun search() {
        val container = binding.layoutId
        val nextButton = Button(requireContext()).apply {
            text = "Next"
            setBackgroundColor(resources.getColor(R.color.appcenterMSthemeDark))
            setTextColor(Color.WHITE)
        }
        container.addView(nextButton)

        val closeButton = Button(requireContext()).apply {
            text = "Close"
            setBackgroundColor(resources.getColor(R.color.appcenterMSthemeDark))
            setTextColor(Color.WHITE)
            setOnClickListener { container.removeAllViews() }
        }

        container.addView(closeButton)

        EditText(requireContext()).apply {
            minEms = 30
            setSingleLine(true)
            setTextColor(Color.BLACK)

            setOnKeyListener { v, keyCode, event ->
                if (event.action === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    binding.holWebView.findAllAsync(this.text.toString())
                    try {
                        val m: Method =
                            WebView::class.java.getMethod("setFindIsUp", java.lang.Boolean.TYPE)
                        m.invoke(binding.holWebView, true)
                    } catch (ignored: Exception) {
                        Log.e("Exception", ignored.printStackTrace().toString())
                    }
                }
                false
            }
            container.addView(this)
        }
        nextButton.setOnClickListener { binding.holWebView.findNext(true) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onDownloadStart(downloadPath: String) {
        homeViewModel.startDownload(downloadPath, requireContext())
    }
}