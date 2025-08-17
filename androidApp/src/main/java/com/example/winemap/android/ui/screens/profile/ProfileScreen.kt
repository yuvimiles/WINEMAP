package com.example.winemap.android.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import com.example.winemap.android.ui.rememberAuthViewModel
import com.example.winemap.android.ui.components.StarRating

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToEditPost: (String) -> Unit = {}
) {
    val authViewModel = rememberAuthViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    // Mock data - replace with real data from ViewModel
    val userPosts = remember {
        listOf(
            UserPost(
                id = "1",
                imageUrl = "",
                wineryName = "היה לנו היום כיף מטורף",
                content = "מגזרים אנחנו במיטבנו טעיב עם מתוחים שלנו",
                rating = 5,
                timeAgo = "2 hour ago"
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Same background as other screens
        Image(
            painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.9f
        )

        // White overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top header with back arrow and logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8DCC6))
                    .padding(vertical = 16.dp)
            ) {
                // Back arrow
                IconButton(
                    onClick = { /* Navigate back */ },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                // Logo and title centered
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

            // Profile section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile picture
                    Image(
                        painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Yuvi_Miles",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "love wine",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // Edit button
                    IconButton(
                        onClick = onNavigateToEditProfile,
                        modifier = Modifier
                            .background(
                                Color.Gray.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Log out button
                OutlinedButton(
                    onClick = { authViewModel.signOut() },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Log out", fontSize = 14.sp)
                }
            }

            // Posts section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "posts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            // Posts list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(userPosts) { post ->
                    UserPostCard(
                        post = post,
                        onEditClick = { onNavigateToEditPost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserPostCard(
    post: UserPost,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Post image
            Image(
                painter = painterResource(id = com.example.winemap.android.R.drawable.winemap_logo),
                contentDescription = "Post Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.wineryName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = post.content,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Star rating
                StarRating(
                    rating = post.rating,
                    size = 14.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = post.timeAgo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Edit button
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Post",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}