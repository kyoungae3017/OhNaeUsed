package com.kyoungae.ohnaejunggo.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kyoungae.ohnaejunggo.R
import com.kyoungae.ohnaejunggo.adapter.GalleryAdapter
import com.kyoungae.ohnaejunggo.adapter.PickedPhotoAdapter
import com.kyoungae.ohnaejunggo.data.Image
import com.kyoungae.ohnaejunggo.databinding.*
import com.kyoungae.ohnaejunggo.util.PICKED_PHOTOS
import com.kyoungae.ohnaejunggo.viewmodel.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var adapter: GalleryAdapter
    private var isValidValue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return DataBindingUtil.inflate<FragmentGalleryBinding>(
            inflater,
            R.layout.fragment_gallery,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle()

        adapter = GalleryAdapter(viewModel)
        binding.recyclerView.adapter = adapter

        viewModel.isValidValue.observe(viewLifecycleOwner, {
            isValidValue = it
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.galleryData.observe(viewLifecycleOwner, {
            it.let { adapter.submitList(it) }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_writing_product, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_complete)
        item.isVisible = isValidValue
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_complete -> {
                val images = mutableListOf<Image>()
                viewModel.pickedPhotos?.map {
                    images.add(Image(it.contentUri))
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set(PICKED_PHOTOS, images)
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setToolbarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "사진 선택"
    }

    private fun goToMainFragment() {
        val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
        findNavController().navigate(action)
    }

    companion object {
        private const val TAG = "GalleryFragment"
    }
}