package com.example.model

enum class OrderStatus(val code: String, val titleAr: String, val titleEn: String) {
    NEW("new", "طلبات جديدة", "New Orders"),
    PREPARING("preparing", "قيد التحضير", "Preparing"),
    DELIVERED("delivered", "تم التسليم", "Delivered"),
    BILLED("billed", "الفاتورة", "Billed"),
    PAID("paid", "التسديد", "Paid");

    companion object {
        fun fromCode(code: String): OrderStatus =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: NEW
    }
}

data class Cafe(
    val id: String = "cafe_main",
    val nameAr: String = "كافيه الأصالة",
    val nameEn: String = "CAFE Original",
    val logoUrl: String = "",
    val phone: String = "+966 50 123 4567",
    val tablesCount: Int = 20,
    val currencyAr: String = "ر.س",
    val currencyEn: String = "SAR"
)

data class MenuItem(
    val id: String = "",
    val cafeId: String = "cafe_main",
    val nameAr: String = "",
    val nameEn: String = "",
    val descriptionAr: String = "",
    val descriptionEn: String = "",
    val price: Double = 0.0,
    val categoryAr: String = "مشروبات",
    val categoryEn: String = "Drinks",
    val imageUrl: String = "",
    val isAvailable: Boolean = true
)

data class OrderItem(
    val itemId: String = "",
    val nameAr: String = "",
    val nameEn: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0,
    val notes: String = ""
) {
    val totalPrice: Double get() = price * quantity
}

data class Order(
    val id: String = "",
    val orderNumber: String = "#1001",
    val cafeId: String = "cafe_main",
    val tableNumber: Int = 1,
    val status: OrderStatus = OrderStatus.NEW,
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "cash", // دائماً نقدي
    val customerNotes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val customerSessionId: String = ""
)

enum class AppLanguage {
    ARABIC, ENGLISH
}

enum class AuthMode {
    LOGIN, REGISTER, FORGOT_PASSWORD
}

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val role: String = "cashier",
    val cafeId: String = "cafe_main"
)
