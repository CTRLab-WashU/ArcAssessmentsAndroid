package edu.wustl.arc.sageassessments

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import edu.wustl.arc.navigation.NavigationManager
import edu.wustl.arc.study.Study
import edu.wustl.arc.study.TestSession
import org.sagebionetworks.assessmentmodel.Step
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.assessmentmodel.presentation.AssessmentViewModel
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseScreenDialog
import org.sagebionetworks.assessmentmodel.presentation.compose.PauseTopBar
import java.io.File

class ArcAssessmentFragment: AssessmentFragment() {

    lateinit var arcViewModel: ArcAssessmentViewModel

    // WashU Arc assessments require portrait only
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Prevent landscape mode for all steps in Motor Control Assessments
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        arcViewModel = ViewModelProvider(this)[ArcAssessmentViewModel::class.java]
        arcViewModel.arcResultLiveData.observe(viewLifecycleOwner) {
            if (it.isComplete) {
                viewModel.assessmentNodeState.currentResult.inputResults.add(it)
                viewModel.goForward()
            }
        }
    }

    override fun showStep(showNodeState: AssessmentViewModel.ShowNodeState) {
        super.showStep(showNodeState)
        NavigationManager.getInstance().removeController()
        Study.openNextFragment()
    }

    override fun getFragmentForStep(step: Step): Fragment {
        val arcStateMachine = Study.getStateMachine() as? ArcStateMachine
        (step as? ArcTestInfoStepObject)?.let {
            when(it.testType) {
                ArcAssessmentType.SYMBOLS -> arcStateMachine?.addSymbolsTest(requireContext(), it)
                ArcAssessmentType.PRICES -> arcStateMachine?.addPricesTest(requireContext(), it)
                ArcAssessmentType.GRIDS -> arcStateMachine?.addGridTest(requireContext(), it)
            }
            // None of the TestSession information is needed for Sage
            Study.setMostRecentTestSession(TestSession(0, 0, 0))
            Study.getCurrentTestSession().markStarted()
            Study.getParticipant().save()
            (Study.getStateMachine() as? ArcStateMachine)?.save()
            arcViewModel.startArcSessionResult(step.testType)
        }
        return ArcNavigatorFragment()
    }
}

class ArcAssessmentViewModel(app: Application): AndroidViewModel(app), SessionCompleteListener {

    // Keeps track of an ARC Result, non-null when an ARC assessment is running
    var arcResultLiveData = MutableLiveData<ArcAssessmentResultObject>()

    init {
        (Study.getStateMachine() as? ArcStateMachine)?.listener = this
    }

    fun startArcSessionResult(assessmentType: ArcAssessmentType) {
        arcResultLiveData.value = ArcAssessmentResultObject(
            identifier = assessmentType.toIdentifier(),
            assessmentType = assessmentType)
    }

    // ARC library callback when an ArcAssessment is finished
    override fun onSessionComplete(signatureList: ArrayList<File>, session: TestSession) {
        val startResult = arcResultLiveData.value ?: run {
            Log.e(
                ArcAssessmentFragment::class.java.simpleName,
                "currentArcResult must be non-null, did you call startArcSessionResult before this?")
            return
        }

        // Convert ARC Test Result model to JSON result
        arcResultLiveData.value = ArcAssessmentObject.createAssessmentResult(
            session,
            startResult
        ).copy(isComplete = true)

        NavigationManager.getInstance().clearBackStack()
    }
}

class ArcNavigatorFragment: Fragment() {

    override fun onDestroy() {
        super.onDestroy()
        NavigationManager.getInstance().clearBackStack()
        Study.getStateMachine().cache.segments = mutableListOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_arc_navigator, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.pauseButtonComposable)
        composeView?.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val openDialog = remember { mutableStateOf(false) }
                PauseScreenDialog(
                    showDialog = openDialog.value,
                    assessmentViewModel = (parentFragment as ArcAssessmentFragment).viewModel
                ) {
                    openDialog.value = false
                }
                PauseTopBar(
                    onPauseClicked = { openDialog.value = true },
                    onSkipClicked = { /** not needed */ },
                    showSkip = false)
            }
        }
        return view
    }
}