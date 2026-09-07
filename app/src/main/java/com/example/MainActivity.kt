package com.example

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

  private val fileChooserLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      val uris: Array<Uri>? =
        if (result.resultCode == RESULT_OK) {
          val data = result.data
          val singleUri = data?.data
          val clipData = data?.clipData
          if (clipData != null && clipData.itemCount > 0) {
            Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
          } else if (singleUri != null) {
            arrayOf(singleUri)
          } else {
            null
          }
        } else {
          null
        }
      fileUploadCallback?.onReceiveValue(uris)
      fileUploadCallback = null
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
          ImageSuiteWebView(
            onOpenFileChooser = { callback, fileChooserParams ->
              fileUploadCallback?.onReceiveValue(null)
              fileUploadCallback = callback
              val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
              }
              try {
                fileChooserLauncher.launch(intent)
                true
              } catch (e: Exception) {
                fileUploadCallback = null
                false
              }
            }
          )
        }
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ImageSuiteWebView(
  modifier: Modifier = Modifier,
  onOpenFileChooser: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams?) -> Boolean
) {
  AndroidView(
    factory = { context ->
      WebView(context).apply {
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          allowFileAccess = true
          allowContentAccess = true
          loadWithOverviewMode = true
          useWideViewPort = true
          builtInZoomControls = false
          displayZoomControls = false
          mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webChromeClient = object : WebChromeClient() {
          override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
          ): Boolean {
            return if (filePathCallback != null) {
              onOpenFileChooser(filePathCallback, fileChooserParams)
            } else {
              false
            }
          }
        }

        webViewClient = WebViewClient()
        loadUrl("file:///android_asset/index.html")
      }
    },
    modifier = modifier.fillMaxSize()
  )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
