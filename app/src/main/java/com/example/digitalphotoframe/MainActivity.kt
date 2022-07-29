package com.example.digitalphotoframe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // specify launcher intent
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"

        // trigger intent immediately
        launcher.launch(intent)
    }

    /**
     * launcher for selecting user photos
     */
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            // set photo list to data
            val imageUris: MutableList<Uri> = mutableListOf()
            if (data?.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else if (data?.data != null) {
                val imageUri: Uri = data.data!!
                imageUris.add(imageUri)
            }

            if (imageUris.size > 0) {
                // get target image view
                val imageView: ImageView = findViewById(R.id.imageView)

                // start cycle
                cycleUris(0, imageUris, imageView, 2000)
            } else {
                findViewById<ImageView>(R.id.imageView).setImageResource(R.drawable.default_img)
            }
        }
    }

    /**
     * Cycle through imageUris
     *
     * @param currIndex current index of imageUris to display
     * @param imageUris list of imageUris being displayed
     * @param imageView view whose image source is being changed
     * @param delay delay between each photo change
     */
    private fun cycleUris(currIndex: Int, imageUris: List<Uri>, imageView: ImageView, delay: Long) {
        // cycle current
        val index = currIndex % imageUris.size

        // set new image
        imageView.setImageURI(imageUris[index])

        // delay next method call
        Handler(Looper.getMainLooper()).postDelayed({
            cycleUris(index + 1, imageUris, imageView, delay)
        }, delay)
    }
}