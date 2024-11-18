package com.example.kidsdrawing

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var currentPaint: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawingView = findViewById(R.id.drawing_view)
        drawingView.setSizeForBrush(20.toFloat())

        val linerLayout = findViewById<LinearLayout>(R.id.ll_color_palette)
        currentPaint = linerLayout.getChildAt(1) as ImageButton
        currentPaint
            .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))

        linerLayout
            .forEach { view -> onClickPallet(view as ImageButton) }

        val ibBrush = findViewById<ImageButton>(R.id.ib_brush)
        ibBrush.setOnClickListener { showBrushSizeChooserDialog() }
    }


    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size:")

        val smallBtn = brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        val mediumBtn = brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        val largeBtn = brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    private fun onClickPallet(view: View) {
    }
}