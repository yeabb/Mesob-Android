package com.example.mesob


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.BarcodeCallback
import com.google.zxing.ResultPoint

class QRCodeScanner : Fragment() {

    private val SCAN_QR_CODE_REQUEST_CODE = 100

    private lateinit var qrCodeScannerView: DecoratedBarcodeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_code_scanner, container, false)

        qrCodeScannerView = view.findViewById(R.id.zxing_barcode_scanner)


        val callback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                // Handle the scan result here.
                val text = result.text
                // 'text' contains the scanned QR code data.
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
                // Handle possible result points if needed.
            }
        }

        qrCodeScannerView.decodeContinuous(callback)
        qrCodeScannerView.resume()

        return view
    }

    override fun onResume() {
        super.onResume()
        qrCodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        qrCodeScannerView.pause()
    }
}
