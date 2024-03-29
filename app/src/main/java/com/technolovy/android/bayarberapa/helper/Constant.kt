package com.technolovy.android.bayarberapa.helper

import com.technolovy.android.bayarberapa.BuildConfig

object Constant {
    const val AppsVersion = BuildConfig.VERSION_NAME

}

object TrackerEvent {
    const val openAboutApps = "uploadInvoicePage_aboutapps"
    const val chooseImageOnUploadInvoice = "uploadInvoicePage_chooseImage"
    const val deleteImageOnUploadInvoice = "uploadInvoicePage_deleteImage"
    const val processImageOnUploadInvoice = "uploadInvoicePage_processImage"
    const val changeItemNameOnPreviewPage = "previewPage_changeItemName"
    const val changePriceTextOnPreviewPage = "previewPage_changePriceText"
    const val changeQtyOnPreviewPage = "previewPage_changeQty"
    const val changeTypeOnPreviewPage = "previewPage_changeType"
    const val deleteItemonPreviewPage = "previewPage_deleteitem"
    const val addItemonPreviewPage = "previewPage_addItem"
    const val seeInvoiceOnPreviewPage = "previewPage_seeInvoiceImage"
    const val seeInformationPageonPreviewPage = "previewPage_seeInformationPage"
    const val calculateOnPreviewPage = "previewPage_calculate"
    const val tagFriendsOnResultPage = "resultPage_tagFriends"
    const val addRecipeintOnRecipientListPage = "recipientListPage_addRecipient"
    const val editRecipeintOnRecipientListPage = "recipientListPage_editRecipient"
    const val saveRecipeintOnRecipeintFormPage = "recipientFormPage_saveRecipient"

    const val errorProcessImage = "uploadInvoicePage_errorFirebaseImage"
    const val errorVisionImage = "uploadInvoicePage_errorVisionFirebaseImage"
    const val errorRecognizeBrand = "uploadInvoicePage_errorRecognizedBrand"
    const val errorPermissionDenied = "uploadInvoicePage_errorPermissionDenied"
    const val adsShowOnUploadInvoicePage = "uploadingInvoicePage_adsShow"
    const val adsFailToLoadOnUploadInvoicePage = "uploadingInvoicePage_adsFailtoLoad"
    const val adsClickedOnUploadInvoicePage = "uploadingInvoicePage_adsClicked"
    const val adsImpressionOnUploadInvoicePage = "uploadingInvoicePage_adsImpression"
    const val adsLeftOnUploadInvoicePage = "uploadingInvoicePage_adsLeftApp"
    const val adsFailToLoadWhenClickPrrocessButtonOnUploadInvoicePage = "uploadingInvoicePage_adsFailtoLoadWhenClickProcess"

    const val adsShowOnInvoiceListResult = "invoiceListResult_adsShow"
    const val adsFailToLoadOnInvoiceListResult = "invoiceListResult_adsFailtoLoad"
    const val adsClickedOnInvoiceListResult = "invoiceListResult_adsClicked"
    const val adsImpressionOnInvoiceListResult = "invoiceListResult_adsImpression"
    const val adsLeftOnInvoiceListResult = "invoiceListResult_adsLeftApp"
    const val adsFailToLoadWhenClickTagFriendsOnInvoiceListResult = "invoiceListResult_adsFailtoLoadWhenClickTagFriends"

}