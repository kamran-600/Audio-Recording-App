package com.kamran.xurveykshan.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.kamran.xurveykshan.R
import com.kamran.xurveykshan.adapters.DataRecAdapter
import com.kamran.xurveykshan.databinding.ActivityDataBinding
import com.kamran.xurveykshan.data.DataEntity
import com.kamran.xurveykshan.viewModels.DataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DataActivity : AppCompatActivity() {

    private var _binding : ActivityDataBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<DataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityDataBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adapter = setUpRecyclerView()

        viewModel.listLiveData.observe(this){
            val titleEntity = DataEntity(0, -1, "Q2", "Recording", "Submit Time" )
            val dataListWithTitle = mutableListOf(titleEntity)
            dataListWithTitle.addAll(it)
            adapter.submitList(dataListWithTitle)


            // if no data then no data found text make visible
            binding.noDataFound.isVisible = it.isEmpty()
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun setUpRecyclerView(): DataRecAdapter {
        val adapter = DataRecAdapter()
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.adapter = adapter
        return adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}