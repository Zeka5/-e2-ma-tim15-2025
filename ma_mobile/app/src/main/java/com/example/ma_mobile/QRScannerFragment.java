package com.example.ma_mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ma_mobile.models.FriendRequest;
import com.example.ma_mobile.repository.FriendRepository;
import com.example.ma_mobile.utils.QRCodeGenerator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRScannerFragment extends Fragment {

    private static final String TAG = "QRScannerFragment";
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private DecoratedBarcodeView barcodeView;
    private FriendRepository friendRepository;
    private boolean isScanning = true;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    public static QRScannerFragment newInstance() {
        return new QRScannerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendRepository = new FriendRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scanner, container, false);
        barcodeView = view.findViewById(R.id.barcode_scanner);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (checkCameraPermission()) {
            startScanning();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                showToast("Camera permission is required to scan QR codes");
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        }
    }

    private void startScanning() {
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null && result.getText() != null && isScanning) {
                    handleQRCodeScanned(result.getText());
                }
            }
        });
    }

    private void handleQRCodeScanned(String qrContent) {
        isScanning = false; // Prevent multiple scans

        Log.d(TAG, "QR Code scanned: " + qrContent);

        Long userId = QRCodeGenerator.parseUserIdFromQRCode(qrContent);
        if (userId != null) {
            sendFriendRequest(userId);
        } else {
            showToast("Invalid QR code");
            isScanning = true; // Allow scanning again
        }
    }

    private void sendFriendRequest(Long userId) {
        friendRepository.sendFriendRequest(userId, new FriendRepository.FriendRequestCallback() {
            @Override
            public void onSuccess(FriendRequest friendRequest) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast("Friend request sent successfully!");
                        // Go back to previous screen
                        getActivity().getSupportFragmentManager().popBackStack();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showToast(error);

                        // Go back if already friends or request exists, allow retry for other errors
                        String lowerError = error.toLowerCase();
                        if (lowerError.contains("already") ||
                            lowerError.contains("pending") ||
                            lowerError.contains("exists") ||
                            lowerError.contains("friend")) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            isScanning = true; // Allow scanning again for other errors
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
