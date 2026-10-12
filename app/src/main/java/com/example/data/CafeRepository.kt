package com.example.data

import android.util.Log
import com.example.model.Cafe
import com.example.model.MenuItem
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CafeRepository {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("CafeRepository", "FirebaseAuth init error", e)
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("CafeRepository", "FirebaseFirestore init error", e)
            null
        }
    }

    // Default sample items matching the UI screenshots
    private val initialSampleMenuItems = listOf(
        MenuItem(
            id = "item_1",
            cafeId = "cafe_main",
            nameAr = "برجر كلاسيك",
            nameEn = "Classic Burger",
            descriptionAr = "شريحة لحم بقري مشوية مع الجبن والخضار الطازجة",
            descriptionEn = "Grilled beef patty with melted cheese and fresh veggies",
            price = 28.0,
            categoryAr = "برجر",
            categoryEn = "Burgers",
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_2",
            cafeId = "cafe_main",
            nameAr = "قطع دجاج مقرمش",
            nameEn = "Crispy Chicken Tenders",
            descriptionAr = "قطع دجاج متبلة ومقلية مقرمشة تقدم مع الصوص الخاص",
            descriptionEn = "Crispy golden fried chicken tenders with signature dip",
            price = 24.0,
            categoryAr = "برجر",
            categoryEn = "Burgers",
            imageUrl = "https://images.unsplash.com/photo-1562967914-608f82629710?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_3",
            cafeId = "cafe_main",
            nameAr = "باستا ألفريدو",
            nameEn = "Alfredo Pasta",
            descriptionAr = "مكرونة فيتوتشيني مع صوص الكريمة والدجاج والمشروم",
            descriptionEn = "Fettuccine pasta in rich creamy parmesan mushroom sauce",
            price = 32.0,
            categoryAr = "أطباق رئيسية",
            categoryEn = "Main Dishes",
            imageUrl = "https://images.unsplash.com/photo-1645112411341-6c4fd023714a?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_4",
            cafeId = "cafe_main",
            nameAr = "بيتزا مارغريتا",
            nameEn = "Margherita Pizza",
            descriptionAr = "عجينة إيطالية هشة مع صوص الطماطم وجبن الموزاريلا",
            descriptionEn = "Classic crust with San Marzano tomatoes and mozzarella",
            price = 35.0,
            categoryAr = "أطباق رئيسية",
            categoryEn = "Main Dishes",
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_5",
            cafeId = "cafe_main",
            nameAr = "قهوة لاتيه",
            nameEn = "Caffe Latte",
            descriptionAr = "إسبريسو غني مع حليب مبخر ورغوة ناعمة وفن اللاتيه",
            descriptionEn = "Rich espresso with steamed velvety milk and latte art",
            price = 16.0,
            categoryAr = "قهوة ومشروبات",
            categoryEn = "Coffee & Drinks",
            imageUrl = "https://images.unsplash.com/photo-1541167760496-1628856ab772?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_6",
            cafeId = "cafe_main",
            nameAr = "كيك شوكولاتة",
            nameEn = "Chocolate Cake",
            descriptionAr = "كيك بلجيكي فاخر محشو بالشوكولاتة الداكنة المذابة",
            descriptionEn = "Decadent Belgian molten dark chocolate fudge cake",
            price = 22.0,
            categoryAr = "حلويات",
            categoryEn = "Desserts",
            imageUrl = "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_7",
            cafeId = "cafe_main",
            nameAr = "بطاطا مقلية",
            nameEn = "French Fries",
            descriptionAr = "أصابع بطاطس مقرمشة متبلة بالأعشاب والملح البحري",
            descriptionEn = "Crispy golden french fries with rosemary seasoning",
            price = 10.0,
            categoryAr = "مقبلات",
            categoryEn = "Sides",
            imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=400&auto=format&fit=crop"
        ),
        MenuItem(
            id = "item_8",
            cafeId = "cafe_main",
            nameAr = "عصير برتقال طازج",
            nameEn = "Fresh Orange Juice",
            descriptionAr = "عصير برتقال طبيعي 100% معصور طازجاً بدون سكر مضاف",
            descriptionEn = "100% freshly squeezed Valencia orange juice",
            price = 14.0,
            categoryAr = "قهوة ومشروبات",
            categoryEn = "Coffee & Drinks",
            imageUrl = "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400&auto=format&fit=crop"
        )
    )

    // Initial sample orders matching screenshot #1
    private val localOrders = MutableStateFlow(
        listOf(
            Order(
                id = "order_1025",
                orderNumber = "#1025",
                cafeId = "cafe_main",
                tableNumber = 5,
                status = OrderStatus.NEW,
                items = listOf(
                    OrderItem("1", "برجر كلاسيك", "Classic Burger", 1, 28.0),
                    OrderItem("7", "بطاطا مقلية", "French Fries", 1, 10.0),
                    OrderItem("9", "بيبسي", "Pepsi", 1, 5.0)
                ),
                totalAmount = 43.0,
                timestamp = System.currentTimeMillis() - (15 * 60 * 1000)
            ),
            Order(
                id = "order_1026",
                orderNumber = "#1026",
                cafeId = "cafe_main",
                tableNumber = 7,
                status = OrderStatus.NEW,
                items = listOf(
                    OrderItem("2", "قطع دجاج مقرمش", "Crispy Chicken Tenders", 1, 24.0),
                    OrderItem("7", "بطاطا مقلية", "French Fries", 1, 10.0)
                ),
                totalAmount = 34.0,
                timestamp = System.currentTimeMillis() - (12 * 60 * 1000)
            ),
            Order(
                id = "order_1027",
                orderNumber = "#1027",
                cafeId = "cafe_main",
                tableNumber = 3,
                status = OrderStatus.PREPARING,
                items = listOf(
                    OrderItem("3", "باستا ألفريدو", "Alfredo Pasta", 1, 32.0),
                    OrderItem("1", "برجر دجاج", "Chicken Burger", 1, 26.0),
                    OrderItem("8", "عصير برتقال", "Orange Juice", 1, 14.0),
                    OrderItem("10", "سلطة خضراء", "Green Salad", 1, 12.0)
                ),
                totalAmount = 84.0,
                timestamp = System.currentTimeMillis() - (25 * 60 * 1000)
            ),
            Order(
                id = "order_1028",
                orderNumber = "#1028",
                cafeId = "cafe_main",
                tableNumber = 9,
                status = OrderStatus.DELIVERED,
                items = listOf(
                    OrderItem("1", "برجر لحم", "Beef Burger", 1, 30.0),
                    OrderItem("9", "كولا", "Cola", 1, 5.0)
                ),
                totalAmount = 35.0,
                timestamp = System.currentTimeMillis() - (40 * 60 * 1000)
            ),
            Order(
                id = "order_1029",
                orderNumber = "#1029",
                cafeId = "cafe_main",
                tableNumber = 4,
                status = OrderStatus.BILLED,
                items = listOf(
                    OrderItem("5", "قهوة لاتيه", "Caffe Latte", 1, 16.0),
                    OrderItem("6", "كيك شوكولاتة", "Chocolate Cake", 1, 22.0),
                    OrderItem("11", "ماء", "Water", 1, 3.0)
                ),
                totalAmount = 41.0,
                timestamp = System.currentTimeMillis() - (55 * 60 * 1000)
            ),
            Order(
                id = "order_1031",
                orderNumber = "#1031",
                cafeId = "cafe_main",
                tableNumber = 6,
                status = OrderStatus.PAID,
                items = listOf(
                    OrderItem("4", "برجر مشوي", "Grilled Burger", 1, 28.0),
                    OrderItem("12", "عصير تفاح", "Apple Juice", 1, 12.0)
                ),
                totalAmount = 40.0,
                timestamp = System.currentTimeMillis() - (70 * 60 * 1000)
            )
        )
    )

    private val currentUserProfile = MutableStateFlow<UserProfile?>(
        UserProfile(
            uid = "cashier_1",
            email = "cashier@cafe.com",
            fullName = "كاشير المطعم",
            role = "cashier",
            cafeId = "cafe_main"
        )
    )

    val currentUser = currentUserProfile.asStateFlow()

    fun getMenuItems(cafeId: String): Flow<List<MenuItem>> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            trySend(initialSampleMenuItems.filter { it.cafeId == cafeId || cafeId.isEmpty() })
            awaitClose { }
            return@callbackFlow
        }

        // Send local sample first for instant display
        trySend(initialSampleMenuItems.filter { it.cafeId == cafeId || cafeId.isEmpty() })

        val listener = fs.collection("cafes").document(cafeId).collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("CafeRepository", "Listen failed", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MenuItem::class.java)?.copy(id = doc.id)
                    }
                    trySend(items)
                }
            }

        awaitClose { listener.remove() }
    }

    fun getOrders(cafeId: String): Flow<List<Order>> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            val job = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).run {
                localOrders.collect { list ->
                    trySend(list.filter { it.cafeId == cafeId || cafeId.isEmpty() })
                }
            }
            awaitClose { }
            return@callbackFlow
        }

        // Emit current local list first
        trySend(localOrders.value.filter { it.cafeId == cafeId || cafeId.isEmpty() })

        val listener = fs.collection("cafes").document(cafeId).collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("CafeRepository", "Orders listen failed", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val ordersList = snapshot.documents.mapNotNull { doc ->
                        val code = doc.getString("status") ?: "new"
                        val orderStatus = OrderStatus.fromCode(code)
                        Order(
                            id = doc.id,
                            orderNumber = doc.getString("orderNumber") ?: "#${doc.id.takeLast(4)}",
                            cafeId = doc.getString("cafeId") ?: cafeId,
                            tableNumber = (doc.getLong("tableNumber") ?: 1L).toInt(),
                            status = orderStatus,
                            totalAmount = doc.getDouble("totalAmount") ?: 0.0,
                            paymentMethod = doc.getString("paymentMethod") ?: "cash",
                            customerNotes = doc.getString("customerNotes") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }
                    trySend(ordersList)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateOrderStatus(cafeId: String, orderId: String, nextStatus: OrderStatus): Result<Unit> {
        // Update local memory state immediately for instant responsive UI
        val currentList = localOrders.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val updated = currentList[index].copy(status = nextStatus)
            currentList[index] = updated
            localOrders.value = currentList
        }

        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("cafes").document(cafeId).collection("orders")
                    .document(orderId)
                    .update("status", nextStatus.code)
                    .await()
            } catch (e: Exception) {
                Log.w("CafeRepository", "Firestore update status failed, kept local", e)
            }
        }
        return Result.success(Unit)
    }

    suspend fun placeOrder(order: Order): Result<String> {
        val currentList = localOrders.value.toMutableList()
        val newOrder = order.copy(
            id = "order_${System.currentTimeMillis()}",
            orderNumber = "#${(1030 + (currentList.size)).toString()}"
        )
        currentList.add(0, newOrder)
        localOrders.value = currentList

        val fs = firestore
        if (fs != null) {
            try {
                val data = hashMapOf(
                    "orderNumber" to newOrder.orderNumber,
                    "cafeId" to newOrder.cafeId,
                    "tableNumber" to newOrder.tableNumber,
                    "status" to newOrder.status.code,
                    "totalAmount" to newOrder.totalAmount,
                    "paymentMethod" to "cash",
                    "customerNotes" to newOrder.customerNotes,
                    "timestamp" to newOrder.timestamp,
                    "customerSessionId" to newOrder.customerSessionId
                )
                fs.collection("cafes").document(newOrder.cafeId).collection("orders")
                    .document(newOrder.id)
                    .set(data)
                    .await()
            } catch (e: Exception) {
                Log.w("CafeRepository", "Firestore place order failed", e)
            }
        }
        return Result.success(newOrder.orderNumber)
    }

    suspend fun login(email: String, pass: String): Result<UserProfile> {
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                val res = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                val uid = res.user?.uid ?: "user_1"
                val profile = UserProfile(
                    uid = uid,
                    email = email,
                    fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = "cashier",
                    cafeId = "cafe_main"
                )
                currentUserProfile.value = profile
                return Result.success(profile)
            } catch (e: Exception) {
                Log.w("CafeRepository", "Firebase auth sign-in error, using local fallback", e)
            }
        }
        // Local simulation fallback
        val profile = UserProfile(
            uid = "usr_${System.currentTimeMillis()}",
            email = email,
            fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            role = "cashier",
            cafeId = "cafe_main"
        )
        currentUserProfile.value = profile
        return Result.success(profile)
    }

    suspend fun register(name: String, email: String, pass: String): Result<UserProfile> {
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                val res = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                val uid = res.user?.uid ?: "user_${System.currentTimeMillis()}"
                val profile = UserProfile(
                    uid = uid,
                    email = email,
                    fullName = name,
                    role = "cashier",
                    cafeId = "cafe_main"
                )
                currentUserProfile.value = profile
                return Result.success(profile)
            } catch (e: Exception) {
                Log.w("CafeRepository", "Firebase auth register error", e)
            }
        }
        val profile = UserProfile(
            uid = "usr_${System.currentTimeMillis()}",
            email = email,
            fullName = name,
            role = "cashier",
            cafeId = "cafe_main"
        )
        currentUserProfile.value = profile
        return Result.success(profile)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val firebaseAuth = auth
        if (firebaseAuth != null) {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
            } catch (e: Exception) {
                Log.w("CafeRepository", "Firebase password reset error", e)
            }
        }
        return Result.success(Unit)
    }

    fun logout() {
        auth?.signOut()
        currentUserProfile.value = null
    }
}
