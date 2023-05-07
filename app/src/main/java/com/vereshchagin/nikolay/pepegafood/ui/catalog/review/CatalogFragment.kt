package com.vereshchagin.nikolay.pepegafood.ui.catalog.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.FragmentCatalogBinding
import com.vereshchagin.nikolay.pepegafood.ui.BaseFragment
import com.vereshchagin.nikolay.pepegafood.ui.catalog.review.paging.CatalogItemAdapter


class CatalogFragment : BaseFragment<FragmentCatalogBinding>() {

    private val viewModel by viewModels<CatalogViewModel> {
        CatalogViewModel.Factory(activity?.application!!)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentCatalogBinding {
        return FragmentCatalogBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.catalogSearch.setOnClickListener {
            onSearchViewClicked()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = CatalogItemAdapter()
        binding.recyclerCatalog.adapter = adapter
        viewModel.catalogItems.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }


    private fun onSearchViewClicked() {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.to_search_fragment)
    }
}