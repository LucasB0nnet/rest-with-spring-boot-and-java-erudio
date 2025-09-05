package br.com.erudio.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QrCodeService {

	public InputStream generateQrCode(String url, int width, int height) throws Exception {
		QRCodeWriter codeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = codeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
		
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
}
