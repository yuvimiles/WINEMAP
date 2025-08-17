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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.winemap.android.ui.components.StarRating
import com.example.winemap.presentation.posts.PostViewModel

@Composable
fun EditPostScreen(
    postViewModel: PostViewModel,
    postId: String = "",
    onNavigateBack: () -> Unit = {},
    onPostUpdated: () -> Unit = {},
    onPostDeleted: () -> Unit = {}
) {
    val uiState by postViewModel.uiState.collectAsState()
    var content by remember { mutableStateOf("היה לנו היום כיף מטורף מגזרים\nהיה טעים למגרי רחמא אחרת,\nהיו טעים למגרי רחמא שלנו\nמגזרים שנו המומחות שלנו") }
    var rating by remember { mutableStateOf(5) }

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

            // Edit post title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Edit post",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Edit form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Post image - exactly like mockup
                Image(
                    painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Content field - exactly like mockup
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    maxLines = 5,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Star rating - exactly like mockup
                StarRating(
                    rating = rating,
                    maxRating = 5,
                    size = 24.dp,
                    isInteractive = true,
                    onRatingChanged = { newRating ->
                        rating = newRating
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons - exactly like mockup
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save button
                    Button(
                        onClick = {
                            // Save post with postViewModel
                            // postViewModel.updatePost(postId, content, rating)
                            onPostUpdated()
                        },
                        modifier = Modifier
                            .width(120.dp)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B6F47)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    // Delete button
                    TextButton(
                        onClick = {
                            // Delete post with postViewModel
                            // postViewModel.deletePost(postId)
                            onPostDeleted()
                        }
                    ) {
                        Text(
                            text = "delete post",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}