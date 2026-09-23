package com.kb.watchlist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kb.watchlist.ui.components.MetricCard
import com.kb.watchlist.ui.theme.*
import com.kb.watchlist.util.Formatters
import com.kb.watchlist.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    stockCode: String,
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(stockCode) {
        viewModel.loadQuote(stockCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "종목 상세",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Typography.titleLarge.fontFamily
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = GoldAccent
                    )
                }

                uiState.quote != null -> {
                    val quote = uiState.quote!!
                    val sign = quote.safeSign
                    val priceColor = when (sign) {
                        "+" -> StockRiseRed
                        "-" -> StockFallBlue
                        else -> TextWhite
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Top Section: Large Stock Name, Code, Price, and Rate
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = NavyCard)
                            ) {
                                Column(
                                    modifier = Modifier.padding(22.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = quote.name?.ifEmpty { stockCode } ?: stockCode,
                                            color = TextWhite,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Typography.headlineMedium.fontFamily
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(NavyCardElevated)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = quote.code.ifEmpty { stockCode },
                                                color = GoldAccent,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Large Current Price
                                    Text(
                                        text = Formatters.formatPrice(quote.currentPrice),
                                        color = TextWhite,
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = Typography.headlineLarge.fontFamily
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Change amount and Rate
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = Formatters.formatChange(quote.change, sign),
                                            color = priceColor,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = Typography.titleMedium.fontFamily
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "(${Formatters.formatChangeRate(quote.changeRate, sign)})",
                                            color = priceColor,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Typography.titleMedium.fontFamily
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // 2x2 Grid Section (시가, 고가, 저가, 거래량)
                            Text(
                                text = "주요 시세 정보",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Typography.titleMedium.fontFamily
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Row 1: 시가 & 고가
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricCard(
                                    label = "시가",
                                    value = Formatters.formatPrice(quote.openPrice),
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    label = "고가",
                                    value = Formatters.formatPrice(quote.highPrice),
                                    modifier = Modifier.weight(1f),
                                    valueColor = StockRiseRed
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Row 2: 저가 & 거래량
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MetricCard(
                                    label = "저가",
                                    value = Formatters.formatPrice(quote.lowPrice),
                                    modifier = Modifier.weight(1f),
                                    valueColor = StockFallBlue
                                )
                                MetricCard(
                                    label = "거래량",
                                    value = Formatters.formatVolume(quote.volume),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Bottom Section: Remove from Watchlist Button
                        Button(
                            onClick = {
                                viewModel.removeFromWatchlist(onSuccess = onNavigateBack)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyCardElevated,
                                contentColor = TextMuted
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder),
                            enabled = !uiState.isRemoving
                        ) {
                            if (uiState.isRemoving) {
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "관심종목에서 제거",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = Typography.titleMedium.fontFamily
                                )
                            }
                        }
                    }
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "시세 정보를 불러올 수 없습니다",
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadQuote(stockCode) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = NavyDeep)
                        ) {
                            Text(text = "다시 시도", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
