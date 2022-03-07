package com.kyoungae.ohnaejunggo.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.play.core.internal.bi
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.adapter.PickedPhotoAdapter
import com.kyoungae.ohnaejunggo.data.Gallery
import com.kyoungae.ohnaejunggo.databinding.FragmentWritingProductBinding
import com.kyoungae.ohnaejunggo.util.CommonUtil.Companion.makeCommaString
import com.kyoungae.ohnaejunggo.util.CommonUtil.Companion.removeComma
import com.kyoungae.ohnaejunggo.viewmodel.ProductWritingViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import com.kyoungae.ohnaejunggo.activity.SubActivity
import com.kyoungae.ohnaejunggo.data.Image
import com.kyoungae.ohnaejunggo.util.*


@AndroidEntryPoint
class ProductWritingFragment : Fragment() {

    private lateinit var binding: FragmentWritingProductBinding
    private val viewModel: ProductWritingViewModel by viewModels()
    private var isValidValue = false
    private var isCameraButtonClicked = false
    private lateinit var adapter: PickedPhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentWritingProductBinding>(
            inflater,
            R.layout.fragment_writing_product,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()
        initEditTextView()

        adapter = PickedPhotoAdapter(viewModel)
        binding.imageList.adapter = adapter
        binding.imageList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        viewModel.isValidValue.observe(viewLifecycleOwner, {
            isValidValue = it
            Log.d(TAG, "onViewCreated: $it")
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.isGalleryPermissionCheck.observe(viewLifecycleOwner, {
            val photoCount = viewModel.numberOfSelectablePhotos()
            if (it && isCameraButtonClicked && photoCount != 0) {
                goToGalleryFragment(photoCount)
                isCameraButtonClicked = false
            }
        })

        binding.cameraButton.setOnClickListener {
            isCameraButtonClicked = true
            CommonUtil.closeKeyboard(binding.cameraButton, requireContext())
            viewModel.galleryPermissionCheck()
        }

        viewModel.pickedPhotoCount.observe(viewLifecycleOwner, {
            binding.photoCount.text = "$it/$PICK_LIMIT_COUNT"
            viewModel.checkValidValue()
        })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MutableList<Image>>(
            PICKED_PHOTOS
        )?.observe(viewLifecycleOwner) {
            viewModel.addPickedPhotos(it)
            adapter.submitList(viewModel.pickedPhotos)
        }

        viewModel.isCreateProduct.observe(viewLifecycleOwner, {
            if (it) {
                binding.progressbar.visibility = View.GONE
                goToProductDetailFragment(viewModel.productId)
            } else {
                CommonUtil.showSnackbarOfUnknownIssues(binding.parentLayout)
            }
        })

        viewModel.productData.observe(viewLifecycleOwner, { product ->
            val transImages = viewModel.productImagesToLocalImages(product.imagePaths!!)
            viewModel.getImageDataAddPickedPhoto(transImages)
            adapter.submitList(viewModel.pickedPhotos)
            binding.title.setText(product.title)
            binding.price.setText(product.price.toString())
            binding.explanation.setText(product.explanation)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_writing_product, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_complete)
        item.isVisible = isValidValue
        Log.d(TAG, "onPrepareOptionsMenu: $isValidValue")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_complete -> {
                binding.progressbar.visibility = View.VISIBLE
                viewModel.createNewProduct()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setToolbarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "중고거래 글쓰기"
    }

    private fun goToGalleryFragment(pickCount: Int) {
        val bundle = bundleOf(PICK_COUNT to pickCount)
        findNavController().navigate(R.id.galleryFragment, bundle)
    }

    private fun initEditTextView() {
        binding.price.addTextChangedListener { text ->
            val textString: String = text.toString()
            if (textString!= "0" && textString.isNotEmpty()) {
                Log.d(TAG, "initEditTextView: ${textString.removeComma()}")
                viewModel.price = textString.removeComma().toLong()
                viewModel.checkValidValue()
            }
        }

        binding.price.setOnFocusChangeListener { view, b ->
            val priceValue = binding.price.text.toString()
            if (!priceValue.isNullOrEmpty()) {
                var priceString: String
                if (b) {
                    priceString = priceValue.removeComma()
                    setEditTextMaxLength(binding.price, MAX_LENGTH_OF_NO_COMMA_PRICE)

                } else {
                    priceString = priceValue.toLong().makeCommaString()
                    setEditTextMaxLength(binding.price, MAX_LENGTH_OF_COMMA_PRICE)
                }
                binding.price.setText(priceString)
            }
        }

        binding.title.addTextChangedListener { text ->
            viewModel.title = text.toString()
            viewModel.checkValidValue()
        }

        binding.explanation.addTextChangedListener { text ->
            viewModel.explanation = text.toString()
            viewModel.checkValidValue()
        }
    }

    fun setEditTextMaxLength(view: EditText, length: Int) {
        view.filters = arrayOf<InputFilter>(
            LengthFilter(
                length
            )
        )
    }

    private fun goToProductDetailFragment() {
        val intent = Intent(context, SubActivity::class.java).apply {
            putExtra(GRAPH_ID, R.navigation.nav_graph_writing_product)
            putExtra(PRODUCT_ID, viewModel.productId)
        }
        startActivity(intent)
    }

    private fun goToProductDetailFragment(productId: String) {
        findNavController().navigateUp() // to clear previous navigation history

        val bundle = bundleOf(PRODUCT_ID to productId)
        findNavController().navigate(R.id.productDetailFragment, bundle)
    }

    companion object {
        private const val TAG = "ProductWritingFragment"
    }
}