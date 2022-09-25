package de.uos.campusapp.component.ui.chat

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.api.tumonline.CacheControl.BYPASS_CACHE
import de.uos.campusapp.api.tumonline.CacheControl.USE_CACHE
import de.uos.campusapp.component.other.generic.adapter.NoResultsAdapter
import de.uos.campusapp.component.other.generic.fragment.FragmentForAccessingApi
import de.uos.campusapp.component.ui.chat.activity.ChatActivity
import de.uos.campusapp.component.ui.chat.activity.JoinRoomScanActivity
import de.uos.campusapp.component.ui.chat.adapter.ChatRoomListAdapter
import de.uos.campusapp.component.ui.chat.api.ChatAPI
import de.uos.campusapp.component.ui.chat.model.ChatMember
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom
import de.uos.campusapp.component.ui.chat.model.ChatRoom
import de.uos.campusapp.component.ui.chat.model.ChatRoomAndLastMessage
import de.uos.campusapp.databinding.FragmentChatRoomsBinding
import de.uos.campusapp.utils.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.support.v4.runOnUiThread

class ChatRoomsFragment : FragmentForAccessingApi<List<AbstractChatRoom>>(
    R.layout.fragment_chat_rooms,
    R.string.chat_rooms,
    Component.CHAT
) {

    private var currentMode = AbstractChatRoom.MODE_JOINED
    private val manager: ChatRoomController by lazy {
        ChatRoomController(requireContext())
    }

    private var currentChatRoom: AbstractChatRoom? = null
    private var currentChatMember: ChatMember? = null

    private lateinit var chatRoomsAdapter: ChatRoomListAdapter

    private val compositeDisposable = CompositeDisposable()

    private val handlerThread = HandlerThread("UpdateDatabaseThread")

    private val binding by viewBinding(FragmentChatRoomsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        handlerThread.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chatRoomsListView.setOnItemClickListener(this::onItemClick)

        setupTabs()
    }

    private fun setupTabs() {
        with(binding) {
            if (!ConfigUtils.getConfig(ConfigConst.CHAT_ROOM_JOINABLE, true)) {
                chatRoomTabs.visibility = View.GONE
                return
            }

            chatRoomTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    // show the given tab
                    currentMode = 1 - tab.position
                    loadChatRooms(USE_CACHE)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) = Unit

                override fun onTabReselected(tab: TabLayout.Tab) {
                    chatRoomsListView.smoothScrollToPosition(0)
                }
            })

            chatRoomTabs.addTab(chatRoomTabs.newTab().setText(R.string.joined))
            chatRoomTabs.addTab(chatRoomTabs.newTab().setText(R.string.not_joined))
        }
    }

    override fun onStart() {
        super.onStart()
        loadChatRooms(USE_CACHE)
    }

    override fun onRefresh() {
        loadChatRooms(BYPASS_CACHE)
    }

    private fun loadChatRooms(cacheControl: CacheControl) {
        fetch { (apiClient as ChatAPI).getChatRooms() }
    }

    override fun onDownloadSuccessful(response: List<AbstractChatRoom>) {
        val chatRooms = response

        // We're starting more background work, so we show a loading indicator again
        showLoadingStart()

        val handler = Handler(handlerThread.looper)
        handler.post { updateDatabase(chatRooms) }
    }

    private fun updateDatabase(chatRooms: List<AbstractChatRoom>) {
        populateCurrentChatMember()

        val currentChatMember = currentChatMember
        if (currentChatMember != null) {
            manager.replaceIntoRooms(chatRooms)
        }

        val chatRoomAndLastMessages = manager.getAllByStatus(currentMode)
        runOnUiThread {
            displayChatRoomsAndMessages(chatRoomAndLastMessages)
            showLoadingEnded()
        }
    }

    private fun displayChatRoomsAndMessages(results: List<ChatRoomAndLastMessage>) {
        with(binding) {
            if (results.isEmpty()) {
                chatRoomsListView.adapter = NoResultsAdapter(requireContext())
            } else {
                chatRoomsAdapter = ChatRoomListAdapter(requireContext(), results, currentMode)
                chatRoomsListView.adapter = chatRoomsAdapter
            }
        }

    }

    /**
     * Gets the saved local information for the user
     */
    private fun populateCurrentChatMember() {
        if (currentChatMember == null) {
            currentChatMember = Utils.getSetting(
                requireContext(), Const.CHAT_MEMBER, ChatMember::class.java)
        }
    }

    private fun onItemClick(adapterView: AdapterView<*>, v: View, position: Int, id: Long) {
        val item = binding.chatRoomsListView.getItemAtPosition(position) as ChatRoomAndLastMessage

        val room = AbstractChatRoom.fromChatRoomDbRow(item.chatRoomDbRow!!)

        if (room.joined) {
            moveToChatActivity(room)
        } else {
            joinChatRoom(room)
        }
    }

    /**
     * Creates a given chat room if it does not exist and joins it
     * Works asynchronously.
     */
    private fun joinChatRoom(room: AbstractChatRoom) {
        Utils.logVerbose("join chat room ${room.title}")
        if (this.currentChatMember == null) {
            Utils.showToast(requireContext(), getString(R.string.chat_not_setup))
            return
        }

        currentChatRoom = room

        compositeDisposable += Single.fromCallable { (apiClient as ChatAPI).addMemberToChatRoom(currentChatRoom!!, currentChatMember!!) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response == null) {
                    Utils.logVerbose("Error joining chat room")
                    Utils.showToastOnUIThread(requireActivity(), R.string.chat_room_join_error)
                    return@subscribe
                }

                // The POST request is successful: go to room. API should have auto joined it
                Utils.logVerbose("Success joining chat room: " + response)
                currentChatRoom = response

                manager.join(currentChatRoom)

                // When we show joined chat rooms open chat room directly
                if (currentMode == AbstractChatRoom.MODE_JOINED) {
                    moveToChatActivity(currentChatRoom!!)
                } else { // Otherwise show a nice information, that we added the room
                    val rooms = manager.getAllByStatus(currentMode)

                    runOnUiThread {
                        chatRoomsAdapter.updateRooms(rooms)
                        Utils.showToast(requireContext(), R.string.joined_chat_room)
                    }
                }

            }, {
                Utils.log(it, "Failure joining chat room - trying to GET it from the server")
                Utils.showToastOnUIThread(requireActivity(), R.string.chat_room_join_error)
            })
    }

    /**
     * Opens [ChatActivity]
     */
    private fun moveToChatActivity(room: AbstractChatRoom) {
        // We are sure that both currentChatRoom and currentChatMember exist at this point
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra(Const.CURRENT_CHAT_ROOM, Gson().toJson(room))
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_activity_chat_rooms, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val menuItemAddRoom = menu.findItem(R.id.action_add_chat_room)
        val menuItemJoinRoom = menu.findItem(R.id.action_join_chat_room)

        menuItemAddRoom?.isVisible = ConfigUtils.getConfig(ConfigConst.CHAT_ROOM_CREATEABLE, true)
        menuItemJoinRoom?.isVisible = ConfigUtils.getConfig(ConfigConst.CHAT_ROOM_MEMBER_ADDABLE, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_chat_room -> {
                showChatRoomCreationDialog()
                true
            }
            R.id.action_join_chat_room -> {
                openJoinChatRoom()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Prompt the user to type in a name for the new chat room
     */
    private fun showChatRoomCreationDialog() {
        // Set an EditText view to get user input
        val view = View.inflate(requireContext(), R.layout.dialog_input, null)
        val input = view.findViewById<EditText>(R.id.inputEditText)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_chat_room)
            .setMessage(R.string.new_chat_room_desc)
            .setView(view)
            .setPositiveButton(R.string.create) { dialogInterface, whichButton ->
                val name = input.text.toString()
                createChatRoom(name)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(R.drawable.rounded_corners_background)
            }
            .show()
    }

    private fun createChatRoom(name: String) {
        Utils.logVerbose("create chat room ${name}")
        if (this.currentChatMember == null) {
            Utils.showToast(requireContext(), getString(R.string.chat_not_setup))
            return
        }

        currentChatRoom = ChatRoom("0", name)

        compositeDisposable += Single.fromCallable { (apiClient as ChatAPI).createChatRoom(currentChatRoom!!) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response == null) {
                    Utils.logVerbose("Error creating chat room")
                    Utils.showToastOnUIThread(requireActivity(), R.string.chat_room_create_error)
                    return@subscribe
                }

                // The POST request is successful: go to room. API should have auto joined it
                Utils.logVerbose("Success creating chat room: " + response)
                currentChatRoom = response

                manager.join(currentChatRoom)

                // When we show joined chat rooms open chat room directly
                if (currentMode == AbstractChatRoom.MODE_JOINED) {
                    moveToChatActivity(currentChatRoom!!)
                } else { // Otherwise show a nice information, that we added the room
                    val rooms = manager.getAllByStatus(currentMode)

                    runOnUiThread {
                        chatRoomsAdapter.updateRooms(rooms)
                        Utils.showToast(requireContext(), R.string.joined_chat_room)
                    }
                }

            }, {
                Utils.log(it, "Failure creating chat room - trying to GET it from the server")
                Utils.showToastOnUIThread(requireActivity(), R.string.chat_room_create_error)
            })
    }

    private fun openJoinChatRoom() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionCheck = ActivityCompat
                .checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                startJoinRoom()
            }
        } else {
            startJoinRoom()
        }
    }

    private fun startJoinRoom() {
        val intent = Intent(requireContext(), JoinRoomScanActivity::class.java)
        startActivityForResult(intent, JOIN_ROOM_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val grantResult = grantResults.firstOrNull()
        if (requestCode == CAMERA_REQUEST_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
            startJoinRoom()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == JOIN_ROOM_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val roomJson = data.getStringExtra("room") ?: return

            joinChatRoom(Gson().fromJson(roomJson, AbstractChatRoom::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        handlerThread.quitSafely()
    }

    private companion object {
        private const val CAMERA_REQUEST_CODE = 34
        private const val JOIN_ROOM_REQUEST_CODE = 22

        @JvmStatic
        fun newInstance() = ChatRoomsFragment()
    }
}
