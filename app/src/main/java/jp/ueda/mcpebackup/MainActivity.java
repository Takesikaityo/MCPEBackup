package jp.ueda.mcpebackup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nispok.snackbar.SnackbarManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<String> songList = new ArrayList<String>();
    private ListView lv;
    private File[] files;
    CustomTabsIntent customTabsIntent;
    CustomTabsIntent.Builder intentBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String type = bundle.getString("type");
            assert type != null;
            if (type.equals("backup")) {
                Log.d("[MCPEBackup]", "Starts MCPE APK Backup");
                PackageManager pm = this.getPackageManager();
                String versionName = "";
                try {
                    SnackbarManager.show(
                            com.nispok.snackbar.Snackbar.with(this)
                                    .text(R.string.starts_backup));
                    PackageInfo packageInfo = pm.getPackageInfo("com.mojang.minecraftpe", 0);
                    versionName = packageInfo.versionName;
                    ApplicationInfo appInfo = pm.getApplicationInfo("com.mojang.minecraftpe", 0);
                    String appFile = appInfo.sourceDir;
                    File src = new File(appFile);

                    String PATH = Environment.getExternalStorageDirectory() + "/MCPEBackups/";
                    File file = new File(PATH);
                    file.mkdirs();

                    File outputFile = new File(file, "MinecraftPE_" + versionName + ".apk");
                    FileChannel srcChannel = null;
                    FileChannel destChannel = null;
                    try {
                        srcChannel = new FileInputStream(src).getChannel();
                        destChannel = new FileOutputStream(outputFile).getChannel();
                        srcChannel.transferTo(0, srcChannel.size(), destChannel);
                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        if (srcChannel != null) {
                            try {
                                srcChannel.close();
                            } catch (IOException e) {
                            }
                        }
                        if (destChannel != null) {
                            try {
                                destChannel.close();
                            } catch (IOException e) {
                            }
                        }
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                SnackbarManager.show(
                        com.nispok.snackbar.Snackbar.with(this)
                                .text(R.string.done_backup));
                Log.d("[MCPEBackup]", "Done!");
            }
        }

        // Initialize intentBuilder
        intentBuilder = new CustomTabsIntent.Builder();

        // Set toolbar(tab) color of your chrome browser
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Define entry and exit animation
        intentBuilder.setExitAnimations(this, R.anim.right_to_left_end, R.anim.left_to_right_end);
        intentBuilder.setStartAnimations(this, R.anim.left_to_right_start, R.anim.right_to_left_start);
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // build it by setting up all
        customTabsIntent = intentBuilder.build();


        String sdPath = Environment.getExternalStorageDirectory() + "/MCPEBackups/";
        files = new File(sdPath).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".apk")) {
                    songList.add(files[i].getName());
                }
            }

            lv = (ListView) findViewById(R.id.listview);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, songList);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    String item = (String) listView.getItemAtPosition(position);
                    try {
                        Toast.makeText(getApplicationContext(),R.string.show,Toast.LENGTH_LONG).show();
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.fromParts("package", "com.mojang.minecraftpe", null);
                    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                    startActivity(intent);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String fileName = Environment.getExternalStorageDirectory() + "/MCPEBackups/" + item;
                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                    startActivity(intent1);
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setAction("android.intent.category.LAUNCHER");
                    intent.setClassName("com.mojang.minecraftpe", "com.mojang.minecraftpe.MainActivity");
                    startActivity(intent);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    // permissionが許可されていません
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        // 許可ダイアログで今後表示しないにチェックされていない場合
                    }
                    // permissionを許可してほしい理由の表示など
                    // 許可ダイアログの表示
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTSはアプリ内で独自定義したrequestCodeの値
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    return;
                }
            }
        }
    }

    public void download() {
        try {
            // URL設定
            URL url = new URL("https://drive.google.com/uc?id=0Bxp5wIuQibuSdU1BOWl0QUFVR0U");
            // HTTP接続開始
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            // SDカードの設定
            String PATH = Environment.getExternalStorageDirectory() + "/MCPEBackup/tmp/";
            File file = new File(PATH);
            file.mkdirs();

            // テンポラリファイルの設定
            File outputFile = new File(file, "MCPEBackup.apk");
            FileOutputStream fos = new FileOutputStream(outputFile);
            // ダウンロード開始
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            // Intent生成
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // MIME type設定
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/MCPEBackup/tmp/" + "MCPEBackup.apk")), "application/vnd.android.package-archive");
            // Intent発行
            startActivity(intent);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    download();
                }
            }).start();
        }
        if (id == R.id.about) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.app_name) + BuildConfig.VERSION_NAME );
            alertDialog.setIcon(R.mipmap.icon);
            alertDialog.setMessage(getString(R.string.author) + "\n\n" +
                    getString(R.string.email) + "\n\n" +
                    getString(R.string.github_link) +  "\n\n" +
                    getString(R.string.date) + "\n");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return true;
        }
        if (id == R.id.developer) {
            startActivity(new Intent(this, DeveloperActivity.class));
            return true;
        }
        if (id == R.id.action_help) {
            customTabsIntent.launchUrl(this, Uri.parse("https://sites.google.com/view/mcpebackup/%E3%83%98%E3%83%AB%E3%83%97"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
