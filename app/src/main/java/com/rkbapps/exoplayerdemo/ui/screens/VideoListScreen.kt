package com.rkbapps.exoplayerdemo.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.viewmodels.VideoListViewModel

class VideoListScreen(private val videos:List<MediaVideos>) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel:VideoListViewModel = hiltViewModel()
        val navigator = LocalNavigator.current

        val searchQuery = remember {
            mutableStateOf("")
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = videos[0].folderName) },
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        ambientColor = Color.Blue,
                        spotColor = Color.Blue
                    ),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(value = searchQuery.value, onValueChange = {
                    searchQuery.value = it
                },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "")
                    },
                    placeholder = {
                        Text(text = "Search Videos")
                    }
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                LazyColumn {
                    items(items=videos){video->
                        VideosItem(item = video) {
                            viewModel.savePathInSaveStateHandel(it.path)
                            navigator?.push(OfflineVideoPlayerScreen(it))
                        }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun VideosItem(item:MediaVideos,onClick:(item:MediaVideos)->Unit) {
        ElevatedCard(
            modifier = Modifier.padding(vertical = 4.dp),
            onClick = {onClick.invoke(item)}
        ) {

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(height = 50.dp, width = 70.dp)
                    .clip(RoundedCornerShape(8.dp)),){
                    GlideImage(
                        model = item.path,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = item.displayName, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(text = Constants.convertTime(item.duration), maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                }
            }

        }


    }
}