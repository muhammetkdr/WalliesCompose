package com.oguzdogdu.walliescompose.features.authenticateduser.component

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.ui.theme.medium
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
enum class StepName(val stepName: String) {
    SURNAME("Surname"),
    BIO("Bio"),
    PROFILE_PICTURE("Profile Picture"),
    LOCATION("Location")
}

@Immutable
sealed class StepState {
    data object NotStarted : StepState()
    data object InProgress : StepState()
    data object Completed : StepState()
}

@Immutable
data class UserProfileStep(
    val stepName: String? = StepName.SURNAME.stepName,
    @DrawableRes val drawableRes: Int?,
    val state: StepState = StepState.NotStarted
) {
    fun copyWithState(newState: StepState): UserProfileStep {
        return this.copy(state = newState)
    }
}

@Stable
interface ProfileStepsIterator {
    fun hasNext(): StateFlow<Boolean>
    fun current(): StateFlow<UserProfileStep>
    fun next(): UserProfileStep
    fun reset()
    fun getAllSteps(): List<UserProfileStep>
    fun completeCurrentStep()
    fun isAllStepsCompleted(): StateFlow<Boolean>
}

@Stable
class UserProfileStepsIterator : ProfileStepsIterator {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println(throwable)
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + exceptionHandler)

    private fun initialSteps() = listOf(
        UserProfileStep(
            StepName.SURNAME.stepName,
            R.drawable.id_card_svgrepo_com,
            StepState.NotStarted
        ),
        UserProfileStep(
            StepName.BIO.stepName,
            R.drawable.resume_business_cv_work_job_curriculum_svgrepo_com,
            StepState.NotStarted
        ),
        UserProfileStep(
            StepName.PROFILE_PICTURE.stepName,
            R.drawable.user_avatar_svgrepo_com,
            StepState.NotStarted
        ),
        UserProfileStep(
            StepName.LOCATION.stepName,
            R.drawable.location_pin_svgrepo_com,
            StepState.NotStarted
        )
    )
    private val _steps = MutableStateFlow(initialSteps())
    val steps: StateFlow<List<UserProfileStep>> = _steps.asStateFlow()

    private var currentIndex = 0

    private val isAllStepsCompleted = _steps.map { stepsList ->
        stepsList.all { it.state == StepState.Completed }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), false)

    private val hasNext = _steps.map { stepsList ->
        currentIndex < stepsList.size - 1
    }.stateIn(coroutineScope, SharingStarted.Eagerly, false)

    private val current = _steps.map {
        it[currentIndex]
    }.stateIn(coroutineScope, started = SharingStarted.Eagerly, UserProfileStep(null,null))

    fun initializeFromRemote(remoteStepName: String?) {
        try {
            if (!remoteStepName.isNullOrEmpty()) {
                val targetStepIndex = _steps.value.indexOfFirst { it.stepName == remoteStepName }

                if (targetStepIndex != -1) {
                    val updatedSteps = _steps.value.mapIndexed { index, step ->
                        when {
                            index <= targetStepIndex -> step.copyWithState(StepState.Completed)
                            index == targetStepIndex + 1 -> step.copyWithState(StepState.InProgress)
                            else -> step.copyWithState(StepState.NotStarted)
                        }
                    }

                    _steps.update { updatedSteps.toMutableList() }
                    currentIndex = targetStepIndex + 1
                }
            } else {
                currentIndex = 0
                updateCurrentStepState(StepState.InProgress)
            }
        } catch (e: Exception) {
            Log.e("UserProfileStepsIterator", "Error initializing from remote", e)
            currentIndex = 0
            updateCurrentStepState(StepState.InProgress)
        }
    }

    override fun hasNext(): StateFlow<Boolean> = hasNext

    override fun current(): StateFlow<UserProfileStep> = current

    override fun getAllSteps(): List<UserProfileStep> = _steps.value

    override fun next() : UserProfileStep {
        coroutineScope.launch(Dispatchers.IO) {
            when {
                hasNext.value -> {
                    completeCurrentStep()
                    delay(500)
                    currentIndex++
                    updateCurrentStepState(StepState.InProgress)
                }
                else -> completeCurrentStep()
            }
        }
        return current().value
    }

    override fun reset() {
        currentIndex = 0
        _steps.update {
            initialSteps()
        }
        updateCurrentStepState(StepState.InProgress)
    }

    override fun completeCurrentStep() {
        if (currentIndex < _steps.value.size) {
            updateCurrentStepState(StepState.Completed)
        }
    }

    private fun updateCurrentStepState(newState: StepState) {
        val updatedSteps = _steps.value.toMutableList()
        updatedSteps[currentIndex] = updatedSteps[currentIndex].copyWithState(newState)
        _steps.update { updatedSteps }
    }

    override fun isAllStepsCompleted(): StateFlow<Boolean> = isAllStepsCompleted
}

