package com.litbig.spotify.ui.models

import com.litbig.spotify.core.domain.model.remote.UserProfile

data class OwnerUiModel(
    override val id: String,
    override val imageUrl: String?,
    override val name: String,
    val follower: Int,
) : UiModel {
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
