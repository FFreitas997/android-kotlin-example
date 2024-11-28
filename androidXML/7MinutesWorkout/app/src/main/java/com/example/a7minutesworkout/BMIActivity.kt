package com.example.a7minutesworkout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a7minutesworkout.databinding.ActivityBmiactivityBinding
import com.example.a7minutesworkout.model.exercise.Metric
import java.util.Locale

class BMIActivity : AppCompatActivity() {

    private var binding: ActivityBmiactivityBinding? = null

    private var inputWeight: Float? = null
    private var inputHeight: Float? = null
    private var feetValue: Float? = null
    private var inchValue: Float? = null
    private var statusMetric: Metric = Metric.METRIC_UNITS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityBmiactivityBinding
            .inflate(layoutInflater)
            .also { setContentView(it.root) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding?.toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = getString(R.string.calculate_bmi_toolbar_title)
        }

        binding
            ?.toolbar
            ?.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding
            ?.btnCalculateUnits
            ?.setOnClickListener {
                when (statusMetric) {
                    Metric.METRIC_UNITS -> onClickButtonMetricUnits()
                    Metric.US_UNITS -> onClickButtonUSUnits()
                    else -> throw IllegalStateException("Unexpected value: $statusMetric")
                }
            }

        binding
            ?.rgUnits
            ?.setOnCheckedChangeListener { _, checkedId ->
                binding?.llDisplayBMIResult?.visibility = android.view.View.INVISIBLE
                when (checkedId) {
                    R.id.rbMetricUnits -> {
                        binding?.etMetricUnitHeight?.visibility = android.view.View.VISIBLE
                        binding?.usMetrics?.visibility = android.view.View.INVISIBLE
                        statusMetric = Metric.METRIC_UNITS
                        binding?.btnCalculateUnits?.isEnabled = isValidMetricsUnitInput()
                    }

                    R.id.rbImperialUnits -> {
                        binding?.etMetricUnitHeight?.visibility = android.view.View.INVISIBLE
                        binding?.usMetrics?.visibility = android.view.View.VISIBLE
                        statusMetric = Metric.US_UNITS
                        binding?.btnCalculateUnits?.isEnabled = isValidUsMetricsInput()
                    }

                    else -> throw IllegalStateException("Unexpected value: $checkedId")
                }
            }

        binding
            ?.etMetricUnitWeightValue
            ?.addTextChangedListener(weightWatcher)

        binding
            ?.etMetricUnitHeightValue
            ?.addTextChangedListener(heightWatcher)

        binding
            ?.usMetricsFeetValue
            ?.addTextChangedListener(feetWatcher)

        binding
            ?.usMetricsInchValue
            ?.addTextChangedListener(inchWatcher)

    }

    private fun isValidMetricsUnitInput(): Boolean {
        return binding?.etMetricUnitHeightValue?.text.toString().isNotEmpty() &&
                binding?.etMetricUnitWeightValue?.text.toString().isNotEmpty()
    }

    private fun isValidUsMetricsInput(): Boolean {
        return binding?.usMetricsFeetValue?.text.toString().isNotEmpty() &&
                binding?.usMetricsInchValue?.text.toString().isNotEmpty() &&
                binding?.etMetricUnitWeightValue?.text.toString().isNotEmpty()
    }

    private fun displayBMIResult(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        when {
            bmi < 16 -> {
                bmiLabel = getString(R.string.severely_underweight)
                bmiDescription =
                    getString(R.string.you_really_need_to_take_care_of_your_health_eat_more)
            }

            bmi < 18.5 -> {
                bmiLabel = getString(R.string.underweight)
                bmiDescription = getString(R.string.you_need_to_take_care_of_your_health_eat_more)
            }

            bmi < 25 -> {
                bmiLabel = getString(R.string.normal)
                bmiDescription = getString(R.string.congratulations_you_are_in_a_good_shape)
            }

            bmi < 30 -> {
                bmiLabel = getString(R.string.overweight)
                bmiDescription =
                    getString(R.string.you_need_to_take_care_of_your_health_exercise_more)
            }

            bmi < 35 -> {
                bmiLabel = getString(R.string.obese_class_i_moderate)
                bmiDescription =
                    getString(R.string.you_need_to_take_care_of_your_health_exercise_more)
            }

            bmi < 40 -> {
                bmiLabel = getString(R.string.obese_class_ii_severe)
                bmiDescription =
                    getString(R.string.you_need_to_take_care_of_your_health_exercise_more)
            }

            else -> {
                bmiLabel = getString(R.string.obese_class_iii_very_severe)
                bmiDescription =
                    getString(R.string.you_need_to_take_care_of_your_health_exercise_more)
            }
        }

        binding?.tvBMIValue?.text = String.format(Locale.getDefault(), "%.2f", bmi)
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription

        binding?.llDisplayBMIResult?.visibility = android.view.View.VISIBLE
    }

    private fun onClickButtonMetricUnits() {
        if (!isValidMetricsUnitInput() || inputHeight == null || inputWeight == null)
            return

        val heightValue = inputHeight!! / 100
        val weightValue = inputWeight!!

        val bmiMetric = weightValue / (heightValue * heightValue)

        displayBMIResult(bmiMetric)
    }

    private fun onClickButtonUSUnits() {
        if (!isValidUsMetricsInput() || feetValue == null || inchValue == null || inputWeight == null)
            return

        val heightValue = (feetValue!! * 12 + inchValue!!) * 0.0254
        val weightValue = inputWeight!!

        val bmiUS = (weightValue / (heightValue * heightValue)) * 703

        displayBMIResult(bmiUS.toFloat())
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private val weightWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding
                ?.etMetricUnitWeightValue
                ?.error = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputWeight = s.toString().toFloatOrNull()
        }

        override fun afterTextChanged(editable: Editable?) {
            binding
                ?.btnCalculateUnits
                ?.isEnabled =
                if (statusMetric == Metric.METRIC_UNITS)
                    isValidMetricsUnitInput()
                else
                    isValidUsMetricsInput()

            if (editable == null || editable.isEmpty())
                binding
                    ?.etMetricUnitWeightValue
                    ?.error = getString(R.string.please_enter_your_weight)
        }
    }

    private val heightWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding
                ?.etMetricUnitHeightValue
                ?.error = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputHeight = s.toString().toFloatOrNull()
        }

        override fun afterTextChanged(editable: Editable?) {
            binding
                ?.btnCalculateUnits
                ?.isEnabled = isValidMetricsUnitInput()

            if (editable == null || editable.isEmpty())
                binding
                    ?.etMetricUnitHeightValue
                    ?.error = getString(R.string.please_enter_your_height)
        }
    }

    private val feetWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding
                ?.usMetricsFeetValue
                ?.error = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            feetValue = s.toString().toFloatOrNull()
        }

        override fun afterTextChanged(editable: Editable?) {
            binding
                ?.btnCalculateUnits
                ?.isEnabled = isValidUsMetricsInput()

            if (editable == null || editable.isEmpty())
                binding
                    ?.usMetricsFeetValue
                    ?.error = getString(R.string.please_enter_your_feet_value)
        }
    }

    private val inchWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            binding
                ?.usMetricsInchValue
                ?.error = null
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inchValue = s.toString().toFloatOrNull()
        }

        override fun afterTextChanged(editable: Editable?) {
            binding
                ?.btnCalculateUnits
                ?.isEnabled = isValidUsMetricsInput()

            if (editable == null || editable.isEmpty())
                binding
                    ?.usMetricsInchValue
                    ?.error = getString(R.string.please_enter_your_inch_value)
        }
    }
}