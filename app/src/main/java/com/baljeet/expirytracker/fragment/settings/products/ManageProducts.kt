package com.baljeet.expirytracker.fragment.settings.products

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.viewmodels.ImageViewModel
import com.baljeet.expirytracker.data.viewmodels.ProductViewModel
import com.baljeet.expirytracker.databinding.FragmentManageProductsBinding
import com.baljeet.expirytracker.interfaces.OnProductSelected
import com.baljeet.expirytracker.listAdapters.ProductResultsAdapter

class ManageProducts : Fragment(), OnProductSelected {

    private lateinit var bind: FragmentManageProductsBinding
    private val viewModel: ProductViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private lateinit var resultsAdapter: ProductResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManageProductsBinding.inflate(inflater, container, false)
        bind.apply {
            backButton.setOnClickListener { activity?.onBackPressed() }

            productsRecycler.layoutManager = GridLayoutManager(requireContext(),
                if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 6 else 4
                )
            resultsAdapter = ProductResultsAdapter(requireContext(), imageViewModel,this@ManageProducts)
            productsRecycler.adapter = resultsAdapter
            searchEdittext.doOnTextChanged { text, _, _, _ ->
                if (text.toString().count() > 0) {
                    viewModel.searchByText(text.toString())
                } else {
                    viewModel.getAllProducts()
                }
            }

            viewModel.getAllProducts()

            viewModel.searchResults.observe(viewLifecycleOwner) {
                resultsAdapter.submitList(it)
                resultsCount.text =
                    requireContext().getString(R.string.number_of_results_var, it.size)
            }

        }
        return bind.root
    }

    override fun openInfoOfProduct(product: Product) {
        Navigation.findNavController(requireView())
            .navigate(ManageProductsDirections.actionManageProductsToProductInfo(product))
    }

    override fun onResume() {
        super.onResume()
        if (bind.searchEdittext.text.toString().count() > 0) {
            viewModel.searchByText(bind.searchEdittext.text.toString())
        } else {
            viewModel.getAllProducts()
        }
    }
}