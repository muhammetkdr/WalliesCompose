package com.oguzdogdu.walliescompose.features.authenticateduser

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.features.authenticateduser.changeprofilephoto.ChangeProfilePhotoDialog
import com.oguzdogdu.walliescompose.features.authenticateduser.component.AnimatedStepProgressIndicator
import com.oguzdogdu.walliescompose.features.authenticateduser.component.StepName
import com.oguzdogdu.walliescompose.features.settings.components.MenuRowItems
import com.oguzdogdu.walliescompose.ui.theme.bold
import com.oguzdogdu.walliescompose.ui.theme.medium
import com.oguzdogdu.walliescompose.ui.theme.regular
import com.oguzdogdu.walliescompose.util.MenuRow
import com.oguzdogdu.walliescompose.util.ReusableMenuRow
import com.oguzdogdu.walliescompose.util.resolveImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.immutableListOf

@Composable
fun AuthenticatedUserScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthenticatedUserViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToChangeNameAndSurname: (String,String,String,String) -> Unit,
    navigateToChangePassword: () -> Unit,
    navigateToChangeEmail: () -> Unit,
) {

    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val dialogState by viewModel.changeProfilePhotoBottomSheetOpenStat.collectAsStateWithLifecycle()
    val firebaseSteps by viewModel.currentStep.collectAsStateWithLifecycle()
    val firebaseAllStepCompleted by viewModel.allStepsCompleted.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel.handleUiEvents(AuthenticatedUserEvent.CheckUserAuth)
        viewModel.handleUiEvents(AuthenticatedUserEvent.FetchUserInfos)
    }

    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            viewModel.setInstantlyProfileImageToDialog(imageUri)
        }
    }

    Scaffold(modifier = modifier
        .fillMaxSize(), topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigateBack.invoke() },
                modifier = Modifier.wrapContentSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.wrapContentSize()
                )
            }

            Text(
                text = stringResource(id = R.string.profile_title),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                fontFamily = medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
    }) {
            AuthenticatedUserScreenContent(
                userInfoState = userState,
                verificationStepFromDB = firebaseSteps,
                isCompleteAllSteps = firebaseAllStepCompleted,
                completeAllSteps = { complete ->
                    viewModel.adjustIsAllStepCompleted(complete)
                },
                onSignOutClick = {
                    viewModel.handleUiEvents(AuthenticatedUserEvent.SignOut)
                    navigateToLogin.invoke()
                },
                onChangeProfilePhotoClick = { dialog ->
                    viewModel.handleUiEvents(
                        AuthenticatedUserEvent.OpenChangeProfileBottomSheet(
                            isOpen = dialog
                        )
                    )
                }, onProfilePhotoClick = {
                    galleryLauncher.launch("image/*")
                }, onChangeProfilePhotoButtonClick = {
                    viewModel.handleUiEvents(AuthenticatedUserEvent.ChangeProfileImage(photoUri = imageUri))
                }, dismissDialog = { dialog ->
                    viewModel.handleUiEvents(
                        AuthenticatedUserEvent.OpenChangeProfileBottomSheet(
                            dialog
                        )
                    )
                }, onChangeNameAndSurnameClick = {
                    navigateToChangeNameAndSurname.invoke(
                        userState.name.orEmpty(),
                        userState.surname.orEmpty(),
                        userState.bio.orEmpty(),
                        userState.location.orEmpty()
                    )
                }, onChangePasswordClick = {
                    navigateToChangePassword.invoke()
                }, onChangeEmailClick = {
                    navigateToChangeEmail.invoke()
                },
                showDialog = dialogState,
                modifier = Modifier.padding(it)
            )
        }
    }

