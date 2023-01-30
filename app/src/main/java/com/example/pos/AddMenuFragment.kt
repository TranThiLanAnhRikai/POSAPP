package com.example.pos


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos.data.repository.MenuItemRepository
import com.example.pos_admin.databinding.FragmentAddMenuBinding
import com.example.pos.model.MenuViewModel
import com.example.pos.model.MenuViewModelFactory
import com.example.pos_admin.R
import java.io.ByteArrayOutputStream


class AddMenuFragment : Fragment() {
    private val cameraRequestId = 1
    private val uploadRequestId = 2
    private val itemTypes =  arrayOf(ItemType.FOOD, ItemType.DESERT, ItemType.DRINK)
    private val menuViewModel: MenuViewModel by activityViewModels {
        MenuViewModelFactory(
            MenuItemRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).menuItemDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao(),
                PosAdminRoomDatabase.getDatabase(requireContext()).cartItemDao()
            )
        )
    }
    private var binding: FragmentAddMenuBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddMenuBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.addMenuFragment = this
        binding?.menuViewModel = menuViewModel
        val options = itemTypes.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose the type")
        builder.setItems(options) { _, which ->
            val selectedItemType = itemTypes[which]
            binding?.typePick?.text = selectedItemType.typeName
            menuViewModel.type.value = selectedItemType.typeName
        }


        val dialog = builder.create()
        val container = binding?.typePickContainer
        container?.setOnClickListener {
            dialog.show()
        }
        binding?.addImgText?.setOnClickListener {
            val options = arrayOf("Take Photo", "Upload Photo")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_DENIED
                        )
                            ActivityCompat.requestPermissions(
                                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                                cameraRequestId
                            )
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(cameraIntent, cameraRequestId)
                    }
                    1 -> {
                        val uploadIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(uploadIntent, uploadRequestId)
                    }
                }
            }
            builder.create().show()
        }
    }


        /*binding?.addImgText?.setOnClickListener {


            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            )
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.CAMERA),
                    cameraRequestId
                )
            binding?.addImgText?.setOnClickListener {
                val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraInt, cameraRequestId)
            }


        }
    }
    */
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == cameraRequestId) {
                val images: Bitmap = data?.extras?.get("data") as Bitmap
                binding?.itemImg?.setImageBitmap(images)
                val bitmap: Bitmap = images
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageInByte = byteArrayOutputStream.toByteArray()
                val imageToStore = encodeToString(imageInByte, DEFAULT)
                menuViewModel.image.value = imageToStore
            }
            if (requestCode == uploadRequestId) {
                val selectedImage = data?.data
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    selectedImage
                )
                binding?.itemImg?.setImageBitmap(bitmap)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageInByte = byteArrayOutputStream.toByteArray()
                val imageToStore = encodeToString(imageInByte, DEFAULT)
                menuViewModel.image.value = imageToStore
            }

        }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    /*fun addNewItem() {
        //Check whether all fields have bene filled and a photo has been uploaded/taken
        val inputName = binding?.nameEdttxt
        val typeContainer = binding?.typePickContainer
        val inputPrice = binding?.priceEdttxt
        val uploadedPhoto = binding?.itemImg

        var isOptionSelected = false
        for (i in 0 until typeContainer!!.childCount) {
            val child = typeContainer.getChildAt(i)
            if (child is RadioButton) {
                if (child.isChecked) {
                    isOptionSelected = true
                    continue
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please select the type of the item.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (inputName!!.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill in the name of the item.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (inputPrice!!.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill in the price of the item.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (uploadedPhoto == null) {
                    Toast.makeText(
                        requireContext(),
                        "Please take a photo or upload one",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    menuViewModel.insertItem()
                }
            }
        }

    }*/
    fun addNewItem() {
        //Check whether all fields have bene filled and a photo has been uploaded/taken
        val inputName = binding?.nameEdttxt
        val typeContainer = binding?.typePickContainer
        val inputPrice = binding?.priceEdttxt
        val uploadedPhoto = binding?.itemImg

       /* var isOptionSelected = false
        for (i in 0 until typeContainer!!.childCount) {
            val child = typeContainer.getChildAt(i)
            if (child is RadioButton) {
                if (child.isChecked) {
                    isOptionSelected = true
                }
            }
        }
        if (!isOptionSelected || inputName!!.toString().isEmpty() || inputPrice!!.toString()
                .isEmpty() || uploadedPhoto == null
        ) {
            if (!isOptionSelected) {
                Toast.makeText(
                    requireContext(),
                    "Please select the type of the item.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (inputName!!.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill in the name of the item.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (inputPrice!!.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill in the price of the item.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (uploadedPhoto == null) {
                Toast.makeText(
                    requireContext(),
                    "Please take a phot or upload one.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {*/
        menuViewModel.insertItem()


    }


            fun backToMenu() {
        findNavController().navigate(R.id.action_addMenuFragment_to_menuFragment)
    }
}
