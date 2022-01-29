package com.baljeet.expirytracker.fragment.shared

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Image
import com.baljeet.expirytracker.data.Product
import com.baljeet.expirytracker.data.relations.CategoryAndImage
import com.baljeet.expirytracker.data.relations.ProductAndImage
import kotlinx.datetime.LocalDateTime

class SelectFromViewModel : ViewModel() {

    private lateinit var expiryDate: LocalDateTime
    private lateinit var mfgDate: LocalDateTime

    private var selectedCategory: CategoryAndImage? = null
    private var selectedProduct: ProductAndImage? = null
    private val categoryList = ArrayList<Category>()
    private fun addToCategories(categories: ArrayList<Category>) {
        categoryList.addAll(categories)
    }

    fun setSelectedCategory(category: CategoryAndImage?) {
        selectedCategory = category
    }

    fun getSelectedCategory(): CategoryAndImage? {
        return selectedCategory
    }

    private val productList = ArrayList<Product>()
    fun setSelectedProduct(product: ProductAndImage?) {
        selectedProduct = product
    }

    fun getSelectedProduct(): ProductAndImage? {
        return selectedProduct
    }

    init {
        addToCategories(getDefaultCategories())
        productList.addAll(getFruits())
    }

    fun getImages(): ArrayList<Image> {
        val images = ArrayList<Image>()
        images.add(
            Image(
                1,
                "fruits",
                "fruits",
                "fruits" ,
                mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                2,
                "vegetable",
                "vegetables",
                "vegetables"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                3,
                "meat",
                "meat",
                "meat"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                4,
                "document",
                "document",
                "document"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                5,
                "subscription",
                "subscription",
                "subscription"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                6,
                "packed_food",
                "packed_food",
                "packed_food"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                7,
                "liquor",
                "liquor",
                "liquor"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                8,
                "drinks",
                "drinks",
                "drinks"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                9,
                "fast_food",
                "fast_food",
                "fast_food"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                10,
                "apple",
                "apple",
                "Apple"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                11,
                "banana",
                "banana",
                "Banana"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                12,
                "grapes",
                "grapes",
                "Grapes"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                13,
                "orange",
                "orange",
                "Orange"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                14,
                "pineapple",
                "pineapple",
                "Pineapple"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                15,
                "red_grapes",
                "red_grapes",
                "Red Grapes"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                16,
                "potato",
                "potato",
                "Potato"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                17,
                "peas",
                "peas",
                "Peas"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                18,
                "broccoli",
                "broccoli",
                "Broccoli"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                19,
                "bell_yellow_pepper",
                "bell_yellow_pepper",
                "bell yellow pepper"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                20,
                "tomato",
                "tomato",
                "Tomato"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )
        images.add(
            Image(
                21,
                "strawberries",
                "strawberries",
                "Strawberries"
                ,mimeType = "asset",
                uri = Uri.parse(""),
                bitmap = ""
            )
        )

        return images
    }

    fun getDefaultCategories(): ArrayList<Category> {
        val defaultCategories = ArrayList<Category>()

        val fruits = Category(
            1,
            "Fruit",
            1,
            false
        )
        val vegetables = Category(
            2,
            "Vegetable",
            2,
            false
        )
        val meat = Category(
            3,
            "Meat",
            3,
            false
        )
        val document = Category(
            4,
            "Document",
            4,
            false
        )
        val subscription = Category(
            5,
            "Subscription",
            5,
            false
        )
        val packedFood = Category(
            6,
            "Packed Food",
            6,
            false
        )
        val liquor = Category(
            7,
            "Liquor",
            7,
            false
        )
        val drinks = Category(
            8,
            "Drinkable",
            8,
            false
        )
        val fastFood = Category(
            9,
            "Fast Food",
            9,
            false
        )
        defaultCategories.add(fruits)
        defaultCategories.add(vegetables)
        defaultCategories.add(meat)
        defaultCategories.add(subscription)
        defaultCategories.add(liquor)
        defaultCategories.add(document)
        defaultCategories.add(drinks)
        defaultCategories.add(fastFood)
        defaultCategories.add(packedFood)

        return defaultCategories
    }

    fun getAllProducts(): ArrayList<Product> {
        val products = ArrayList<Product>()
        products.addAll(getFruits())
        products.addAll(getVegetables())

        return products
    }

    private fun getVegetables(): ArrayList<Product> {
        val products = ArrayList<Product>()
        val broccoli = Product(
            1,
            "Broccoli",
            2, 18,
            false
        )
        val potato = Product(
            2,
            "Potato",
            2, 16,
            false
        )
        val peas = Product(
            3,
            "Peas",
            2, 17 ,
            false
        )
        val bellPepper = Product(
            4,
            "Bell Pepper",
            2, 19,
            false
        )
        val tomato = Product(
            5,
            "Tomato",
            2, 20,
            false
        )


        products.add(broccoli)
        products.add(potato)
        products.add(bellPepper)
        products.add(peas)
        products.add(tomato)
        return products
    }

    private fun getFruits(): ArrayList<Product> {
        val products = ArrayList<Product>()

        val banana = Product(
            6,
            "Banana",
            1,
            11,
            false
        )
        val pineApple = Product(
            7,
            "Pineapple",
            1,
            14,
            false
        )
        val grapes = Product(
            8,
            "Grapes",
            1,
            12,
            false
        )
        val orange = Product(
            9,
            "Orange",
            1,
            13,
            false
        )
        val apple = Product(
            10,
            "Apple",
            1,
            10,
            false
        )
        val redGrapes = Product(
            11,
            "Red Grapes",
            1, 15,
            false
        )

        products.add(redGrapes)
        products.add(pineApple)
        products.add(apple)
        products.add(orange)
        products.add(grapes)
        products.add(banana)


        return products
    }

    fun setExpiryDate(date: LocalDateTime) {
        expiryDate = date
    }

    fun getExpiryDate(): LocalDateTime {
        return expiryDate
    }

    fun setMfgDate(date: LocalDateTime) {
        mfgDate = date
    }

    fun getMfgDate(): LocalDateTime {
        return mfgDate
    }
}