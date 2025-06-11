package com.clypt.clypt_backend.handler;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.clypt.clypt_backend.controller.AnonymousFileHandlerController;
import com.clypt.clypt_backend.entity.UrlMapping;
import com.clypt.clypt_backend.responses.CodeResponse;
import com.clypt.clypt_backend.services.UrlMappingService;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "file.handler", havingValue = "cloudinary")
public class CloudinaryFileHandler implements FileHandler {

	@Autowired
	private Cloudinary cloudinary;

	@Autowired
	private UrlMappingService urlMappingService;

	private static final Logger log = LoggerFactory.getLogger(AnonymousFileHandlerController.class);

	@Override
	public CodeResponse upload(MultipartFile[] multipartFiles, String folderName) {
		// utils needed while uploading the files.
		Map<Object, Object> utils = new HashMap<>();
		utils.put("folder", folderName);
		utils.put("use_filename", true);
		utils.put("unique_filename", false);
		utils.put("type", "upload");
		utils.put("resource_type", "raw");
		utils.put("access_mode", "public");

		// list to store the uploaded files urls.
		List<String> fileUrls = new ArrayList<>();
		String uniqueCode = generateUniqueCode();
		String fileExtension = "";
		List<String> fileType = new ArrayList<>();

		try {
			for (MultipartFile file : multipartFiles) {
				String fileNameWithExtension = file.getOriginalFilename();
				String fileNameWithoutExtension = getFileNameWithoutExtension(fileNameWithExtension);
//				if (fileExtension.length() == 0)
//					fileExtension = getFileExtension(fileNameWithExtension);
				fileType.add(getFileExtension(fileNameWithExtension));
				utils.put("public_id", fileNameWithoutExtension + uniqueCode);

				Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), utils);

				// getting url to access the file
				String url = (String) uploadResult.get("secure_url");
				log.info("File uploaded with name {}", fileNameWithExtension);
				fileUrls.add(url);

			}

			urlMappingService.save(uniqueCode, fileUrls, fileType);
			System.out.println("upload from Cloudin;aryFileHandler");
			return new CodeResponse(uniqueCode);

		} catch (IOException e) {
			log.error("Deleting due to error while uploading.");
			delete(uniqueCode, fileUrls);

		}
		return null;

	}

	@Override
	public void delete(String uniqueCode, List<String> fileUrls) {
		try {
			for (String url : fileUrls) {
				String publicId = extractPublicIdFromUrl(url);

				Map<Object, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

				if (!("ok".equals(result.get("result")))) {
					throw new Exception("Failed to delete file from Cloudinary: " + url);
				}
				log.info("File with url: {} deleted", url);
				System.out.println("delete from CloudinaryFileHandler");
			}

			try {
				// Fetch list of uploaded resources
				Map result = cloudinary.api().resources(ObjectUtils.emptyMap());

				// Print uploaded resources
				System.out.println("cloudinary uploads " + result);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("delete from cloudinaryFileHandler");
		} catch (Exception e) {
			log.error("\"Failed to delete files from Cloudinary:" + e);

		}

	}

	@Override
	public Path getFiles(String uniqueCode) {
		UrlMapping urlMapping = urlMappingService.get(uniqueCode);
		List<String> fileUrls = urlMapping.getUrls();

		if (fileUrls == null || fileUrls.isEmpty()) {
			throw new RuntimeException("No files found for the provided code: " + uniqueCode);
		}

		try {
			Path tempZip = Files.createTempFile("clypt-", ".zip");
			System.out.println("Created ZIP file at: " + tempZip.toAbsolutePath());

			try (ZipOutputStream zos = new ZipOutputStream(
					Files.newOutputStream(tempZip))) {
				for (String fileUrl : fileUrls) {
					try {
						System.out.println("Fetching: " + fileUrl);
						URL url = new URL(fileUrl);
						HttpURLConnection conn = (HttpURLConnection)url.openConnection();
						conn.setRequestMethod("GET");

						int code = conn.getResponseCode();
						System.out.println("HTTP response: " + code);

						if (code != 200) {
							System.out.println("Skipping file due to bad response: " + fileUrl);
							continue;
						}

						String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
						fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
						System.out.println("File name to write: " + fileName);

						try (InputStream in = conn.getInputStream()) {
							zos.putNextEntry(new ZipEntry(fileName));
							byte[] buffer = new byte[4096];
							int bytesRead, totalBytes = 0;

							while ((bytesRead = in.read(buffer)) != -1) {
								zos.write(buffer, 0, bytesRead);
								totalBytes += bytesRead;
							}

							zos.closeEntry();
							System.out.println("Wrote " + totalBytes + " bytes to ZIP entry.");
						}

					} catch (Exception e) {
						System.err.println("Failed for URL: " + fileUrl);
						e.printStackTrace();
					}
				}
			}

			// Verify if zip has anything
			File zipFile = tempZip.toFile();
			System.out.println("ZIP file size: " + zipFile.length());
			if (zipFile.length() == 0) {
				System.err.println("ZIP is empty!");
			}

			return tempZip;

		} catch (IOException e) {
			throw new RuntimeException("Could not write ZIP", e);
		}
	}

	@Override
	public List<String> getFileType(String uniqueCode) {
		UrlMapping urlMapping = urlMappingService.get(uniqueCode);
		return urlMapping.getFileType();
	}

	// Helper method to extract file extension
	private String getFileExtension(String filename) {
		if (filename == null || filename.isEmpty()) {
			return "";
		}
		int lastDotIndex = filename.lastIndexOf('.');
		if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
			return "";
		}
		return filename.substring(lastDotIndex); // includes the dot (e.g., ".pdf")
	}

	private String extractPublicIdFromUrl(String secureUrl) {
		String[] parts = secureUrl.split("/");
		if (parts.length < 2) {
			log.error("Invalid Cloudinary URL: " + secureUrl);
		}

		String publicID = getPublicID(parts);
		System.out.println("extractPublicIdFromUrl from CloudinaryFileHandler");
		return publicID;
	}

	private String getPublicID(String[] parts) {
		String publicIdWithExtension = parts[parts.length - 1];
		String folderName = parts[parts.length - 2];

		int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
		String publicIdWithOutExtension = (lastDotIndex == -1) ? publicIdWithExtension
				: publicIdWithExtension.substring(0, lastDotIndex);
		System.out.println("getPublicID from CloudinaryFileHandler");
		return folderName + "/" + publicIdWithOutExtension;
	}

	private String generateUniqueCode() {
		System.out.println("generateUniqueCode from CloudinaryFileHandler");
		return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}

	private String getFileNameWithoutExtension(String originalFilename) {
		if (originalFilename != null && originalFilename.contains(".")) {
			originalFilename = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
		}
		System.out.println("getFileNameWithoutExtension from CloudinaryFileHandler");
		return originalFilename;
	}

}
