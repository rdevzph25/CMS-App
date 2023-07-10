package com.rdevzph.dpwhcms.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rdevzph.dpwhcms.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.material.bottomappbar.BottomAppBar;
import android.os.Message;
import android.view.WindowManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String TAG = WebView.class.getSimpleName();
    
    private ValueCallback<Uri[]> mUploadMessage;
    private String mCameraPhotoPath = null;
    private long size = 0;

    private int count;

    private BottomNavigationView bottomNavigationView;
    
    private BottomAppBar bottomappbar;

    private FloatingActionButton add_comp;
    
    private SwipeRefreshLayout refresh;
    
    private AlertDialog dialog;
    
    private AlertDialog loading;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));  
        
        webView = (WebView)findViewById(R.id.web_view);
        
        bottomappbar = (BottomAppBar) findViewById(R.id.bottomappbar);
        
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavigationbar);
        
        refresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        
        refresh.setColorScheme(R.color.colorPrimary);
        
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){

                @Override
                public void onRefresh() {
                    
                    webView.loadUrl(webView.getUrl());
                    
                    
                }

            
        });
        
        bottomNavigationView.setBackground(null);

        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 
                    switch (item.getItemId())
                    {
                        case R.id.mDashboard :
                            webView.loadUrl(Util.host + "users/dashboard.php");
                            break;
                        case R.id.mProfile :
                            webView.loadUrl(Util.host + "users/profile.php");
                            
                            break;

                        case R.id.mHistory :
                            webView.loadUrl(Util.host + "users/complaint-history.php");
                            
                            break;

                        case R.id.mLogout :
                            AlertDialog dialog = new AlertDialog.Builder(WebViewActivity.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dia, int which) {
                                        webView.loadUrl(Util.host + "users/logout.php");
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                            dialog.show();
                            
                            break;
                            


                    }

                    return true;
                }
            });
       
        add_comp = (FloatingActionButton) findViewById(R.id.add_complaint);
        
        add_comp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    
                    webView.loadUrl(Util.host + "users/register-complaint.php");
                    

                }
            });
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebViewClient(new PQClient());
        webView.setWebChromeClient(new PQChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setEnableSmoothTransition(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        
        //if SDK version is greater of 19 then activate hardware acceleration 
        //otherwise activate software acceleration
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        
        
        webView.loadUrl(Util.host+"index.php");

    }
    
    private void showLoading(String msg){
        View v = LayoutInflater.from(this).inflate(R.layout.loading, null);
        final TextView text = v.findViewById(R.id.loadingText);
        text.setText(msg);
        loading = new AlertDialog.Builder(this)
            .setView(v)
            .setCancelable(true)
            .create();

        loading.show();
	}
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || mUploadMessage == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        try {
            String file_path = mCameraPhotoPath.replace("file:", "");
            File file = new File(file_path);
            size = file.length();

        } catch (Exception e) {
            Log.e("Error!", "Error while opening image file" + 
                  e.getLocalizedMessage());
        }

        if (data != null || mCameraPhotoPath != null) {
            ClipData images = null;
            try {
                images = data.getClipData();
            } catch (Exception e) {
                Log.e("Error!", e.getLocalizedMessage());
            }

            if (images == null && data != null && data.getDataString() != null) {
                count = data.getDataString().length();
            } else if (images != null) {
                count = images.getItemCount();
            }
            Uri[] results = new Uri[count];
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (size != 0) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else if (data.getClipData() == null) {
                    results = new Uri[]{Uri.parse(data.getDataString())};
                } else {

                    for (int i = 0; i < images.getItemCount(); i++) {
                        results[i] = images.getItemAt(i).getUri();
                    }
                }
            }

            mUploadMessage.onReceiveValue(results);
            mUploadMessage = null;
        }
    }
    
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new 
                                                                          Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        return imageFile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up 
        //to the default
        // system behavior (probably exit the activity)

        return super.onKeyDown(keyCode, event);
    }
    
    public class PQChromeClient extends WebChromeClient {

        
        // For Android 5.0+
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> 
                                         filePath, WebChromeClient.FileChooserParams fileChooserParams) {                     
                                             
           // Double check that we don't have any existing callbacks
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePath;
            Log.e("FileCooserParams => ", filePath.toString());

            Intent takePictureIntent = new 
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
                                               Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new 
                Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[2];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(Intent.createChooser(chooserIntent, "Select Files"), 1);

            return true;

        }
        
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result){
            String title = "";
            if (message.contains("delete")){
                title = "Warning";
            }else{
                title = "Success";
            }
            AlertDialog dialog = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.confirm();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.cancel();

                    }
                })
                .create();
            dialog.show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result){
            String title = "";
            if (message.contains("delete")){
                title = "Warning";
            }else{
                title = "Success";
            }
            AlertDialog dialog = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.confirm();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.cancel();

                    }
                })
                .create();
            dialog.show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result){


            final EditText input = new EditText(view.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setText(defaultValue);
            String title = "";
            if (message.contains("delete")){
                title = "Warning";
            }else{
                title = "Success";
            }
            AlertDialog dialog = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.confirm(input.getText().toString());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {

                        result.cancel();

                    }
                })
                .create();
            dialog.show();
            return true;
        }
        
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(WebViewActivity.this);
            newWebView.clearCache(true);
            newWebView.setWebViewClient(new PQClient());
            newWebView.setWebChromeClient(new PQChromeClient());
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);
            
            dialog = new AlertDialog.Builder(WebViewActivity.this)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        
                        dialog.dismiss();
                        

                    }
                })
                .create();
            dialog.setView(newWebView);
            dialog.setCancelable(false);
            dialog.show();
            dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                
            
            
            // Other configuration comes here, such as setting the WebViewClient

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();
          
            return true;
        }

    }
    
    

    public class PQClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {

                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                // Here we can open new activity

                return true;

            } else {

                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }

        //Show loader on url load
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            
            
            if (url.contains("index")){
                bottomappbar.setVisibility(View.GONE);
                add_comp.setVisibility(View.GONE);
            }else if (url.contains("about")){
                bottomappbar.setVisibility(View.GONE);
                add_comp.setVisibility(View.GONE);
            }
            else if (url.contains("admin")){
                bottomappbar.setVisibility(View.GONE);
                add_comp.setVisibility(View.GONE);
            }else if (url.contains("officers")){
                bottomappbar.setVisibility(View.GONE);
                add_comp.setVisibility(View.GONE);
            }else{
                bottomappbar.setVisibility(View.VISIBLE);
                add_comp.setVisibility(View.VISIBLE);
            }

            // Then show progress Dialog
          
            if (loading == null) {
                showLoading("Loading, please wait...");
            }
        }

        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            
            if (refresh.isRefreshing()){
                refresh.setRefreshing(false);
            }
            
            webView.loadUrl("javascript:(function(){ " +
                            "document.getElementById('android-app').style.display ='none';})()");
            try {
                // Close progressDialog
                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                    loading = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        
        @Override 
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            
            String htmlData ="";
            
            webView.loadUrl("about:blank");
            webView.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
            webView.invalidate();
            AlertDialog errDialog = new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("Error")
                .setMessage("There was a problem connecting to the server, please try again later." + "(" + description + ")")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        
                        webView.loadUrl(Util.host+"index.php");

                    }
                })
                .create();
            errDialog.setCancelable(false);
            errDialog.show();
        }
        
    }

 
    @Override
    
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Standard back button implementation (for example this could close the app)
            finish();
        }
    }


}



