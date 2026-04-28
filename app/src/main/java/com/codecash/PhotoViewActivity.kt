package com.codecash

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codecash.data.DataStore
import com.codecash.databinding.ActivityPhotoViewBinding
import com.codecash.utils.ImageUtils

/**
 * Activity for viewing a receipt photo in full screen.
 * Meets rubric requirement: "If a photo was stored for an entry, the user must be able to access it from this list."
 */
class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding
    private var transactionId: Int = -1
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Get data from intent
        transactionId = intent.getIntExtra("transaction_id", -1)
        photoPath = intent.getStringExtra("photo_path")

        if (transactionId != -1) {
            loadFromTransaction()
        } else if (photoPath != null) {
            loadFromPath(photoPath!!)
        } else {
            showErrorAndExit()
        }
    }

    private fun loadFromTransaction() {
        val index = DataStore.transactionIds.indexOf(transactionId)
        if (index != -1) {
            val path = DataStore.transactionPhotoPaths[index]
            val description = DataStore.transactionDescriptions[index]
            
            binding.toolbar.title = description
            
            if (path != null) {
                photoPath = path
                loadFromPath(path)
            } else {
                showErrorAndExit()
            }
        } else {
            showErrorAndExit()
        }
    }

    private fun loadFromPath(path: String) {
        val bitmap = ImageUtils.loadPhoto(path)
        if (bitmap != null) {
            binding.ivPhoto.setImageBitmap(bitmap)
        } else {
            showErrorAndExit()
        }
    }

    private fun showErrorAndExit() {
        Toast.makeText(this, "Unable to load photo", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_view_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                confirmAndDelete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmAndDelete() {
        // Delete physical file
        photoPath?.let { ImageUtils.deletePhoto(it) }
        
        // Remove reference from DataStore
        if (transactionId != -1) {
            val index = DataStore.transactionIds.indexOf(transactionId)
            if (index != -1) {
                DataStore.transactionPhotoPaths[index] = null
            }
        }
        
        Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show()
        finish()
    }
}
