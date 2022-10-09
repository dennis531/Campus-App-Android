package de.uos.campusapp.component.ui.lectures.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.other.generic.fragment.FragmentForAccessingApi
import de.uos.campusapp.component.ui.lectures.adapter.LectureFilesAdapter
import de.uos.campusapp.component.ui.lectures.api.LecturesAPI
import de.uos.campusapp.component.ui.lectures.model.FileInterface
import de.uos.campusapp.component.ui.lectures.model.AbstractLecture
import de.uos.campusapp.databinding.FragmentLectureFilesBinding
import de.uos.campusapp.utils.*
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.support.v4.runOnUiThread
import java.util.concurrent.atomic.AtomicBoolean

class LectureFilesFragment : FragmentForAccessingApi<List<FileInterface>>(
    R.layout.fragment_lecture_files,
    R.string.lecture_files,
    Component.LECTURES
) {

    private var lectureId: String? = null
    private var currentFile: FileInterface? = null

    private val compositeDisposable = CompositeDisposable()
    private val isDownloading = AtomicBoolean(false)

    private val binding by viewBinding(FragmentLectureFilesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lectureNameTextView.text = requireActivity().intent.getStringExtra(Const.TITLE_EXTRA)
        lectureId = requireActivity().intent.getStringExtra(AbstractLecture.Lecture_ID)

        if (lectureId == null) {
            requireActivity().finish()
            return
        }

        loadLectureFiles(CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        loadLectureFiles(CacheControl.BYPASS_CACHE)
    }

    private fun loadLectureFiles(cacheControl: CacheControl) {
        lectureId?.let {
            fetch { (apiClient as LecturesAPI).getLectureFiles(it) }
        }
    }

    override fun onDownloadSuccessful(response: List<FileInterface>) {
        if (response.isNullOrEmpty()) {
            showError(R.string.no_files)
            return
        }

        val files = response.sortedByDescending { it.date }

        binding.lectureFilesRecyclerView.setHasFixedSize(true)
        binding.lectureFilesRecyclerView.adapter = LectureFilesAdapter(files, this::onItemClick)
    }

    private fun onItemClick(file: FileInterface) {
        if (isDownloading.get()) {
            // Allow only one download at a time
            Utils.showToast(requireContext(), R.string.download_not_finished)
            return
        }

        currentFile = file

        // Select file location
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = file.mimeType
            putExtra(Intent.EXTRA_TITLE, file.name)
        }

        try {
            startActivityForResult(intent, CREATE_FILE)
        } catch (e: ActivityNotFoundException) {
            // If the mime type is incorrect, the system file picker can not be opened
            Utils.showToast(requireContext(), getString(R.string.file_open_failure_format_string, file.name))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                downloadFile(it)
            }
        }
    }

    private fun downloadFile(uri: Uri) {
        if (!NetUtils.isConnected(requireContext())) {
            Utils.showToast(requireContext(), R.string.no_internet_connection)
            return
        }

        val file = currentFile ?: return

        // No concurrent download activity
        if (!isDownloading.compareAndSet(false, true)) {
            Utils.showToast(requireContext(), R.string.download_not_finished)
            return
        }

        binding.downloadProgressIndicator.run {
            isIndeterminate = true
            visibility = View.VISIBLE
        }

        compositeDisposable += Completable
            .fromCallable {
                // Download file content
                (apiClient as LecturesAPI).downloadLectureFile(file)?.use { inputStream ->
                    requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val fileSize = file.size

                        var bytesCopied: Long = 0
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytes = inputStream.read(buffer)

                        while (bytes >= 0) {
                            outputStream.write(buffer, 0, bytes)
                            bytesCopied += bytes
                            bytes = inputStream.read(buffer)

                            // Update progress
                            if (fileSize != null && fileSize >= 0) {
                                runOnUiThread {
                                    binding.downloadProgressIndicator.setProgressCompat((bytesCopied * 100 / fileSize).toInt(), true)
                                }
                            }
                        }
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Utils.showToast(requireContext(), getString(R.string.download_success_format_string, file.name))
                isDownloading.set(false)
                openFile(uri)
                binding.downloadProgressIndicator.visibility = View.INVISIBLE
            }, {
                binding.downloadProgressIndicator.visibility = View.INVISIBLE
                Utils.log(it)
                Utils.showToast(requireContext(), R.string.download_error)
                isDownloading.set(false)
            })
    }

    private fun openFile(uri: Uri) {
        // Open file in other app. Android determines needed app.
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // No app exists to open this file format
            Utils.showToast(requireContext(), getString(R.string.no_app_for_open_file))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val CREATE_FILE = 1

        @JvmStatic
        fun newInstance() = LectureFilesFragment()
    }
}
