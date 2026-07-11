package com.example.data

import kotlinx.coroutines.flow.Flow

class VideoProjectRepository(private val dao: VideoProjectDao) {
    val allProjects: Flow<List<VideoProject>> = dao.getAllProjects()

    fun getProjectById(id: Long): Flow<VideoProject?> = dao.getProjectById(id)

    suspend fun insertProject(project: VideoProject): Long = dao.insertProject(project)

    suspend fun updateProject(project: VideoProject) = dao.updateProject(project)

    suspend fun deleteProject(project: VideoProject) = dao.deleteProject(project)

    suspend fun deleteProjectById(id: Long) = dao.deleteProjectById(id)
}
