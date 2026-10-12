package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.CafeLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardBorderActive
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.StatusBilled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusNew
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CashierScreen(
    repository: CafeRepository,
    cafeId: String = "cafe_main",
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenCustomerMenu: () -> Unit,
    onOpenQrDialog: () -> Unit,
    onLogout: () -> Unit
) {
    val orders by repository.getOrders(cafeId).collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var selectedStageFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showMenuSettings by remember { mutableStateOf(false) }

    // Counts for each of the 5 pipeline stages
    val countNew = orders.count { it.status == OrderStatus.NEW }
    val countPreparing = orders.count { it.status == OrderStatus.PREPARING }
    val countDelivered = orders.count { it.status == OrderStatus.DELIVERED }
    val countBilled = orders.count { it.status == OrderStatus.BILLED }
    val countPaid = orders.count { it.status == OrderStatus.PAID }

    // Filter orders based on stage filter & search query
    val filteredOrders = orders.filter { order ->
        val matchesStage = selectedStageFilter == null || order.status == selectedStageFilter
        val matchesSearch = searchQuery.isBlank() ||
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.tableNumber.toString().contains(searchQuery) ||
                order.items.any { it.nameAr.contains(searchQuery) || it.nameEn.contains(searchQuery, ignoreCase = true) }
        matchesStage && matchesSearch
    }

    val isArabic = language == AppLanguage.ARABIC
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

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
                // Header (matches screenshot #1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CafeLogo(iconSize = 44.dp, showText = true, titleSize = 18)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isArabic) "كاشير المطعم" else "Restaurant Cashier",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "إدارة طلبات العملاء" else "Customer Orders Management",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // QR Generator button
                        IconButton(
                            onClick = onOpenQrDialog,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CardDark)
                                .border(1.dp, CardBorder, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = "QR Codes",
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Settings / Menu dropdown
                        Box {
                            IconButton(
                                onClick = { showMenuSettings = !showMenuSettings },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CardDark)
                                    .border(1.dp, CyanPrimary.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenuSettings,
                                onDismissRequest = { showMenuSettings = false },
                                modifier = Modifier
                                    .background(CardDark)
                                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (isArabic) "فتح قائمة الزبون (QR)" else "Open Customer Menu",
                                            color = TextPrimary
                                        )
                                    },
                                    onClick = {
                                        showMenuSettings = false
                                        onOpenCustomerMenu()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (isArabic) "تغيير اللغة (English)" else "Switch to العربية",
                                            color = CyanPrimary
                                        )
                                    },
                                    onClick = {
                                        showMenuSettings = false
                                        onLanguageChange(if (isArabic) AppLanguage.ENGLISH else AppLanguage.ARABIC)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (isArabic) "تسجيل الخروج" else "Sign Out",
                                            color = StatusNew
                                        )
                                    },
                                    onClick = {
                                        showMenuSettings = false
                                        onLogout()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Five Stages Pipeline Tabs (matches screenshot #1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StageTabItem(
                        stageNumber = "01",
                        title = if (isArabic) "طلبات جديدة" else "New Orders",
                        count = countNew,
                        icon = Icons.Default.Description,
                        badgeColor = StatusNew,
                        isSelected = selectedStageFilter == OrderStatus.NEW,
                        onClick = {
                            selectedStageFilter = if (selectedStageFilter == OrderStatus.NEW) null else OrderStatus.NEW
                        }
                    )

                    PipelineArrow()

                    StageTabItem(
                        stageNumber = "02",
                        title = if (isArabic) "قيد التحضير" else "Preparing",
                        count = countPreparing,
                        icon = Icons.Default.SoupKitchen,
                        badgeColor = StatusPreparing,
                        isSelected = selectedStageFilter == OrderStatus.PREPARING,
                        onClick = {
                            selectedStageFilter = if (selectedStageFilter == OrderStatus.PREPARING) null else OrderStatus.PREPARING
                        }
                    )

                    PipelineArrow()

                    StageTabItem(
                        stageNumber = "03",
                        title = if (isArabic) "تم التسليم" else "Delivered",
                        count = countDelivered,
                        icon = Icons.Default.Check,
                        badgeColor = StatusDelivered,
                        isSelected = selectedStageFilter == OrderStatus.DELIVERED,
                        onClick = {
                            selectedStageFilter = if (selectedStageFilter == OrderStatus.DELIVERED) null else OrderStatus.DELIVERED
                        }
                    )

                    PipelineArrow()

                    StageTabItem(
                        stageNumber = "04",
                        title = if (isArabic) "الفاتورة" else "Billed",
                        count = countBilled,
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        badgeColor = StatusBilled,
                        isSelected = selectedStageFilter == OrderStatus.BILLED,
                        onClick = {
                            selectedStageFilter = if (selectedStageFilter == OrderStatus.BILLED) null else OrderStatus.BILLED
                        }
                    )

                    PipelineArrow()

                    StageTabItem(
                        stageNumber = "05",
                        title = if (isArabic) "التسديد" else "Paid",
                        count = countPaid,
                        icon = Icons.Default.CreditCard,
                        badgeColor = StatusPaid,
                        isSelected = selectedStageFilter == OrderStatus.PAID,
                        onClick = {
                            selectedStageFilter = if (selectedStageFilter == OrderStatus.PAID) null else OrderStatus.PAID
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search & Filter Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isArabic) "البحث عن طلب رقم ..." else "Search by order number ...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = CyanPrimary)
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = CardDark,
                            unfocusedContainerColor = CardDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("cashier_search_input")
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // "All / الكل" Filter Button
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDark)
                            .border(
                                1.dp,
                                if (selectedStageFilter == null) CyanPrimary else CardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedStageFilter = null }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "الكل" else "All",
                                color = if (selectedStageFilter == null) CyanPrimary else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Orders List
                if (filteredOrders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isArabic) "لا توجد طلبات في هذه المرحلة" else "No orders in this stage",
                                color = TextMuted,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredOrders, key = { it.id }) { order ->
                            OrderCardItem(
                                order = order,
                                isArabic = isArabic,
                                onAdvanceStatus = { nextStatus ->
                                    coroutineScope.launch {
                                        repository.updateOrderStatus(order.cafeId, order.id, nextStatus)
                                    }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StageTabItem(
    stageNumber: String,
    title: String,
    count: Int,
    icon: ImageVector,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF0C3840) else CardDark
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) CyanPrimary else CardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(78.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Count Badge in top right/left
                if (count > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = count.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stage Icon
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) CyanPrimary else TextSecondary,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stageNumber,
                color = if (isSelected) CyanPrimary else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PipelineArrow() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = null,
        tint = Color(0xFF1B4E58),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(14.dp)
    )
}

@Composable
fun OrderCardItem(
    order: Order,
    isArabic: Boolean,
    onAdvanceStatus: (OrderStatus) -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedTime = remember(order.timestamp) { timeFormat.format(Date(order.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Status Badge, Order Number, Table Number
            Column(
                modifier = Modifier.width(76.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Status pill
                val (tagColor, tagText) = when (order.status) {
                    OrderStatus.NEW -> StatusNew to if (isArabic) "جديد" else "New"
                    OrderStatus.PREPARING -> StatusPreparing to if (isArabic) "قيد التحضير" else "Prep"
                    OrderStatus.DELIVERED -> StatusDelivered to if (isArabic) "تم التسليم" else "Done"
                    OrderStatus.BILLED -> StatusBilled to if (isArabic) "فاتورة" else "Bill"
                    OrderStatus.PAID -> StatusPaid to if (isArabic) "تسديد" else "Paid"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(tagColor.copy(alpha = 0.2f))
                        .border(1.dp, tagColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tagText,
                        color = tagColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = order.orderNumber,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.TableBar,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isArabic) "طاولة ${order.tableNumber}" else "Table ${order.tableNumber}",
                        color = CyanPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Thumbnail (food photo)
            val firstItem = order.items.firstOrNull()
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF071C21))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=150&auto=format&fit=crop",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Center Column: Time & Items Breakdown
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedTime,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "${order.items.size} عناصر" else "${order.items.size} items",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                order.items.take(3).forEach { item ->
                    Text(
                        text = "${item.quantity} ${if (isArabic) item.nameAr else item.nameEn}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                if (order.items.size > 3) {
                    Text(
                        text = "+ ${order.items.size - 3} المزيد",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Column: Stage Progression Action Button (matches screenshot #1)
            when (order.status) {
                OrderStatus.NEW -> {
                    Button(
                        onClick = { onAdvanceStatus(OrderStatus.PREPARING) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF008E82),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("action_start_prep_${order.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SoupKitchen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "بدء التحضير ->" else "Start Prep ->",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                OrderStatus.PREPARING -> {
                    OutlinedButton(
                        onClick = { onAdvanceStatus(OrderStatus.DELIVERED) },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, StatusPreparing),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusPreparing),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("action_deliver_${order.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "قيد التحضير" else "In Prep",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                OrderStatus.DELIVERED -> {
                    OutlinedButton(
                        onClick = { onAdvanceStatus(OrderStatus.BILLED) },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, StatusDelivered),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDelivered),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("action_bill_${order.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "تم التسليم" else "Delivered",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                OrderStatus.BILLED -> {
                    OutlinedButton(
                        onClick = { onAdvanceStatus(OrderStatus.PAID) },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, StatusBilled),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusBilled),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("action_pay_${order.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "عرض الفاتورة ->" else "Invoice ->",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                OrderStatus.PAID -> {
                    OutlinedButton(
                        onClick = { /* Already paid */ },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StatusPaid),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusPaid),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "تم التسديد ✓" else "Paid ✓",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
