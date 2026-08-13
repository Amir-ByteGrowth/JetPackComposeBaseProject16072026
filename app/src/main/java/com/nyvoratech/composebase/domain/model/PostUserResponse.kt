package com.nyvoratech.composebase.domain.model

import com.nyvoratech.composebase.ui.postusers.data.modeldto.PostUserDto
import kotlinx.serialization.Serializable

@Serializable
data class PostUserResponse(var postUserList: List<PostUserDto>)