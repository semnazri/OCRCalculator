package com.semnazri.ocrcalculator

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.semnazri.ocrcalculator.base.BaseActivity
import com.semnazri.ocrcalculator.databinding.ActivityMainBinding
import com.semnazri.ocrcalculator.util.subscribeState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException

class MainActivity : BaseActivity() {
    private var latestTmpUri: Uri? = null
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var viewBinding: ActivityMainBinding
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    try {
                        recognizer.process(InputImage.fromFilePath(applicationContext, uri))
                            .addOnSuccessListener {
                                extractionProcess(it)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                recognizer.process(InputImage.fromFilePath(applicationContext, uri))
                    .addOnSuccessListener {
                        extractionProcess(it)
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
        }


    private fun extractionProcess(result: Text) {
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    Log.d(TAG, "Element Text : $elementText")

                }
                Log.d(TAG, "Line Text : $lineText")
            }
            Log.d(TAG, "Block Text : $blockText")
            viewModel.callEvent(MainViewModel.Event.CalculateObject(blockText))
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (BuildConfig.FLAVOR_functionality == "cameraBuildIn"){
                    takeImage()
                }else{
                    selectImageFromGallery()
                }
            }
        }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }


    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")


    override fun getLayoutResource(): ConstraintLayout {
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun initPage() {

        button_input.setOnClickListener {
            if (allPermissionsGranted()) {
                if (BuildConfig.FLAVOR_functionality == "cameraBuildIn"){
                    takeImage()
                }else{
                    selectImageFromGallery()
                }
            } else {
                requestPermissions()
            }
        }

    }


    override fun observeData() {
        subscribeState(viewModel.state){
            when(it){
                is MainViewModel.State.ShowResultCalculate -> {
                    viewBinding.fieldInput.text = "Input : ${it.input}"
                    viewBinding.fieldResult.text = "Result : ${it.result}"
                }
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }


}