package com.massttr.user.utils

import com.common.data.network.model.PhotosTask
import com.common.data.network.model.SelectPhotos
import com.massttr.user.R
import java.io.File

class DummyClass {
    companion object {
        val photosTask = arrayListOf(
            PhotosTask(R.drawable.img_photos_task_1),
            PhotosTask(R.drawable.img_photos_task_2),
            PhotosTask(R.drawable.img_photos_task_3),
            PhotosTask(R.drawable.img_photos_task_1),
            PhotosTask(R.drawable.img_photos_task_2),
            PhotosTask(R.drawable.img_photos_task_3),
        )

        val selectPhotos = arrayListOf(
            SelectPhotos(" ")
        )

        //val file = arrayListOf(File("","","",""))

    }
}