package com.example.winemap.android.ui.screens.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winemap.android.ui.components.StarRating
import com.example.winemap.presentation.posts.PostViewModel

@Composable
fun MapScreen(
    postViewModel: PostViewModel,
    wineries: List<Winery> = emptyList(),
    onWinerySelected: (Winery) -> Unit = {},
    onRelatedPostsClick: (String) -> Unit = {}
) {
    val uiState by postViewModel.uiState.collectAsState()
    var selectedWinery by remember { mutableStateOf<Winery?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top header with logo - exactly like mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8DCC6))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                        contentDescription = "Winemap Logo",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WINEMAP",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // Map area - placeholder for Google Maps integration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE0E0E0))
            ) {
                // Google Maps will be integrated here
                // GoogleMap component will show wineries as markers
                // Each marker click will call: selectedWinery = clickedWinery

                // Placeholder content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🗺️ Interactive Map",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Google Maps will show ${wineries.size} wineries",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Red markers for each winery location",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Selected winery card at bottom - exactly like mockup
        selectedWinery?.let { winery ->
            WineryCard(
                winery = winery,
                onRelatedPostsClick = { onRelatedPostsClick(winery.id) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun WineryCard(
    winery: Winery,
    onRelatedPostsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Winery image
            Image(
                painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                contentDescription = "Winery Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = winery.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = winery.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Star rating
                StarRating(
                    rating = winery.rating.toInt(),
                    size = 16.dp
                )
            }

            // Related posts button
            Button(
                onClick = onRelatedPostsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B6F47)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Related posts",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Data class for Winery
data class Winery(
    val id: String,
    val name: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val imageUrl: String = ""
)