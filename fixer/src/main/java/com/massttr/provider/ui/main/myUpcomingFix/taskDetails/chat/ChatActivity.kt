package com.massttr.provider.ui.main.myUpcomingFix.taskDetails.chat

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.common.base.BaseActivity
import com.common.stickyheader.StickyHeaderDecoration
import com.google.gson.Gson
import com.kbeanie.multipicker.api.ImagePicker
import com.kbeanie.multipicker.api.Picker
import com.kbeanie.multipicker.api.VideoPicker
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback
import com.kbeanie.multipicker.api.callbacks.VideoPickerCallback
import com.kbeanie.multipicker.api.entity.ChosenImage
import com.kbeanie.multipicker.api.entity.ChosenVideo
import com.massttr.provider.R
import com.massttr.provider.chat.*
import com.massttr.provider.databinding.ActivityChatBinding
import com.massttr.provider.databinding.DialogPhotoVideoPickerBinding
import com.massttr.provider.ui.main.availableTasks.viewTask.showImage.ShowImageActivity
import com.massttr.user.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.browse
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File



import org.jetbrains.anko.startActivity

@ObsoleteCoroutinesApi
@DelicateCoroutinesApi
@RuntimePermissions
class ChatActivity : BaseActivity<ActivityChatBinding>(R.layout.activity_chat),
    View.OnClickListener {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var smoothScroller: LinearSmoothScroller

    private var inbox: Inbox? = null
    private var notificationChatMessage: ChatMessage? = null
    private var isChatCreate: Boolean = false

    private var offset = 0

    val typingHandler = Handler(Looper.getMainLooper())
    var typedString = ""
    var isTyping = false
    val gson = Gson()

    private val viewModel: ChatActivityViewModel by viewModels()

    private var isImageUpload = false

    private var imagePicker: ImagePicker? = null
    private var videoPicker: VideoPicker? = null

    companion object {
        const val INBOX = "inbox"
        const val CHAT_MESSAGE = "chatMessage"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setUpUI()
        setUpObserver()
        initBusEvent()
        initListener()
    }

    private fun init() {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancelAll()

        if (intent.hasExtra(INBOX))
            inbox = intent.getParcelableExtra(INBOX)
        if (intent.hasExtra(CHAT_MESSAGE))
            notificationChatMessage = intent.getParcelableExtra(CHAT_MESSAGE)

        CURRENT_OTHER_USER_ID = inbox?.chat_user_id ?: -1

        chatAdapter = ChatAdapter(this)
        chatAdapter.setItemClickListener { view, i, chatMessage ->
            when (view.id) {
                R.id.ivMyImage,
                R.id.ivOtherImage -> {
                    startActivity<ShowImageActivity>(ShowImageActivity.SHOW_IMAGE to chatMessage.message)
                }
                R.id.ivMyVideo,
                R.id.ivOtherVideo -> browse(chatMessage.message)
            }
        }

        smoothScroller = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                showMoveDownButton((binding.rvChat.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() != chatAdapter.itemCount - 1)
            }
        })
        binding.rvChat.addItemDecoration(StickyHeaderDecoration(chatAdapter))

        imagePicker = ImagePicker(this)
        imagePicker?.setImagePickerCallback(object : ImagePickerCallback {
            override fun onError(p0: String?) {

            }

            override fun onImagesChosen(p0: MutableList<ChosenImage>?) {
                if (!p0.isNullOrEmpty()) {
                    val file = File(p0[0].originalPath)
                    isImageUpload = true
                    val builder = file.multipartImageBody("media")
                    builder.addFormDataPart("type", "1")
                    viewModel.uploadChatMedia(builder.build())
                } else {
                    Timber.e("unable to fetch file")
                }
            }
        })

        videoPicker = VideoPicker(this)
        videoPicker?.setVideoPickerCallback(object : VideoPickerCallback {
            override fun onError(p0: String?) {

            }

            override fun onVideosChosen(p0: MutableList<ChosenVideo>?) {
                if (!p0.isNullOrEmpty()) {
                    val file = File(p0[0].originalPath)
                    isImageUpload = false
                    val builder = file.multipartImageBody("media")
                    builder.addFormDataPart("type", "2")
                    viewModel.uploadChatMedia(builder.build())
                } else {
                    Timber.e("unable to fetch file")
                }
            }
        })
    }

    private fun setUpUI() {
        binding.rvChat.adapter = chatAdapter

        // updateLoaderUI(true)
        setupInbox()

        notificationChatMessage?.let {
            Timber.d("hiren: ---->sendCreateChat")
            if (FixerSocketService.getSocketInstance()?.connected() == true) {
                isChatCreate = true
                FixerSocketService.getInstance()?.sendCreateChat(it.sender_id, it.job_id)
            }
        }
    }


    private fun setupInbox() {
        inbox?.let { inbox ->
            if (FixerSocketService.getSocketInstance()?.connected() == true) {
                FixerSocketService.getInstance()?.run {
                    sendGetMessage(inbox.chat_id, offset)
                }
            }
            binding.ivProfilePic.load("https://massttr.com/storage/${inbox.chat_user_avatar}") {
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            binding.tvUserName.text = inbox.chat_user_name
            binding.tvChatUserStatus.text = getString(R.string.online)
            binding.tvChatUserStatus.isVisible = inbox.is_online

            binding.tvTaskTitle.text = inbox.job_title
            binding.tvJobContent.isVisible = inbox.job_description.isNotEmpty()
            binding.tvJobContent.text = inbox.job_description

            CURRENT_OTHER_USER_ID = inbox.chat_user_id
            binding.ivCall.isVisible =
                inbox.job_status_id == 2 || inbox.job_status_id == 3 || inbox.job_status_id == 4
        }
    }

    private fun initListener() {
        binding.run {
            ivAttachOpen.setOnClickListener(this@ChatActivity)
            ivBack.setOnClickListener(this@ChatActivity)
            ivSend.setOnClickListener(this@ChatActivity)
            ivCall.setOnClickListener(this@ChatActivity)

            etMessage.doAfterTextChanged {
                typingHandler.removeCallbacks(typingRunnable)
                typingHandler.postDelayed(typingRunnable, 0)
            }

            srlChat.setOnRefreshListener {
                if (FixerSocketService.getSocketInstance()?.connected() == true) {
                    offset++
                    inbox?.let {
                        FixerSocketService.getInstance()?.sendGetMessage(it.chat_id, offset)
                    }
                } else {
                    handler.postDelayed({
                        srlChat.isRefreshing = false
                    }, 100)
                }
            }

            ivMoveDown.setOnClickListener(this@ChatActivity)
        }
    }

    private fun initBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            Timber.d("ChatActivity: event")

            if (it.containsKey(BUS_EVENT_CONNECTED) && !isChatCreate) {
                notificationChatMessage?.let { chatMessage->
                    isChatCreate = true
                    FixerSocketService.getInstance()?.sendCreateChat(chatMessage.sender_id, chatMessage.job_id)
                }
            }

            if (it.containsKey(BUS_EVENT_CREATE_CHAT) && isChatCreate) {
                inbox = it.getParcelable(BUS_EVENT_CREATE_CHAT) as Inbox?
                inbox?.let {
                    isChatCreate = false
                    setupInbox()
                }
            }

            if (it.containsKey(BUS_EVENT_MESSAGES_LIST)) {
                //  handler.postDelayed({ updateLoaderUI(false) }, 50)
                runOnUiThread {
                    Timber.d("ChatActivity: BUS_EVENT_MESSAGES_LIST")
                    val chatMessages: ArrayList<ChatMessage>? =
                        it.getParcelableArrayList(BUS_EVENT_MESSAGES_LIST)
                    chatMessages?.let { messages ->
                        messages.reverse()
                        if (binding.srlChat.isRefreshing) {
                            chatAdapter.displayList.addAll(0, messages)
                            chatAdapter.notifyDataSetChanged()

                            binding.srlChat.isRefreshing = false
                        } else {
                            chatAdapter.addAll(messages)
                            scrollToBottom(false)
                        }

                        readAllMessages()
                    }
                }

            }
            if (it.containsKey(BUS_EVENT_ONLINE)) {
                val online = it.getParcelable<Online>(BUS_EVENT_ONLINE)
                if (online?.user_id == inbox?.chat_user_id) {
                    binding.tvChatUserStatus.isVisible = online?.status == 1
                    inbox?.is_online = online?.status == 1
                }
            }

            if (it.containsKey(BUS_EVENT_TYPING)) {
                val typing = it.getParcelable<Typing>(BUS_EVENT_TYPING)
                if (inbox?.chat_id == typing?.chat_id) {
                    inbox?.is_online = true
                    binding.tvChatUserStatus.isVisible = true
                    if (typing?.typing == 1) {
                        binding.tvChatUserStatus.text = getString(R.string.typing)
                    } else {
                        binding.tvChatUserStatus.text = getString(R.string.online)
                        binding.tvChatUserStatus.isVisible = true
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_MESSAGE_RECEIVED)) {
                val newChatMessage: ChatMessage? = it.getParcelable(BUS_EVENT_MESSAGE_RECEIVED)

                newChatMessage?.let {
                    if ((binding.rvChat.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == chatAdapter.itemCount - 1) {
                        if (newChatMessage.sender_id != pref.userInfo?.id) {
                            newChatMessage.read_status = ReadStatus.READ.status
                            inbox?.chat_id?.let { chatId ->
                                FixerSocketService.getInstance()
                                    ?.sendReadMessage(chatId, newChatMessage.m_id)
                                val list = pref.unreadChatMessages
                                val index =
                                    list.indexOfLast { chatMessage -> newChatMessage.chat_id == chatMessage.chat_id && chatMessage.m_id == newChatMessage.m_id }
                                if (index != -1) {
                                    list.removeAt(index)
                                    pref.unreadChatMessages = list
                                }
                            }
                        }

                        chatAdapter.addItem(newChatMessage)
                        scrollToBottom(newChatMessage.sender_id == pref.userInfo?.id)

                        val bundle = Bundle()
                        bundle.putParcelable(
                            BUS_EVENT_READ_ALL_MESSAGES_LOCALLY,
                            InboxItem(newChatMessage.chat_id)
                        )
                        EventBus.publish(bundle)
                    } else {
                        chatAdapter.addItem(newChatMessage)
                        scrollToBottom(newChatMessage.sender_id == pref.userInfo?.id)
                        return@let
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_DELIVERED_MESSAGE)) {
                val message: Message? = it.getParcelable(BUS_EVENT_DELIVERED_MESSAGE)
                if (message?.chat_id == inbox?.chat_id) {
                    val index =
                        chatAdapter.displayList.indexOfLast { chatMessage -> chatMessage.m_id == message?.m_id }
                    if (index != -1) {
                        chatAdapter.displayList[index].read_status = ReadStatus.DELIVERED.status
                        chatAdapter.notifyItemChanged(index, ChatAdapter.PAYLOAD_STATUS)
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_DELIVERED_ALL_MESSAGES)) {
                val allMessage: InboxItem? = it.getParcelable(BUS_EVENT_DELIVERED_ALL_MESSAGES)
                if (allMessage?.chat_id == inbox?.chat_id) {
                    chatAdapter.displayList.forEachIndexed { index, chatMessage ->
                        if (chatMessage.sender_id == pref.userInfo?.id && chatMessage.read_status == ReadStatus.SENT.status) {
                            chatMessage.read_status = ReadStatus.DELIVERED.status
                            chatAdapter.notifyItemChanged(index, ChatAdapter.PAYLOAD_STATUS)
                        }
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_READ_MESSAGE)) {
                val message: Message? = it.getParcelable(BUS_EVENT_READ_MESSAGE)
                if (message?.chat_id == inbox?.chat_id) {
                    val index =
                        chatAdapter.displayList.indexOfLast { chatMessage -> chatMessage.m_id == message?.m_id }
                    if (index != -1) {
                        chatAdapter.displayList[index].read_status = ReadStatus.READ.status
                        chatAdapter.notifyItemChanged(index, ChatAdapter.PAYLOAD_STATUS)
                    }
                }
            }

            if (it.containsKey(BUS_EVENT_READ_ALL_MESSAGES)) {
                val allMessage: InboxItem? = it.getParcelable(BUS_EVENT_READ_ALL_MESSAGES)
                if (allMessage?.chat_id == inbox?.chat_id) {
                    chatAdapter.displayList.forEachIndexed { index, chatMessage ->
                        if (chatMessage.sender_id == pref.userInfo?.id && chatMessage.read_status < ReadStatus.READ.status) {
                            chatMessage.read_status = ReadStatus.READ.status
                            chatAdapter.notifyItemChanged(index, ChatAdapter.PAYLOAD_STATUS)
                        }
                    }
                }
            }
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(this@ChatActivity) { updateLoaderUI(it) }
            apiErrors.observe(this@ChatActivity) { handleError(it) }
            uploadChatMedia.observe(this@ChatActivity) {
                inbox?.let { inbox ->
                    FixerSocketService.getInstance()?.sendChatMessage(
                        inbox.chat_user_id,
                        it.media_path,
                        if (isImageUpload) MessageType.PHOTO.type else MessageType.VIDEO.type,
                        inbox.chat_id,
                        pref.userInfo?.profile_picture.orEmpty(),
                        pref.userInfo?.full_name.orEmpty(),
                        inbox.job_id
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CURRENT_OTHER_USER_ID = inbox?.chat_user_id ?: -1
    }

    override fun onPause() {
        super.onPause()
        CURRENT_OTHER_USER_ID = -1
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> onBackPressed()
            R.id.ivAttachOpen -> onAllowStoragePermissionWithPermissionCheck()
            R.id.ivCall -> {
                this.call(inbox?.chat_user_mobile_no.toString())
            }
            R.id.ivSend -> {
                if (binding.etMessage.text.toString()
                        .isNotEmpty() && binding.etMessage.text.toString().isNotBlank()
                ) {
                    inbox?.let { inbox ->
                        FixerSocketService.getInstance()?.sendChatMessage(
                            inbox.chat_user_id,
                            binding.etMessage.text.toString(),
                            MessageType.TEXT.type,
                            inbox.chat_id,
                            pref.userInfo?.profile_picture.orEmpty(),
                            pref.userInfo?.full_name.orEmpty(),
                            inbox.job_id
                        )
                        binding.etMessage.text.clear()
                    }
                }
            }
            R.id.ivMoveDown -> scrollToBottom()
        }
    }

    private fun attachAlert() {
        val dialog = Dialog(this, R.style.PreferenceThemeOverlay)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.80f)

        val dialogBinding: DialogPhotoVideoPickerBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.dialog_photo_video_picker,
                null,
                false
            )
        dialog.setContentView(dialogBinding.root)
        dialogBinding.ivAttach.isVisible = true
        //binding.ivAttach.setImageResource(R.drawable.ic_attach_white)
        dialogBinding.alert.setOnClickListener {
            binding.ivAttachOpen.visible()
            dialog.dismiss()
        }
        dialogBinding.imgPhoto.setOnClickListener {
            binding.ivAttachOpen.visible()

//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            resultImageLauncher.launch(Intent.createChooser(intent, "Select an Image"))

            imagePicker?.pickImage()
            dialog.dismiss()
        }
        dialogBinding.imgVideo.setOnClickListener {
            binding.ivAttachOpen.visible()

//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            resultVideoLauncher.launch(Intent.createChooser(intent, "Select Video"))

            videoPicker?.pickVideo()
            dialog.dismiss()
        }

        dialog.setOnCancelListener {
            binding.ivAttachOpen.visible()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        binding.ivAttachOpen.visible()
        super.onBackPressed()
        binding.ivAttachOpen.visible()
    }

    private fun showMoveDownButton(show: Boolean) {
        if (show) {
            binding.ivMoveDown.visible()
        } else {
            binding.ivMoveDown.gone()
            binding.tvUnreadCount.gone()

            readAllMessages()
        }
    }

    private fun scrollToBottom(isSendMessage: Boolean = true) {
        if (isSendMessage || (binding.rvChat.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == chatAdapter.itemCount - 1 || !binding.ivMoveDown.isVisible) {
            val iPos = chatAdapter.itemCount
            if (iPos >= 2) binding.rvChat.scrollToPosition(iPos - 2)
            smoothScroller.targetPosition = iPos
            binding.rvChat.layoutManager?.startSmoothScroll(smoothScroller)
        } else {
            if (binding.ivMoveDown.isVisible) {
                binding.tvUnreadCount.isVisible = true
                binding.tvUnreadCount.text = pref.unreadChatMessages.size.toString()
            }
        }
    }

    private fun readAllMessages() {
        inbox?.let { inbox ->
            var allMessageSeen = true
            run breaker@{
                chatAdapter.displayList.forEach {
                    if (it.sender_id == inbox.chat_user_id && it.read_status < ReadStatus.READ.status) {
                        allMessageSeen = false
                        return@breaker
                    }
                }
            }
            if (!allMessageSeen) {
                chatAdapter.displayList.forEach {
                    if (it.sender_id == inbox.chat_user_id && it.read_status < ReadStatus.READ.status)
                        it.read_status = ReadStatus.READ.status
                }
                FixerSocketService.getInstance()?.sendReadAllMessages(inbox.chat_id)

                val list = pref.unreadChatMessages
                val isRemoved = list.removeAll { it.chat_id == inbox.chat_id }
                if (isRemoved) {
                    pref.unreadChatMessages = list
                }

                val bundle = Bundle()
                bundle.putParcelable(BUS_EVENT_READ_ALL_MESSAGES_LOCALLY, InboxItem(inbox.chat_id))
                EventBus.publish(bundle)
            }
        }
    }

    private val typingRunnable = object : Runnable {
        override fun run() {
            Timber.d("typing: $isTyping")
            val text = binding.etMessage.text.toString()

            isTyping = text.isNotEmpty()

            if (typedString.length == text.length) {
                Timber.d("lengths are same")
                if (isTyping)
                    isTyping = false
            }

            inbox?.let {
                FixerSocketService.getInstance()?.sendTypingStatus(isTyping, it.chat_id)
            }

            typedString = text

            /**
             * this means it will check only if user is typing for making it as stopped after 1 second.
             * if we will check in both case then it will continuously send when user is not typing.*/
            if (isTyping)
                typingHandler.postDelayed(this, 500)
        }
    }

//    private var resultImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val file = File(result.data?.data?.let { getRealPathFromURI(it) }.orEmpty())
//            if (file.exists()) {
//                isImageUpload = true
//                val builder = file.multipartImageBody("media")
//                builder.addFormDataPart("type", "1")
//                viewModel.uploadChatMedia(builder.build())
//            }
//        }
//    }
//
//    private var resultVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val file = File(result.data?.data?.let { getRealPathFromURI(it) }.orEmpty())
//            if (file.exists()) {
//                isImageUpload = false
//                val builder = file.multipartImageBody("media")
//                builder.addFormDataPart("type", "2")
//                viewModel.uploadChatMedia(builder.build())
//            }
//        }
//    }

//    private fun getRealPathFromURI(contentURI: Uri): String {
//        val result: String
//        val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.path.orEmpty()
//        } else {
//            cursor.moveToFirst()
//            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker?.submit(data)
            }

            if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                videoPicker?.submit(data)
            }
        }
    }


    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onAllowStoragePermission() {
        binding.ivAttachOpen.isVisible = false
        attachAlert()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onRationaleStoragePermission(request: PermissionRequest) {
        showAskingPermissionDialog("Need Storage Permission to share Media",
            negativeClick = {
                request.cancel()
            }, positiveClick = {
                request.proceed()
            })
    }

    @OnPermissionDenied(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onDeniedStoragePermission() {
    }

    @OnNeverAskAgain(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onNeverAskStoragePermission() {
        showDeniedPermissionDialog(
            "Please Allow Storage Permission From Settings.",
            negativeClick = {}, positiveClick = {}
        )
    }
}