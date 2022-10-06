package com.app.appcenter.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.appcenter.ui.MainActivity
import com.app.appcenter.R
import com.app.appcenter.databinding.FragmentDashboardBinding
import com.app.appcenter.ui.dashboard.adapter.FileMeta
import com.app.appcenter.ui.dashboard.adapter.FileViewerAdapter
import com.app.appcenter.ui.dashboard.interfaces.DownloadFileClickListener
import com.google.android.material.snackbar.Snackbar
import java.io.File


class DownloadsFragment : Fragment(), DownloadFileClickListener {

    private lateinit var downloadsViewModel: DownloadsViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val PROVIDER_PATH = "com.app.appcenter.fileprovider"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        downloadsViewModel =
            ViewModelProvider(this).get(DownloadsViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        downloadsViewModel.callLoadMeta().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.apply {
                    emptyList.visibility = View.VISIBLE
                    fileListing.visibility = View.GONE
                    gotoHome.setOnClickListener {
                        (activity as MainActivity).findNavController(R.id.nav_host_fragment_activity_main)
                            .navigateUp()
                    }
                }

            } else {
                binding.emptyList.visibility = View.GONE
                binding.fileListing.apply {
                    addItemDecoration(getDivider())
                    adapter = FileViewerAdapter(this@DownloadsFragment, it)
                    layoutManager =
                        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                }
            }

        }
    }

    private fun getDivider(): DividerItemDecoration {
        val itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireContext().getDrawable(R.drawable.divider_drawable)?.let{
                itemDecoration.setDrawable(it)
            }
        }
        return itemDecoration
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onShareClick(fileMeta: FileMeta, position: Int) {
        val csvUri = FileProvider.getUriForFile(requireContext(), PROVIDER_PATH, File(fileMeta.absolutePath))

        val intent = ShareCompat.IntentBuilder.from(requireActivity())
            .setStream(csvUri)
            .setType("application/vnd.android.package-archive")
            .intent
            .setAction(Intent.ACTION_SEND)
            .setDataAndType(csvUri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)


    }

    override fun onDeleteClick(fileMeta: FileMeta, position: Int) {
        downloadsViewModel.getResolveInfo().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val intent = Intent(
                    Intent.ACTION_DELETE, Uri.fromParts(
                        "package",
                        it[0].activityInfo.packageName, null
                    )
                )
                startActivity(intent)
            } else {
                Snackbar.make(binding.root, "App is not installed yet", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onInstallClick(fileMeta: FileMeta, position: Int) {
        Build.VERSION_CODES.N
        val uri = FileProvider.getUriForFile(requireContext(), PROVIDER_PATH,
            File(fileMeta.absolutePath))
        Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(this)
        }
    }
}