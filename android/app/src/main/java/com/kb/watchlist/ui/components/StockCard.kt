package com.kb.watchlist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kb.watchlist.model.StockQuote
import com.kb.watchlist.model.WatchlistItem
import com.kb.watchlist.ui.theme.*
import com.kb.watchlist.util.Formatters

@Composable
fun StockCard(
    item: WatchlistItem,
    quote: StockQuote?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Stock Name & Code
            Column(modifier = Modifier.weight(1f)) {
                val displayName = quote?.name?.takeIf { it.isNotEmpty() } ?: item.name
                Text(
                    text = displayName,
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Typography.titleMedium.fontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.code,
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = Typography.bodyMedium.fontFamily
                )
            }

            // Right: Current Price & Change Rate
            Column(horizontalAlignment = Alignment.End) {
                if (quote != null && quote.currentPrice > 0L) {
                    val sign = quote.safeSign
                    val priceColor = when (sign) {
                        "+" -> StockRiseRed
                        "-" -> StockFallBlue
                        else -> TextWhite
                    }

                    val badgeBg = when (sign) {
                        "+" -> StockRiseRed.copy(alpha = 0.15f)
                        "-" -> StockFallBlue.copy(alpha = 0.15f)
                        else -> NavyCardElevated
                    }

                    Text(
                        text = Formatters.formatPrice(quote.currentPrice),
                        color = TextWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Typography.titleMedium.fontFamily
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = Formatters.formatChangeRate(quote.changeRate, sign),
                            color = priceColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            fontFamily = Typography.labelMedium.fontFamily
                        )
                    }
                } else {
                    Text(
                        text = "--",
                        color = TextMuted,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "시세 대기 중",
                        color = TextSubtle,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
