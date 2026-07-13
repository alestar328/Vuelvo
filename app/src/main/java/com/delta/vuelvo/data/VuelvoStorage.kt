package com.delta.vuelvo.data

/**
 * Resolves the Firebase Storage download URL for a logo/cover image referenced by an NFC tag's
 * deeplink. The comercio apps upload images as flat objects named `{uuid}_logo.jpg` / `{uuid}_cover.jpg`
 * at the bucket root (see vuelvo_commerce's `ImageUploadRepository`) and write only the reference
 * (without extension, e.g. `"ABCDE12345_logo"`) to the tag — this rebuilds the public download URL from it.
 */
object VuelvoStorage {
    /** Bucket shared by both comercio apps (iOS and Android write to the same Firebase project). */
    private const val BUCKET = "vuelvocommerce.firebasestorage.app"

    /** Builds the public download URL for an object reference written to an NFC tag. Storage's read
     * rules must allow public access to these objects. */
    fun downloadUrl(objectReference: String): String =
        "https://firebasestorage.googleapis.com/v0/b/$BUCKET/o/$objectReference.jpg?alt=media"
}