@Composable
fun AuthenticatedUserScreenContent(
    userInfoState: UserInfoState,
    verificationStepFromDB:String?,
    isCompleteAllSteps: Boolean,
    completeAllSteps: (Boolean) -> Unit,
    onSignOutClick: () -> Unit,
    onChangeProfilePhotoClick: (Boolean) -> Unit,
    onProfilePhotoClick: () -> Unit,
    onChangeProfilePhotoButtonClick: () -> Unit,
    dismissDialog: (Boolean) -> Unit,
    onChangeNameAndSurnameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    showDialog: Boolean,
    modifier: Modifier = Modifier,
) {
    val isAuthenticated =
        rememberUpdatedState(
            newValue = userInfoState.isAuthenticatedWithFirebase
                    or
                    userInfoState.isAuthenticatedWithGoogle
        )

    if (!isAuthenticated.value) { UserNotAuthenticatedInfo() }

    var visibilityOfSteps by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(verificationStepFromDB,isCompleteAllSteps)  {
        visibilityOfSteps = !isCompleteAllSteps
    }
    BoxWithConstraints {
        val pageSize = this.maxHeight
        Box(
            modifier = modifier
                .height(pageSize)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pageSize)
                    .padding(horizontal = 12.dp)
            ) {
                if (visibilityOfSteps){
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AnimatedContent(
                            targetState = isCompleteAllSteps, transitionSpec = {
                                scaleIn(tween(1500)).togetherWith(scaleOut(tween(1500)))
                            }, label = ""
                        ) { state ->
                            when(state) {
                                false ->  AnimatedStepProgressIndicator(
                                    userVerificationStep = verificationStepFromDB,
                                    completeAllSteps = completeAllSteps,
                                )
                                true -> Text(
                                    text = "\uD83C\uDF89 Congratulations! You have completed your profile! \uD83C\uDF89",
                                    fontFamily = medium,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(Modifier.size(16.dp))
                }

                AuthenticatedUserWelcomeCard(
                    userInfoState = userInfoState,
                    onChangeProfilePhotoClick = {
                        onChangeProfilePhotoClick.invoke(it)
                    }
                )
                EditProfileInformationContent(
                    onChangeNameAndSurnameClick = {
                        onChangeNameAndSurnameClick.invoke()
                    },
                    onChangePasswordClick = {
                        onChangePasswordClick.invoke()
                    },
                    onChangeEmailClick = {
                        onChangeEmailClick.invoke()
                    }
                )
            }
            ChangeProfilePhotoDialog(
                userInfoState = userInfoState,
                isOpen = showDialog,
                onDismiss = {
                    dismissDialog.invoke(false)
                }, onProfilePhotoClick = {
                    onProfilePhotoClick.invoke()
                }, onChangeProfilePhotoButtonClick = {
                    onChangeProfilePhotoButtonClick.invoke()
                })
            Button(
                onClick = {
                    onSignOutClick.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_out),
                    fontSize = 14.sp,
                    fontFamily = medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun UserNotAuthenticatedInfo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.user_not_auth_caution), fontSize = 14.sp,
            fontFamily = medium
        )
    }
}

@Composable
fun AuthenticatedUserWelcomeCard(
    userInfoState: UserInfoState,
    onChangeProfilePhotoClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ).copy(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                AnimatedContent(
                    targetState = userInfoState.profileImage,
                    transitionSpec = {
                        (expandIn(tween(1000)))
                            .togetherWith(shrinkOut(tween(1000)))
                    },
                    label = ""
                ) { image ->
                    EditableProfileImage(
                        profileImage = image,
                        imageButtonEnabled = userInfoState.stepName == StepName.BIO.stepName || userInfoState.allStepsCompleted,
                        onChangeProfilePhotoClick = onChangeProfilePhotoClick
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))

            Text(
                buildAnnotatedString {
                    append(stringResource(id = R.string.welcome_profile))
                    withStyle(style = SpanStyle(fontFamily = bold)) {
                        append(", ${userInfoState.name.orEmpty()} ${userInfoState.surname.orEmpty()} \uD83D\uDD90")
                    }
                },
                fontSize = 16.sp,
                fontFamily = medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (userInfoState.location?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "Location Icon",
                        modifier = Modifier.wrapContentSize(),
                        tint = Color.Red
                    )
                    Spacer(modifier = modifier.size(4.dp))
                    Text(
                        text = userInfoState.location,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 14.sp,
                        fontFamily = medium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
            if (userInfoState.bio?.isNotEmpty() == true) {
                Text(
                    text = userInfoState.bio,
                    fontSize = 14.sp,
                    fontFamily = regular,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EditableProfileImage(
    profileImage: String?,
    imageButtonEnabled: Boolean,
    onChangeProfilePhotoClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .wrapContentWidth()
            .height(150.dp),
    ) {
        AsyncImage(
            model = resolveImage(
                profileImage = profileImage,
                uri = null,
                defaultImage = R.drawable.ic_default_avatar
            ),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        width = 2.dp,
                        color = Color.Gray
                    ), shape = CircleShape
                )
                .align(Alignment.TopCenter)
        )
            OutlinedButton(
                onClick = { onChangeProfilePhotoClick(true) },
                enabled = imageButtonEnabled,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter)
                    .height(32.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(space = 8.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_photo),
                        contentDescription = "Photo Icon",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.edit),
                        fontSize = 16.sp,
                        fontFamily = medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }


@Composable
fun EditProfileInformationContent(
    onChangeNameAndSurnameClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val profileOptionsList = immutableListOf(
        MenuRow(
            titleRes = R.string.edit_user_info_title,
            icon = R.drawable.ic_person
        ),
        MenuRow(
            titleRes = R.string.edit_email_title,
            icon = R.drawable.ic_email
        ),
        MenuRow(
            titleRes = R.string.forgot_password_title,
            icon = R.drawable.password
        )
    )
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp)
    ) {
        items(count = 1) { index: Int ->
            ReusableMenuRow(data = profileOptionsList,
                index = index,
                modifier = Modifier.fillMaxWidth(),
                itemContent = { menu ->
                    MenuRowItems(
                        modifier = modifier, menuRow = menu, arrow = true
                    )
                }
            ) {
                handleMenuItemClick(
                    itemIndex = it,
                    coroutineScope = scope,
                    openPersonalInformation = {
                        onChangeNameAndSurnameClick.invoke()
                    },
                    openEditEmail = {
                        onChangeEmailClick.invoke()
                    }, openChangePassword = {
                        onChangePasswordClick.invoke()
                    }
                )
            }
        }
    }
}

fun handleMenuItemClick(
    itemIndex: Int,
    coroutineScope: CoroutineScope,
    openPersonalInformation: () -> Unit,
    openEditEmail: () -> Unit,
    openChangePassword: () -> Unit,
) {
    coroutineScope.launch {
        when (itemIndex) {
            0 -> {
                openPersonalInformation.invoke()
            }

            1 -> {
                openEditEmail.invoke()
            }

            2 -> {
                openChangePassword.invoke()

            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun AuthenticatedUserScreenPreview() {
    AuthenticatedUserScreenContent(
        userInfoState = UserInfoState(
            name = "Muhammet",
            surname = "Küdür",
            email = "muhammetdeneme@gmail.com",
            isAuthenticatedWithGoogle = true,
            isAuthenticatedWithFirebase = false,
        ),
        verificationStepFromDB = null,
        completeAllSteps = {},
        isCompleteAllSteps = false,
        onSignOutClick = {},
        onChangeProfilePhotoClick = {},
        onProfilePhotoClick = {},
        onChangeProfilePhotoButtonClick = {},
        dismissDialog = {} ,
        onChangeNameAndSurnameClick = {},
        onChangePasswordClick = {},
        onChangeEmailClick = {},
        showDialog = false
    )
}
