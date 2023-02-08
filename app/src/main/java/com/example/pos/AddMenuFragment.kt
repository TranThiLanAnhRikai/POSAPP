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
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pos_admin.const.ItemType
import com.example.pos_admin.databinding.FragmentAddMenuBinding
import com.example.pos.model.MenuViewModel
import com.example.pos_admin.R
import java.io.ByteArrayOutputStream

/** メニューに新しい物を追加させる
 */
class AddMenuFragment : Fragment() {
    private val cameraRequestId = 1
    private val uploadRequestId = 2
    private val itemTypes = arrayOf(ItemType.FOOD, ItemType.DESSERT, ItemType.DRINK)
    private val menuViewModel: MenuViewModel by activityViewModels()

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
        // 物のタイプのオプションをDialogで表示する
        val options = itemTypes.map { it.name }.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose the type")
        builder.setItems(options) { _, which ->
            val selectedItemType = itemTypes[which]
            binding?.typePick?.text = selectedItemType.toString()
            menuViewModel.type.value = selectedItemType.typeName
        }
        val dialog = builder.create()
        val container = binding?.typePickContainer
        container?.setOnClickListener {
            dialog.show()
        }
        binding?.addImgText?.setOnClickListener {
            val photoOptions = arrayOf("Take Photo", "Upload Photo")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an option")
            builder.setItems(photoOptions) { _, which ->
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

    @Deprecated("Deprecated in Java")
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

    // メニューに新しい物を追加する
    fun addNewItem() {
        //
        if (menuViewModel.itemName.value == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
            builder.setMessage("Please fill in the name of the item.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.type.value == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
            builder.setMessage("Please select the type of the item.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel._price.value == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
            builder.setMessage("Please fill in the price of the item.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else if (menuViewModel.image.value == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Error")
            builder.setMessage("Please take a photo or upload one.")
            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            menuViewModel.insertItem()
            binding?.nameInput?.text = null
            binding?.inputPrice?.text = null
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("New Item added")
/*            builder.setMessage("Please take a photo or upload one.")*/
            builder.setPositiveButton("Add another one") { dialog, _ ->
                dialog.dismiss()
                binding?.nameInput?.text = null
                binding?.inputPrice?.text = null
                binding?.typePick?.text = "Choose the type"
                binding?.itemImg?.setImageBitmap(null)
                binding?.inputPrice?.clearFocus()
            }
            builder.setNegativeButton("Go back to menu") { _, _ ->
                findNavController().navigate(R.id.action_addMenuFragment_to_menuFragment)
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()


        }
    }

}
