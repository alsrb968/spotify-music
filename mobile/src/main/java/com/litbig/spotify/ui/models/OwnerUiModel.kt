package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.UserProfile

data class OwnerUiModel(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val follower: Int,
) {
    companion object {
        @JvmStatic
        fun from(userProfile: UserProfile): OwnerUiModel {
            return OwnerUiModel(
                id = userProfile.id,
                imageUrl = userProfile.images.firstOrNull()?.url,
                name = userProfile.displayName ?: "",
                follower = userProfile.followers.total,
            )
        }
    }
}
