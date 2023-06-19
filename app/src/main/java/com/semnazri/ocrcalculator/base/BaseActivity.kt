package com.semnazri.ocrcalculator.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.semnazri.ocrcalculator.BuildConfig

abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayoutResource(): ConstraintLayout
    abstract fun initPage()
    abstract fun observeData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
        initPage()
        observeData()
    }

     fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        const val TAG = "OCR Calculator"
        const val FILENAME_FORMAT = "yyyy-MM-dd-HH:mm"
        const val PERMISSION_REQUESTS = 1
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}