@Composable
fun AnimatedStepProgressIndicator(
    userVerificationStep: String?,
    completeAllSteps: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val stepsIterator = remember { UserProfileStepsIterator() }
    val stateOfSteps = stepsIterator.steps.collectAsStateWithLifecycle()
    val allStepsCompleted by stepsIterator.isAllStepsCompleted().collectAsStateWithLifecycle()

    LaunchedEffect(userVerificationStep) {
        if (!userVerificationStep.isNullOrEmpty()) {
            stepsIterator.initializeFromRemote(userVerificationStep)
        }
    }

    LaunchedEffect(userVerificationStep) {
        when (userVerificationStep == StepName.LOCATION.stepName) {
            true -> completeAllSteps.invoke(true)
            false -> completeAllSteps.invoke(false)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.elevatedCardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!allStepsCompleted) {
                Text(
                    text = "In order to verify your account, you need to complete the following steps.",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontFamily = medium,
                    textAlign = TextAlign.Start,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                stateOfSteps.value.forEachIndexed { index, step ->
                    val isLastStep by remember { derivedStateOf { mutableStateOf(index == stateOfSteps.value.size - 1) } }
                    AnimatedStepItem(
                        step = step,
                        isLastStep = isLastStep.value
                    )
                }
            }
        }
    }
}


@Composable
private fun AnimatedStepItem(
    step: UserProfileStep,
    isLastStep: Boolean
) {
    var showCheckmark by remember { mutableStateOf(false) }
    val progressAnim = remember { Animatable(0f) }
    val iconAnim = remember { Animatable(0f) }

    ConstraintLayout {
        val (iconBox, divider, stepText) = createRefs()

        Box(
            modifier = Modifier
                .size(32.dp)
                .offset {
                    IntOffset(x = 0, y = iconAnim.value.toInt())
                }
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (step.state != StepState.Completed) Color.Gray.copy(alpha = 0.5f) else Color(
                        0xFF4CAF50
                    ),
                    shape = CircleShape
                )
                .background(
                    color = if (step.state == StepState.Completed) Color(0xFF4CAF50) else Color.White,
                    shape = CircleShape
                )
                .constrainAs(iconBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
            contentAlignment = Alignment.Center
        ) {

            val icon = remember(step.state) {  when (step.state) {
                StepState.Completed -> R.drawable.ic_completed
                else -> step.drawableRes
            } }

            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = if (step.state == StepState.Completed) Color.White else Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }

        }
        LaunchedEffect(step.state) {
            if (step.state == StepState.InProgress && !progressAnim.isRunning) {
                iconAnim.animateTo(
                    -30f,
                    animationSpec = tween(300, easing = EaseInOut)
                )
                iconAnim.animateTo(0f, animationSpec = tween(300, easing = EaseInOut))
            }
        }

        if (!isLastStep) {
            LaunchedEffect(step.state) {
                if (step.state == StepState.Completed) {
                    progressAnim.animateTo(
                        targetValue = 1f, animationSpec = tween(300, easing = EaseOutCubic)
                    )
                    showCheckmark = true
                }
                else {
                    showCheckmark = false
                    progressAnim.snapTo(0f)
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .constrainAs(divider) {
                        start.linkTo(iconBox.end)
                        top.linkTo(iconBox.top)
                        bottom.linkTo(iconBox.bottom)
                    }
                    .requiredWidthIn(min = 48.dp, max = 72.dp)
                    .padding(horizontal = 4.dp)
                    .drawWithContent {
                        drawContent()
                        val strokeWidth = 2.dp.toPx()
                        val path = Path().apply {
                            if (progressAnim.value > 0f) {
                                moveTo(0f, size.height / 2)
                                lineTo(size.width * progressAnim.value, size.height / 2)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF4CAF50),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    },
                thickness = 2.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
        } else {
            LaunchedEffect(step.state) {
                showCheckmark = step.state == StepState.Completed
            }
        }

        Text(
            text = step.stepName.orEmpty(),
            fontFamily = medium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.constrainAs(stepText) {
                top.linkTo(iconBox.bottom, margin = 4.dp)
                start.linkTo(iconBox.start)
                end.linkTo(iconBox.end)
            }
        )
    }
}