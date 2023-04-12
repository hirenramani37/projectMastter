package com.massttr.user.ui.main.myprofile.order_history.order_details.showImage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.common.base.BaseAdapter
import com.common.data.network.model.JobImage
import com.kbeanie.multipicker.utils.FileUtils.getExternalFilesDir
import com.massttr.user.R
import com.massttr.user.databinding.ItemShowImagePagerBinding
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
class ImageViewPagerAdapter(val context: Context) :
    BaseAdapter<ItemShowImagePagerBinding, JobImage>(R.layout.item_show_image_pager) {
    override fun setClickableView(binding: ItemShowImagePagerBinding): List<View?> {
        return listOf()
    }

    override fun onBind(
        binding: ItemShowImagePagerBinding,
        position: Int,
        item: JobImage,
        payloads: MutableList<Any>?,
    ) {
        binding.run {
            Glide.with(context)
                .load(item.image.toUri())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(ivSliderImage)

            ivShareImage.setOnClickListener {
                val bitmapDrawable = ivSliderImage.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                /*val bitmapPath = MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap,
                    "Photos of the task",
                    null
                )
                val file = File(
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    "share_image_" + System.currentTimeMillis() + ".jpg"
                )*/
                //Timber.e("file: ${file}")
                val file = File(item.image, "yourfile.jpg")
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                val bitmapUri = Uri.parse(file.toString())
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
                intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image")
                intent.type = "image/*"
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(Intent.createChooser(intent, "Share image via"))
            }
        }
    }
}

