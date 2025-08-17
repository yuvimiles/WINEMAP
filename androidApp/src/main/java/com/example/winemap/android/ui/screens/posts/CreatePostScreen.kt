package com.example.winemap.android.ui.screens.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winemap.android.ui.components.ImagePicker
import com.example.winemap.android.ui.components.WinerySelector
import com.example.winemap.android.ui.components.StarRating
import com.example.winemap.presentation.posts.PostViewModel

@Composable
fun CreatePostScreen(
    postViewModel: PostViewModel,
    onNavigateBack: () -> Unit = {},
    onPostCreated: () -> Unit = {}
) {
    val uiState by postViewModel.uiState.collectAsState()
    var selectedWinery by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var selectedImagePath by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Same background
        Image(
            painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.9f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8DCC6))
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center)
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

            // Create new post title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create new post",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Create form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Add Image section - exactly like mockup
                ImagePicker(
                    onImageSelected = { imagePath ->
                        selectedImagePath = imagePath
                    },
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Star rating
                StarRating(
                    rating = rating,
                    maxRating = 5,
                    size = 24.dp,
                    isInteractive = true,
                    onRatingChanged = { newRating ->
                        rating = newRating
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Choose winery field - exactly like mockup
                WinerySelector(
                    selectedWinery = selectedWinery,
                    wineryList = emptyList(), // Will be populated from ViewModel
                    onWinerySelected = { winery ->
                        selectedWinery = winery
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content field - large text area like mockup
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "content",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.weight(1f))

                // Post button - exactly like mockup
                Button(
                    onClick = {
                        // Create post with postViewModel
                        // postViewModel.createPost(selectedWinery, content, rating, selectedImagePath)
                        onPostCreated()
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B6F47)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    enabled = selectedWinery.isNotEmpty() && content.isNotEmpty() && rating > 0
                ) {
                    Text(
                        text = "post",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}