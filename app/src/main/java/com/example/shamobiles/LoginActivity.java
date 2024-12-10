package com.example.shamobiles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize default users
        initializeDefaultUsers();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void initializeDefaultUsers() {
        // Create default users if they don't exist
        createUserIfNotExists("jahangir@shamobiles.com", "Owner@786", "owner");
        createUserIfNotExists("javeed@shamobiles.com", "Manager@786", "manager");
        createUserIfNotExists("elumalai@shamobiles.com", "Mechanic@1", "mechanic1");
        createUserIfNotExists("nizar@shamobiles.com", "Mechanic@2", "mechanic2");
    }

    private void createUserIfNotExists(String email, String password, String role) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(signInMethodQueryResult -> {
                    if (signInMethodQueryResult.getSignInMethods().isEmpty()) {
                        // User doesn't exist, create new user
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    String uid = authResult.getUser().getUid();
                                    
                                    // Create user document in Firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("role", role);
                                    user.put("username", email.split("@")[0]);
                                    
                                    db.collection("users")
                                            .document(uid)
                                            .set(user)
                                            .addOnSuccessListener(aVoid -> 
                                                Log.d("LoginActivity", "User created: " + email)
                                            )
                                            .addOnFailureListener(e -> 
                                                Log.e("LoginActivity", "Error creating user document", e)
                                            );
                                })
                                .addOnFailureListener(e -> 
                                    Log.e("LoginActivity", "Error creating user", e)
                                );
                    }
                });
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add @shamobiles.com if email doesn't contain @
        if (!email.contains("@")) {
            email = email + "@shamobiles.com";
        }

        final String finalEmail = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    
                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(document -> {
                                String role = document.getString("role");
                                if (role != null) {
                                    navigateBasedOnRole(role);
                                } else {
                                    Toast.makeText(this, "Error: User role not found", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error getting user role", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            });
                })
                .addOnFailureListener(e -> {
                    String errorMessage = "Login failed: ";
                    if (e.getMessage().contains("password is invalid")) {
                        errorMessage += "Invalid password";
                    } else if (e.getMessage().contains("no user record")) {
                        errorMessage += "User not found: " + finalEmail;
                    } else {
                        errorMessage += e.getMessage();
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        switch (role.toLowerCase()) {
            case "owner":
                intent = new Intent(this, OwnerDashboardActivity.class);
                break;
            case "manager":
                intent = new Intent(this, ManagerDashboardActivity.class);
                break;
            case "mechanic1":
            case "mechanic2":
                intent = new Intent(this, MechanicDashboardActivity.class);
                break;
            default:
                Toast.makeText(this, "Invalid user role", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                return;
        }
        startActivity(intent);
        finish();
    }
} 