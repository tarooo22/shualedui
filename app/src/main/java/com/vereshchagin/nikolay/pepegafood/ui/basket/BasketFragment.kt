package com.vereshchagin.nikolay.pepegafood.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.FragmentBasketBinding
import com.vereshchagin.nikolay.pepegafood.ui.BaseStateFragment
import com.vereshchagin.nikolay.pepegafood.ui.basket.paging.BasketItemAdapter
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout.Companion.CONTENT_STATE


class BasketFragment : BaseStateFragment<FragmentBasketBinding>() {

    companion object {
        private const val BASKET_EMPTY_STATE = 1
    }

    private val viewModel by viewModels<BasketViewModel> {
        BasketViewModel.Factory(activity?.application!!)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentBasketBinding {
        return FragmentBasketBinding.inflate(inflater, container, false)
    }

    override fun onStatefulLayout(): StatefulLayout {
        val statefulLayout = StatefulLayout(binding.root, BASKET_EMPTY_STATE, binding.basketEmpty)
        statefulLayout.addView(CONTENT_STATE, binding.basketContainer)
        return statefulLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sheetBehavior = BottomSheetBehavior.from(binding.favoriteBaskets)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable = false
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        sheetBehavior.setExpandedOffset(100)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.favoriteBasketsButton.setImageDrawable(
                    context?.getDrawable(
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            R.drawable.ic_common_arrow_up
                        } else {
                            R.drawable.ic_common_arrow_down
                        }
                    )
                )
            }
        })

        binding.favoriteBasketsButton.setOnClickListener {
            sheetBehavior.state = if (sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.basketToCatalog.setOnClickListener {
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.nav_catalog)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = BasketItemAdapter()
        binding.basketList.adapter = adapter
        viewModel.basketItems.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                adapter.submitList(it)
                stateful.setState(CONTENT_STATE)
            } else {
                stateful.setState(BASKET_EMPTY_STATE)
            }
        })
    }




}