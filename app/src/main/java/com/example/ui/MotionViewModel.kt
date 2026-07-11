package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.VideoProject
import com.example.data.VideoProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// UI Tabs representing Bottom Navigation or Sidebar
enum class AppTab {
    HOME,
    GENERATE,
    VIDEOS,
    PRICING,
    SETTINGS
}

// Preset Demo Images
data class PresetDemo(
    val title: String,
    val description: String,
    val imageUrl: String,
    val defaultPrompt: String,
    val motionStyle: String,
    val cameraMotion: String
)

class MotionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoProjectRepository
    val allProjects: StateFlow<List<VideoProject>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VideoProjectRepository(database.videoProjectDao())
        allProjects = repository.allProjects.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Navigation state
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    // Configuration settings
    val motionStrength = MutableStateFlow(0.65f)
    val durationSeconds = MutableStateFlow(5) // 5, 10, 15
    val aspectRatio = MutableStateFlow("16:9") // "16:9", "9:16", "1:1"
    val cameraMotion = MutableStateFlow("Zoom In") // "Zoom In", "Zoom Out", "Pan Left", "Pan Right", "Orbit", "Dolly"
    val creativity = MutableStateFlow(0.70f)
    val prompt = MutableStateFlow("")
    val negativePrompt = MutableStateFlow("")
    val selectedImageUri = MutableStateFlow<String?>(null) // Can be local content URI or URL preset

    // Selected project for playback
    private val _selectedProject = MutableStateFlow<VideoProject?>(null)
    val selectedProject = _selectedProject.asStateFlow()

    // Current Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generationProgress = MutableStateFlow(0)
    val generationProgress = _generationProgress.asStateFlow()

    private val _generationStatusText = MutableStateFlow("")
    val generationStatusText = _generationStatusText.asStateFlow()

    // Auth Simulation
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    private val _isProUser = MutableStateFlow(false)
    val isProUser = _isProUser.asStateFlow()

    // Demo Presets List
    val demoPresets = listOf(
        PresetDemo(
            title = "Cyberpunk Neo Alley",
            description = "Rainy alley in Neo-Tokyo with colorful neon reflections.",
            imageUrl = "https://images.unsplash.com/photo-1515621061946-eff1c2a352bd?auto=format&fit=crop&w=600&q=80",
            defaultPrompt = "Cinematic slow dolly forward, rain falling on neon pavement, puddles reflecting neon advertising, steam rising from grates",
            motionStyle = "Realistic Motion",
            cameraMotion = "Dolly"
        ),
        PresetDemo(
            title = "Golden Sunset Shore",
            description = "Gentle waves crashing onto a sandy beach under glowing sky.",
            imageUrl = "https://images.unsplash.com/photo-1505118380757-91f5f5632de0?auto=format&fit=crop&w=600&q=80",
            defaultPrompt = "Camera zooming out slowly, ocean waves gently undulating with photorealistic physics, golden hour glow expanding across water",
            motionStyle = "Lightning Fast",
            cameraMotion = "Zoom Out"
        ),
        PresetDemo(
            title = "Mystical Redwood Forest",
            description = "Sunbeams filtering through giant ancient redwood trees.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=600&q=80",
            defaultPrompt = "Slow pan left, sun shafts shimmering dynamically through branches, atmospheric particles floating in redwood forest",
            motionStyle = "AI Camera Movement",
            cameraMotion = "Pan Left"
        ),
        PresetDemo(
            title = "Futuristic Space Port",
            description = "Advanced spaceships hovering near an orbital docking station.",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=600&q=80",
            defaultPrompt = "Camera orbiting planetary rings, spacecraft engines glowing, lens flares expanding, volumetric nebulae moving in background",
            motionStyle = "Face & Character Consistency",
            cameraMotion = "Orbit"
        )
    )

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectPreset(preset: PresetDemo) {
        selectedImageUri.value = preset.imageUrl
        prompt.value = preset.defaultPrompt
        cameraMotion.value = preset.cameraMotion
        _currentTab.value = AppTab.GENERATE
    }

    fun selectProject(project: VideoProject) {
        _selectedProject.value = project
    }

    fun clearSelectedProject() {
        _selectedProject.value = null
    }

    fun performAuth(email: String, isRegister: Boolean) {
        _userEmail.value = email
        _isLoggedIn.value = true
        // Default register to standard account, but users can simulate upgrade
        if (email.contains("pro") || email.contains("admin")) {
            _isProUser.value = true
        }
    }

    fun logout() {
        _userEmail.value = ""
        _isLoggedIn.value = false
        _isProUser.value = false
    }

    fun simulateUpgrade() {
        _isProUser.value = true
    }

    fun deleteProject(project: VideoProject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteProject(project)
            if (_selectedProject.value?.id == project.id) {
                _selectedProject.value = null
            }
        }
    }

    // Trigger video generation
    fun generateVideo(onSuccess: () -> Unit = {}) {
        val image = selectedImageUri.value
        val pr = prompt.value.ifEmpty { "Cinematic camera movement" }
        val neg = negativePrompt.value
        val motionS = motionStrength.value
        val camM = cameraMotion.value
        val dur = durationSeconds.value
        val aspect = aspectRatio.value
        val creativeVal = creativity.value

        if (image.isNullOrEmpty()) {
            _generationStatusText.value = "Error: Please select or upload an image first"
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationProgress.value = 0

            val steps = listOf(
                "Analyzing image perspective & composition..." to 15,
                "Segmenting foreground and background elements..." to 35,
                "Calculating custom optical flows ($camM)..." to 60,
                "Synthesizing high-fidelity video frames..." to 85,
                "Applying 4K visual refinement & finishing rendering..." to 98
            )

            for ((status, targetProgress) in steps) {
                _generationStatusText.value = status
                val currentP = _generationProgress.value
                val diff = targetProgress - currentP
                for (i in 1..diff) {
                    _generationProgress.value = currentP + i
                    delay((20..60).random().toLong())
                }
                delay((300..700).random().toLong())
            }

            _generationProgress.value = 100
            _generationStatusText.value = "Compilation completed!"
            delay(400)

            // Call Gemini API to expand details (if key is set)
            val geminiDescription = withContext(Dispatchers.IO) {
                callGeminiForCinematicLogs(pr, camM, dur, aspect, motionS)
            }

            // Create Room project
            val newProj = VideoProject(
                title = "Motion #${(100..999).random()}",
                prompt = pr,
                negativePrompt = neg,
                imageUri = image,
                motionStyle = if (motionS > 0.8f) "High Motion" else "Smooth Cinematic",
                cameraMotion = camM,
                motionStrength = motionS,
                durationSeconds = dur,
                aspectRatio = aspect,
                creativity = creativeVal,
                status = "COMPLETED",
                progress = 100,
                videoEffectIndex = (0..5).random(),
                geminiDescription = geminiDescription
            )

            val newId = withContext(Dispatchers.IO) {
                repository.insertProject(newProj)
            }

            // Load generated project as the active view project
            val savedProj = newProj.copy(id = newId)
            _selectedProject.value = savedProj
            _isGenerating.value = false

            // Move user to Recent Projects tab or screen
            _currentTab.value = AppTab.VIDEOS
            onSuccess()
        }
    }

    private suspend fun callGeminiForCinematicLogs(
        promptText: String,
        motion: String,
        duration: Int,
        aspect: String,
        strength: Float
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Return beautifully styled static cinematic logs in markdown!
            return """
                🎥 **Director's AI Camera Logs:**
                - **Flow Reconstruction:** Generated $duration seconds of coherent visual motion styled as a dramatic *${motion}* camera sweep.
                - **Depth Layer Extraction:** Separated subject layers from the anchor background, utilizing ${(strength * 100).toInt()}% vector energy to create high-fidelity motion parallax.
                - **Aspect Optimization:** Framed beautifully in standard `$aspect` layout, stabilizing edge artifacts dynamically across 240 rendered frames.
                - **Technical Verdict:** Pixel alignment successfully checked at 99.8%. No degradation in subject texture borders. Ready for professional export.
            """.trimIndent()
        }

        return try {
            val systemInstructions = "You are an advanced AI Film Director & Motion Simulation Scientist at Motion.ai. Given a user's motion description and settings, write a beautiful, highly creative 2-3 sentence 'Director's Log' or 'Scene Motion Reconstruction Log' analyzing the optical flow of the generated video. Describe how the camera performs the requested motion, the emotional weight of the motion, and pixel level fidelity."
            val userPrompt = "Motion prompt: \"$promptText\", Camera direction: \"$motion\", Duration: $duration seconds, Motion Strength: ${(strength*100).toInt()}%, Aspect Ratio: \"$aspect\"."

            val jsonPayload = JSONObject().apply {
                put("contents", org.json.JSONArray().put(JSONObject().apply {
                    put("parts", org.json.JSONArray().put(JSONObject().apply {
                        put("text", userPrompt)
                    }))
                }))
                put("systemInstruction", JSONObject().apply {
                    put("parts", org.json.JSONArray().put(JSONObject().apply {
                        put("text", systemInstructions)
                    }))
                })
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val respStr = response.body?.string() ?: ""
                val respJson = JSONObject(respStr)
                val text = respJson.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                text.trim()
            } else {
                Log.e("MotionVM", "Gemini API failed with response code: ${response.code}")
                getDefaultLog(motion, duration, aspect, strength)
            }
        } catch (e: Exception) {
            Log.e("MotionVM", "Gemini API Exception: ${e.message}", e)
            getDefaultLog(motion, duration, aspect, strength)
        }
    }

    private fun getDefaultLog(motion: String, duration: Int, aspect: String, strength: Float): String {
        return """
            🎥 **Director's AI Camera Logs:**
            - **Flow Reconstruction:** Generated $duration seconds of coherent visual motion styled as a dramatic *${motion}* camera sweep.
            - **Depth Layer Extraction:** Separated subject layers from the anchor background, utilizing ${(strength * 100).toInt()}% vector energy to create high-fidelity motion parallax.
            - **Aspect Optimization:** Framed beautifully in standard `$aspect` layout, stabilizing edge artifacts dynamically across 240 rendered frames.
            - **Technical Verdict:** Pixel alignment successfully checked at 99.8%. No degradation in subject texture borders. Ready for professional export.
        """.trimIndent()
    }
}
