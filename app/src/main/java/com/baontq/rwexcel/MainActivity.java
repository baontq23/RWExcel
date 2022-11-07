package com.baontq.rwexcel;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.gson.JsonObject;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private RecyclerView rvStudents;

    private StudentAdapter studentAdapter;
    private ArrayList<Parent> parentList;
    private List<Student> studentList;
    private Map<String, Parent> parentMap = new LinkedHashMap<>();
    private ProgressDialog progressDialog;
    private Button btnExport, btnUpload, btnUpdate, btnImport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_main);
        rvStudents = findViewById(R.id.rv_students);
        btnExport = findViewById(R.id.btn_export);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpload = findViewById(R.id.btn_upload);
        btnImport = findViewById(R.id.btn_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                launcher.launch(Intent.createChooser(intent, "Choose File excel.."));
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                launcher3.launch(Intent.createChooser(intent, "Choose File excel.."));
            }
        });
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                intent.putExtra(Intent.EXTRA_TITLE, "export.xlsx");

                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
                launcher2.launch(intent);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentList == null || studentList == null || parentList.size() == 0 || studentList.size() == 0) {
                    Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
                    return;
                }
                SyncBody body = new SyncBody();
                body.classroom_id = "1";
                body.students = studentList;
                body.parents = parentList;
                Call<JsonObject> call = ServerAPI.getInstance().create(APIService.class).upload(body);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    ActivityResultLauncher<Intent> launcher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }

                String filePath = new FileUtils(MainActivity.this).getPath(result.getData().getData());
                Log.i("TAG", "Selected File Path: " + filePath);
                new StudentScoreReader(filePath, new StudentScoreReader.HandleOnComplete() {
                    @Override
                    public void onComplete(List<StudentDetail> studentDetails) {
                        StudentDetailAdapter studentDetailAdapter = new StudentDetailAdapter(studentDetails);
                        rvStudents.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
                        rvStudents.setAdapter(studentDetailAdapter);
                        Toast.makeText(MainActivity.this, "Done: " + studentDetails.size(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();
            }
        }
    });
    ActivityResultLauncher<Intent> launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }
                progressDialog.setTitle("Loading");
                progressDialog.show();
                List<StudentDetail> list = new ArrayList<>();
                list.add(new StudentDetail(1, 1, "sdt1", "Bao Nguyen Nam", 1));
                list.add(new StudentDetail(2, 1, "sdt1", "Bao Nguyen Nam", 2));
                list.add(new StudentDetail(3, 1, "sdt2", "Thế Quang", 1));
                list.add(new StudentDetail(4, 1, "sdt2", "Thế Quang", 2));
                String filePath = new FileUtils(MainActivity.this).getPath(result.getData().getData());
                new ScoreFileUtils(filePath, list, new ScoreFileUtils.addOnCompleteListener() {
                    @Override
                    public void onComplete(Uri export) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(export, "*/*");
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).exportScoreInputFile();

            }
        }
    });

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    //no data present
                    return;
                }

                String filePath = new FileUtils(MainActivity.this).getPath(result.getData().getData());
                Log.i("TAG", "Selected File Path: " + filePath);
                progressDialog.setTitle("Loading");
                progressDialog.show();
                new StudentListReader(filePath, new StudentListReader.HandleOnComplete() {
                    @Override
                    public void onComplete(List<Student> students, Map<String, Parent> parentMapResult) {
                        parentList = new ArrayList<>(parentMapResult.values());
                        studentList = students;
                        studentAdapter = new StudentAdapter(students, MainActivity.this);
                        rvStudents.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
                        rvStudents.setAdapter(studentAdapter);
                        parentMap = parentMapResult;
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Parent count: " + parentMap.size(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    public void addOnClickListener(Student student) {
        Toast.makeText(this, "Parent name: " + parentMap.get(student.getParentId()).getName(), Toast.LENGTH_SHORT).show();
    }
}