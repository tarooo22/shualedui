package com.vereshchagin.nikolay.pepegafood.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.FragmentHomeBinding
import com.vereshchagin.nikolay.pepegafood.ui.home.paging.catalog.CatalogAdapter
import com.vereshchagin.nikolay.pepegafood.ui.home.paging.favorite.FavoriteBasketAdapter
import com.vereshchagin.nikolay.pepegafood.ui.home.paging.shopping.ShoppingBasketAdapter


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeSearch.setOnClickListener { onSearchViewClicked() }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this,
            HomeViewModel.Factory(activity?.application!!))
            .get(HomeViewModel::class.java)

        initShoppingBaskets()
        initFavoriteBaskets()
        initCatalogItems()
    }


    private fun initShoppingBaskets() {
        val shoppingBasketAdapter = ShoppingBasketAdapter()
        binding.recyclerAutoBaskets.adapter = shoppingBasketAdapter

        viewModel.shoppingBaskets.observe(viewLifecycleOwner, Observer {
            shoppingBasketAdapter.submitList(it)
        })
    }


    private fun initFavoriteBaskets() {
        val favoriteBasketAdapter = FavoriteBasketAdapter()
        binding.recyclerFavorites.adapter = favoriteBasketAdapter

        viewModel.favoriteBaskets.observe(viewLifecycleOwner, Observer {
            favoriteBasketAdapter.submitList(it)
        })
    }


    private fun initCatalogItems() {
        val catalogAdapter = CatalogAdapter()
        binding.recyclerCatalog.adapter = catalogAdapter

        viewModel.catalogItems.observe(viewLifecycleOwner, Observer {
            catalogAdapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun onSearchViewClicked() {
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.to_search_fragment)
    }
}