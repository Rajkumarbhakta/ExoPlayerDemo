package com.rkbapps.exoplayerdemo.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.rkbapps.exoplayerdemo.BuildConfig
import com.rkbapps.exoplayerdemo.R
import com.rkbapps.exoplayerdemo.models.Folders
import com.rkbapps.exoplayerdemo.ui.theme.onPrimaryContainerDark
import com.rkbapps.exoplayerdemo.ui.theme.primaryContainerDark
import com.rkbapps.exoplayerdemo.ui.theme.primaryContainerLight
import com.rkbapps.exoplayerdemo.viewmodels.HomeViewModel


class HomeScreen :Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: HomeViewModel = getViewModel()
        val context = LocalContext.current
        val navigator = LocalNavigator.current
        val folderList = viewModel.folderList.collectAsStateWithLifecycle()
        val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it){
                Toast.makeText(context, "Permission granted.", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    intent.setData(uri)
                    context.startActivity(intent)
                }
            } else {
                //below android 11=======
                if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

                }else if(ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)){

                }else{
                    permissionLauncher.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }
        }

        val searchQuery = remember {
            mutableStateOf("")
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Folders") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = FloatingActionButtonDefaults.containerColor,
                        titleContentColor = contentColorFor(FloatingActionButtonDefaults.containerColor)
                    ),
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        ambientColor = Color.Blue,
                        spotColor = Color.Blue
                    ),
                )
            },
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    val video = viewModel.readLastPlayedVideo()
                    if (video != null) {
                        navigator?.push(OfflineVideoPlayerScreen(video))
                    }
                },
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow,
                        contentDescription = ""
                    )
                }
            }
        ) { innerPadding ->
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp)
            ){
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (folderList.value.isLoading ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }else{

                    OutlinedTextField(value = searchQuery.value, onValueChange = {
                        searchQuery.value = it
                        viewModel.searchFolder(it)
                    },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "")
                        },
                        placeholder = {
                            Text(text = "Search Folders")
                        }
                    )
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    LazyColumn (){
                        items(items = folderList.value.folders){ videos ->
                            VideoFolderItem(item = videos){
                                navigator?.push(VideoListScreen(it.files))
                            }
                        }
                    }
                }
                }
            }
        }

    }

    @Composable
    fun VideoFolderItem(item:Folders,onClick:(item:Folders)->Unit) {
//        ElevatedCard (
//            modifier = Modifier
//                .padding(vertical = 4.dp),
//            onClick = {onClick.invoke(item)},
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        ){
            Row (modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick.invoke(item)
                }
                .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(height = 50.dp, width = 70.dp)
                    .clip(RoundedCornerShape(8.dp)),){
                    AsyncImage(
                        model = R.drawable.video_folder,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(text = "${item.files.size} videos", maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                }
            }
        }


//    }


}