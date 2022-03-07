package com.kyoungae.ohnaejunggo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kyoungae.ohnaejunggo.util.CommonUtil
import javax.inject.Inject
import com.kyoungae.ohnaejunggo.util.USER
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.*

import kotlin.coroutines.resume


class ProductDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore
) {

    var storageRef: StorageReference = firebaseStorage.reference

    fun uploadImage(index: Int, image: Image, uploadUrl: (String?) -> Unit) {

        if (image.url == null) {
            val riversRef =
                storageRef.child("images/products/${CommonUtil.getCurrentTime()}_$index")
            val uploadTask = riversRef.putFile(Uri.parse("file://" + image.localPathName))

            uploadTask.addOnSuccessListener {
                riversRef.downloadUrl
                    .addOnSuccessListener { url ->
                        uploadUrl(url.toString())
                        Log.d(TAG, "uplodImage: $url 끝")
                    }.addOnFailureListener {
                        uploadUrl(null)
                    }
            }.addOnFailureListener { taskSnapshot ->
                uploadUrl(null)
            }
        }
    }

    suspend fun create(product: Product): String {

        val result = suspendCancellableCoroutine<String> { cont ->
            firebaseFirestore.collection(PRODUCT)
                .add(product)
                .addOnSuccessListener { documentReference ->
                    cont.resume(documentReference.id) }
                .addOnFailureListener { cont.resume("") }
        }
        return result
    }

    suspend fun update(documentId: String, product: Product): Boolean {

        val result = suspendCancellableCoroutine<Boolean> { cont ->

            val productData = mutableMapOf<String, Any?>()
            productData["userId"] = product.userId
            productData["imagePaths"] = product.imagePaths
            productData["title"] = product.title
            productData["price"] = product.price
            productData["explanation"] = product.explanation
            productData["updateDate"] = product.updateDate

            firebaseFirestore.collection(USER).document(documentId)
                .update(productData)
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
        return result
    }

    suspend fun getProductData(documentId: String): Product? {
        val result = suspendCancellableCoroutine<Product?> { cont ->
            firebaseFirestore.collection(PRODUCT).document(documentId)
                .get()
                .addOnSuccessListener {
                    val product = it.toObject<Product>()
                    cont.resume(product)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
        return result
    }

    suspend fun getUserData(documentId: String): User? {
        val result = suspendCancellableCoroutine<User?> { cont ->
            firebaseFirestore.collection(USER).document(documentId)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject<User>()
                    cont.resume(user)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
        return result
    }

    suspend fun delete(documentId: String): Boolean {
        val result = suspendCancellableCoroutine<Boolean> { cont ->
            firebaseFirestore.collection(PRODUCT).document(documentId)
                .delete()
                .addOnSuccessListener { cont.resume(true) }
                .addOnFailureListener { cont.resume(false) }
        }
        return result
    }

    suspend fun saveBitmapToJpeg(index: Int, image: Image): Image {

        var bitmap = BitmapFactory.decodeFile(image.localPathName)

        val maximagesize = 1000 * 800 // 저용량 변환중 최대 사이즈
        var realimagesize: Int = maximagesize
        var quality = 100 //사진퀄리티는 처음 100부터 줄여나가면서 용량을 맞춥니다.

        val storage: File = context.cacheDir; //임시파일(캐시라 적혀잇죠?)
        val fileName = "${CommonUtil.getCurrentTime()}_$index.jpg";  // 어짜피 임시파일이기 때문에 알맞게 적어주세요.

        val tempFile = File(storage, fileName);

        try {
            tempFile.createNewFile();  // 파일을 생성해주고

            //아래 부분이 가장 중요한 부분이에요.
            while (realimagesize >= maximagesize) {
                Log.d(TAG, "saveBitmapToJpeg: $realimagesize,$maximagesize")
                if (quality < 0) {
                    quality = 10
                }
                val out = FileOutputStream(tempFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                realimagesize = tempFile.length().toInt() //작아진 본 파일의 크기를 저장하여 다시 비교합니다.

                quality -= 20; //이부분으 줄면서 용량이 작아집니다.

                out.close(); // 마무리로 닫아줍니다.
            }

            Log.d(TAG, "imagelocation resizefilesize result: " + realimagesize);

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace();
        }

        Log.d(TAG, "saveBitmapToJpeg: ${tempFile.absolutePath}")

        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }

        return Image(tempFile.absolutePath, null)
    }

    companion object {
        private const val PRODUCT = "product"
        private const val TAG = "ProductDataSource"
    }
}