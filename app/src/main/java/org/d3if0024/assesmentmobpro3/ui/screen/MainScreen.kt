package org.d3if0024.assesmentmobpro3.ui.screen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0024.assesmentmobpro3.R
import org.d3if0024.assesmentmobpro3.model.Mouse
import org.d3if0024.assesmentmobpro3.model.User
import org.d3if0024.assesmentmobpro3.network.MouseApi
import org.d3if0024.assesmentmobpro3.network.UserDataStore
import org.d3if0024.assesmentmobpro3.network.getCropperImage
import org.d3if0024.assesmentmobpro3.network.signIn
import org.d3if0024.assesmentmobpro3.network.signOut
import org.d3if0024.assesmentmobpro3.ui.theme.AssesmentMobpro3Theme
import org.d3if0024.assesmentmobpro3.util.SettingsDataStore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    val viewModel: MainViewModel = viewModel()
    val errorMessage by viewModel.errorMessage

    var showDialog by remember { mutableStateOf(false) }
    var showMouseDialog by remember { mutableStateOf(false) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap = getCropperImage(context.contentResolver, it)
        if (bitmap != null) showMouseDialog = true
    }
    val settingStore = SettingsDataStore(context)
    val showList by settingStore.layoutFlow.collectAsState(true)

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = (stringResource(id = R.string.app_name)))
            },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    Row {
                        IconButton(onClick = {
                            if (user.email.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch { signIn(context, dataStore) }
                            } else {
                                showDialog = true
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = stringResource(id = R.string.profil),
                                tint = MaterialTheme.colorScheme.primary

                            )
                        }
                        IconButton(onClick = { CoroutineScope(Dispatchers.IO).launch {settingStore.saveLayout(!showList) } }) {
                            Icon(
                                painter = painterResource(
                                    if (showList) R.drawable.baseline_grid_view_24
                                    else R.drawable.baseline_view_list_24
                                ),
                                contentDescription = stringResource(
                                    if (showList) R.string.grid
                                    else R.string.list
                                ),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    }

            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val options = CropImageContractOptions(
                    null, CropImageOptions(
                        imageSourceIncludeGallery = false,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.tambah)
                )
            }
        }
    ) { padding ->
        ScreenContent(showList, viewModel, user.email, Modifier.padding(padding))
        if (showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }
        if (showMouseDialog) {
            MouseDialog(
                bitmap = bitmap,
                onDismissRequest = { showMouseDialog = false }) { namaMouse, modelMouse ->
                viewModel.saveData(user.email, namaMouse, modelMouse, bitmap!!)
                showMouseDialog = false
            }
            if (errorMessage != null) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }
}

@Composable
fun ScreenContent(showList: Boolean, viewModel: MainViewModel, userId: String, modifier: Modifier) {
    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    LaunchedEffect(userId) {
        viewModel.retrieveData(userId)
    }

    when (status) {
        MouseApi.ApiStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        MouseApi.ApiStatus.SUCCESS -> {
            if (data.isEmpty()){
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = stringResource(id = R.string.data_kosong)
                    )
                }
            }else{
                if (showList) {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(data) {
                            ListItem(mouse = it)
                            Divider()
                        }
                    }
                }
                else{
                    LazyVerticalStaggeredGrid(
                        modifier = modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp, 8.dp ,8.dp, 84.dp),
                    ){
                        items(data){
                            GridItem(mouse = it, onDelete = {mouseId ->
                                Log.d("screenContent", "Deleting mouse with ID: $mouseId")
                                viewModel.deleteData(userId, mouseId)
                            })
                        }
                    }
                }
            }

        }

        MouseApi.ApiStatus.FAILED -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(text = stringResource(id = R.string.error))
                Button(
                    onClick = { viewModel.retrieveData(userId) },
                    modifier = Modifier.padding(top = 16.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.coba_lagi))
                }
            }
        }
    }
}

@Composable
fun GridItem(mouse: Mouse, onDelete: (String) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    DisplayAlertDialog(
        openDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirmation = {
            onDelete(mouse.id)
            showDialog = false
        }
    )

    Box(
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.BottomCenter

    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(MouseApi.getMouseUrl(mouse.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.gambar, mouse.namaMouse),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = mouse.namaMouse,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = mouse.modelMouse,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ListItem(mouse: Mouse) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(MouseApi.getMouseUrl(mouse.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.gambar, mouse.namaMouse),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.baseline_broken_image_24),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        Text(
            text = mouse.namaMouse,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = mouse.modelMouse,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = Color.Black
        )
    }

}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScreenPreview() {
    AssesmentMobpro3Theme {
        MainScreen()
    }
}