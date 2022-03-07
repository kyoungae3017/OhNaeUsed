package com.kyoungae.ohnaejunggo.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.adapter.ProductDetailViewPagerAdapter
import com.kyoungae.ohnaejunggo.data.Product
import com.kyoungae.ohnaejunggo.databinding.FragmentDetailProductBinding
import com.kyoungae.ohnaejunggo.util.CommonUtil.Companion.makeCommaString
import com.kyoungae.ohnaejunggo.util.CommonUtil.Companion.makeDateFormat
import com.kyoungae.ohnaejunggo.util.PRODUCT
import com.kyoungae.ohnaejunggo.util.PRODUCT_ID
import com.kyoungae.ohnaejunggo.viewmodel.ProductDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailProductBinding
    private val viewModel: ProductDetailViewModel by viewModels()
    private var isMyProduct: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentDetailProductBinding>(
            inflater,
            R.layout.fragment_detail_product,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle()

        viewModel.productData.observe(viewLifecycleOwner, { product ->
            if (product.imagePaths != null) {
                setViewPager(product.imagePaths)
            }
            binding.title.text = product.title
            val time = product.updateDate ?: product.createDate
            binding.updateDate.text = time!!.makeDateFormat()
            binding.price.text = "가격 ${product.price!!.makeCommaString()}원"
            binding.explanation.text = product.explanation

        })

        viewModel.isMyProduct.observe(viewLifecycleOwner, {
            isMyProduct = it
            Log.d(TAG, "onViewCreated: $isMyProduct")
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.productUserData.observe(viewLifecycleOwner, { user ->
            if (user.profileImage != null){
                binding.profileImage.setImageURI(Uri.parse(user.profileImage))
            }
            binding.nickname.text = user.nickname
        })

        binding.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${viewModel.productUserData.value!!.phoneNumber}")
            }
            startActivity(intent)
        }

        binding.smsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("smsto:${viewModel.productUserData.value!!.phoneNumber}")  // This ensures only SMS apps respond
            }
            startActivity(intent)
        }

        viewModel.isDelete.observe(viewLifecycleOwner,{
            if (it){
                requireActivity().finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail_product, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val update = menu.findItem(R.id.action_update)
        val delete = menu.findItem(R.id.action_delete)
        Log.d(TAG, "onPrepareOptionsMenu: $isMyProduct")
        update.isVisible = isMyProduct
        delete.isVisible = isMyProduct
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                goToProductWritingFragment(viewModel.productId)
                true
            }
            R.id.action_delete -> {
                viewModel.delete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setToolbarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
    }

    private fun setViewPager(imageList: MutableList<String>) {
        binding.viewPager.adapter = ProductDetailViewPagerAdapter(imageList) // 어댑터 생성
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        binding.indicator.setViewPager2(binding.viewPager)
    }

    private fun goToProductWritingFragment(productId: String) {
        findNavController().navigateUp() // to clear previous navigation history

        val bundle = bundleOf(PRODUCT_ID to productId)
        findNavController().navigate(R.id.productWritingFragment, bundle)
    }

    companion object{
        private const val TAG = "ProductDetailFragment"
    }
}