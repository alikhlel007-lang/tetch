package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CafeRepository
import com.example.model.AppLanguage
import com.example.model.MenuItem
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.ui.components.CafeLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun CustomerMenuScreen(
    repository: CafeRepository,
    cafeId: String = "cafe_main",
    initialTableNumber: Int = 5,
    language: AppLanguage,
    onLanguageToggle: () -> Unit,
    onBackToCashier: () -> Unit
) {
    val menuItems by repository.getMenuItems(cafeId).collectAsState(initial = emptyList())
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var tableNumber by remember { mutableIntStateOf(initialTableNumber) }
    val cartQuantities = remember { mutableStateMapOf<String, Int>() }

    var isSubmitting by remember { mutableStateOf(false) }
    var submittedOrderNumber by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val isArabic = language == AppLanguage.ARABIC
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val categories = remember(menuItems, isArabic) {
        val cats = menuItems.map { if (isArabic) it.categoryAr else it.categoryEn }.distinct()
        listOf(if (isArabic) "الكل" else "All") + cats
    }

    val filteredItems = remember(menuItems, selectedCategory, isArabic) {
        if (selectedCategory == null || selectedCategory == (if (isArabic) "الكل" else "All")) {
            menuItems
        } else {
            menuItems.filter { (if (isArabic) it.categoryAr else it.categoryEn) == selectedCategory }
        }
    }

    // Cart calculations
    val totalItemsCount = cartQuantities.values.sum()
    val totalAmount = cartQuantities.entries.sumOf { (itemId, qty) ->
        val item = menuItems.find { it.id == itemId }
        (item?.price ?: 0.0) * qty
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF031619),
                            BackgroundDark,
                            Color(0xFF020E10)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackToCashier,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CardDark)
                                .border(1.dp, CardBorder, CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        CafeLogo(iconSize = 38.dp, showText = true, titleSize = 16)
                    }

                    // Table Badge + Language Toggle
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF08272E))
                                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.TableBar,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "طاولة $tableNumber" else "Table $tableNumber",
                                    color = CyanPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = onLanguageToggle,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CardDark)
                        ) {
                            Text(
                                text = if (isArabic) "EN" else "عربي",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Subtitle / QR notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "قائمة الطعام الرقمية (مسح QR)" else "Digital QR Menu",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isArabic) "الدفع: نقدي عند الكاشير" else "Payment: Cash Only",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Categories Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (selectedCategory == null && cat == (if (isArabic) "الكل" else "All")) || selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CyanPrimary else CardDark)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanGlow else CardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    selectedCategory = if (cat == (if (isArabic) "الكل" else "All")) null else cat
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFF031619) else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Menu items list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        val qty = cartQuantities[item.id] ?: 0
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = CardDark),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Food image
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF071C21))
                                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                ) {
                                    AsyncImage(
                                        model = item.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200&auto=format&fit=crop" },
                                        contentDescription = item.nameEn,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Item details
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isArabic) item.nameAr else item.nameEn,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isArabic) item.descriptionAr else item.descriptionEn,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        lineHeight = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${item.price} ${if (isArabic) "ر.س" else "SAR"}",
                                        color = GoldAccent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Quantity +/- controls
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF071E23))
                                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                        .padding(4.dp)
                                ) {
                                    if (qty > 0) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(CardDark)
                                                .clickable {
                                                    if (qty == 1) cartQuantities.remove(item.id)
                                                    else cartQuantities[item.id] = qty - 1
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                                        }

                                        Text(
                                            text = qty.toString(),
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(CyanPrimary)
                                            .clickable {
                                                cartQuantities[item.id] = qty + 1
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF031619), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // Bottom Cart Sticky Bar
            AnimatedVisibility(
                visible = totalItemsCount > 0,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF07242B))
                        .border(1.5.dp, CyanPrimary, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "$totalItemsCount عناصر" else "$totalItemsCount items",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "$totalAmount ${if (isArabic) "ر.س" else "SAR"}",
                                color = GoldAccent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Button(
                            onClick = {
                                isSubmitting = true
                                coroutineScope.launch {
                                    val orderItems = cartQuantities.mapNotNull { (itemId, qty) ->
                                        menuItems.find { it.id == itemId }?.let { item ->
                                            OrderItem(
                                                itemId = item.id,
                                                nameAr = item.nameAr,
                                                nameEn = item.nameEn,
                                                quantity = qty,
                                                price = item.price
                                            )
                                        }
                                    }
                                    val order = Order(
                                        cafeId = cafeId,
                                        tableNumber = tableNumber,
                                        items = orderItems,
                                        totalAmount = totalAmount,
                                        paymentMethod = "cash",
                                        status = OrderStatus.NEW,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    val res = repository.placeOrder(order)
                                    isSubmitting = false
                                    if (res.isSuccess) {
                                        submittedOrderNumber = res.getOrNull() ?: "#1032"
                                        cartQuantities.clear()
                                    }
                                }
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = Color(0xFF031619)
                            ),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("submit_order_button")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color(0xFF031619), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "إرسال للكاشير (نقدي)" else "Send Order (Cash)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Order Success Dialog
            submittedOrderNumber?.let { orderNum ->
                AlertDialog(
                    onDismissRequest = { submittedOrderNumber = null },
                    containerColor = CardDark,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusDelivered)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تم إرسال الطلب بنجاح!" else "Order Placed Successfully!",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = if (isArabic) "رقم الطلب الخاص بك: $orderNum" else "Your Order Number: $orderNum",
                                color = CyanPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isArabic)
                                    "تم استلام طلبك في كاشير المطعم (طاولة $tableNumber). يرجى التوجه لتسديد المبلغ نقداً عند تجهيز الفاتورة."
                                else
                                    "Your order is received by the cashier (Table $tableNumber). Please pay in cash when the invoice is ready.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { submittedOrderNumber = null },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF031619))
                        ) {
                            Text(if (isArabic) "حسناً" else "OK")
                        }
                    }
                )
            }
        }
    }
}
