package com.wnp.imagesearch.imagesList

import com.wnp.imagesearch.relatedImagesList.RelatedImagesListAdapter

class Image(
    val imageUrl: String,
    val description: String,
    val siteUrl: String,
    val thumbnailUrl: String,
    val width: Int,
    val height: Int
) {
    var isRelatedOpened = false
    var relatedImagesListAdapter: RelatedImagesListAdapter? = null
}
