package com.hernandez.studentinfosystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etStudentNumber, etLastName, etFirstName, etMiddleName, etProgram, etSpecialization, etYearLevel;
    private ListView lvStudents;
    private List<String> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Layouts
        LinearLayout layoutLogin = findViewById(R.id.layoutLogin);
        LinearLayout layoutSignup = findViewById(R.id.layoutSignup);
        LinearLayout layoutDashboard = findViewById(R.id.layoutDashboard);

        // Login Views
        EditText etLoginUsername = findViewById(R.id.etLoginUsername);
        EditText etLoginPassword = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToSignup = findViewById(R.id.tvGoToSignup);

        // Signup Views
        EditText etSignupUsername = findViewById(R.id.etSignupUsername);
        EditText etSignupPassword = findViewById(R.id.etSignupPassword);
        EditText etSignupConfirm = findViewById(R.id.etSignupConfirm);
        Button btnSignup = findViewById(R.id.btnSignup);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Dashboard Views
        Button btnLogout = findViewById(R.id.btnLogout);
        etStudentNumber = findViewById(R.id.etStudentNumber);
        etLastName = findViewById(R.id.etLastName);
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etProgram = findViewById(R.id.etProgram);
        etSpecialization = findViewById(R.id.etSpecialization);
        etYearLevel = findViewById(R.id.etYearLevel);
        Button btnSaveStudent = findViewById(R.id.btnSaveStudent);
        lvStudents = findViewById(R.id.lvStudents);

        // Switch to Signup
        tvGoToSignup.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutSignup.setVisibility(View.VISIBLE);
        });

        // Switch to Login
        tvGoToLogin.setOnClickListener(v -> {
            layoutSignup.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        });

        // Signup Action
        btnSignup.setOnClickListener(v -> {
            String user = etSignupUsername.getText().toString().trim();
            String pass = etSignupPassword.getText().toString();
            String confirm = etSignupConfirm.getText().toString();

            if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dbHelper.registerUser(user, pass)) {
                Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_SHORT).show();
                etSignupUsername.setText("");
                etSignupPassword.setText("");
                etSignupConfirm.setText("");
                layoutSignup.setVisibility(View.GONE);
                layoutLogin.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        });

        // Login Action
        btnLogin.setOnClickListener(v -> {
            String user = etLoginUsername.getText().toString().trim();
            String pass = etLoginPassword.getText().toString();

            if (dbHelper.checkUser(user, pass)) {
                layoutLogin.setVisibility(View.GONE);
                layoutDashboard.setVisibility(View.VISIBLE);
                etLoginUsername.setText("");
                etLoginPassword.setText("");
                loadStudents();
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Logout Action
        btnLogout.setOnClickListener(v -> {
            layoutDashboard.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        });

        // Save Student Action
        btnSaveStudent.setOnClickListener(v -> {
            String studNum = etStudentNumber.getText().toString().trim();
            String lName = etLastName.getText().toString().trim();
            String fName = etFirstName.getText().toString().trim();
            String mName = etMiddleName.getText().toString().trim();
            String prog = etProgram.getText().toString().trim();
            String spec = etSpecialization.getText().toString().trim();
            String year = etYearLevel.getText().toString().trim();

            if (studNum.isEmpty() || lName.isEmpty() || fName.isEmpty() || prog.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "Please fill in all required (*) fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.insertStudent(studNum, lName, fName, mName, prog, spec, year)) {
                Toast.makeText(this, "Student saved!", Toast.LENGTH_SHORT).show();
                clearFields();
                loadStudents();
            } else {
                Toast.makeText(this, "Error saving student", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Student on Tap
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = studentList.get(position);
            String studNum = selectedItem.split("ID: ")[1].split("  \\|")[0];

            new AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage("Delete student " + studNum + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteStudentByNumber(studNum);
                        loadStudents();
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadStudents() {
        studentList = dbHelper.getAllStudentsFormatted();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        lvStudents.setAdapter(adapter);
    }

    private void clearFields() {
        etStudentNumber.setText("");
        etLastName.setText("");
        etFirstName.setText("");
        etMiddleName.setText("");
        etProgram.setText("");
        etSpecialization.setText("");
        etYearLevel.setText("");
    }
}
