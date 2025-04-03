package com.cook.cookapp.global.util;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cook.cookapp.apiPayload.code.exception.GeneralException;
import com.cook.cookapp.apiPayload.code.status.ErrorStatus;
import com.cook.cookapp.post.repository.PostRepository;
import com.cook.cookapp.recipe.entity.Recipe;
import com.cook.cookapp.recipe.entity.RecipeImage;
import com.cook.cookapp.recipe.repository.RecipeImageRepository;
import com.cook.cookapp.recipe.repository.RecipeRepository;
import com.cook.cookapp.user.entity.ProfileImage;
import com.cook.cookapp.user.entity.User;
import com.cook.cookapp.user.repository.ProfileImageRepository;
import com.cook.cookapp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Util {

    private final AmazonS3 amazonS3Client;
    private final AmazonS3 amazonS3;
    private final ProfileImageRepository profileImageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private final RecipeImageRepository recipeImageRepository;
    private final RecipeRepository recipeRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.path.profile}")
    private String profilePath;

    @Value("${cloud.aws.s3.path.recipe}")
    private String recipePath;

//    @Value("${cloud.aws.s3.path.post}")
//    private String postPath;

    //프로필 이미지 업로드
    //db에 있는걸 먼저 찾고 s3를 삭제한 후 디비 데이터를 삭제해주시면 됩니다!
    @Transactional
    public String profileImageUpload(MultipartFile multipartFile, Long userId) throws IOException {

        String contentType = multipartFile.getContentType();
        //용량 5MB이하만 받도록 제한
        if(multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
        }

        //이미지 파일만 받도록 제한
        if(contentType == null || !contentType.startsWith("image/") ){
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        } else {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            ProfileImage existingProfileImage = profileImageRepository.findByUser(user);

            // 기존 이미지 삭제
            if (existingProfileImage != null) {
                user.setProfileImage(null);
                userRepository.save(user);
                String existingKey = profilePath + "/" + existingProfileImage.getUuid() + "_" + existingProfileImage.getOriginalFilename();
                amazonS3Client.deleteObject(bucket, existingKey);  // S3에서 삭제
                profileImageRepository.delete(existingProfileImage);  // DB에서 삭제
                profileImageRepository.flush();//즉시 DB에 반영
            }


            // 새 이미지 업로드
            String uuid = UUID.randomUUID().toString();
            String key = profilePath + "/" + uuid + "_" + multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            // S3에 업로드
            amazonS3Client.putObject(bucket, key, multipartFile.getInputStream(), metadata);

            // DB에 새 프로필 이미지 정보 저장
            ProfileImage newProfileImage = ProfileImage.builder()
                    .uuid(uuid)
                    .originalFilename(multipartFile.getOriginalFilename())
                    .contentType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .user(user)
                    .build();

            user.setProfileImage(newProfileImage);
            userRepository.save(user);
            profileImageRepository.save(newProfileImage);

            return amazonS3Client.getUrl(bucket, key).toString();
        }
    }

    @Transactional
    public String recipeImageUpload(MultipartFile multipartFile,Long recipeId, Long userId) throws IOException {

        String contentType = multipartFile.getContentType();
        //용량 5MB이하만 받도록 제한
        if(multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
        }

        //이미지 파일만 받도록 제한
        if(contentType == null || !contentType.startsWith("image/") ){
            throw new GeneralException(ErrorStatus.INVALID_FILE_TYPE);
        } else {

            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));

            RecipeImage existingRecipeImage = recipeImageRepository.findByRecipe(recipe);

            // 기존 이미지 삭제
            if (existingRecipeImage != null) {
                recipe.setRecipeImage(null);
                recipeRepository.save(recipe);
                String existingKey = recipePath + "/" + existingRecipeImage.getUuid() + "_" + existingRecipeImage.getOriginalFilename();
                amazonS3Client.deleteObject(bucket, existingKey);  // S3에서 삭제
            }


            // 새 이미지 업로드
            String uuid = UUID.randomUUID().toString();
            String key = recipePath + "/" + uuid + "_" + multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            // S3에 업로드
            amazonS3Client.putObject(bucket, key, multipartFile.getInputStream(), metadata);

            RecipeImage newRecipeImage = RecipeImage.builder()
                    .uuid(uuid)
                    .originalFilename(multipartFile.getOriginalFilename())
                    .contentType(multipartFile.getContentType())
                    .fileSize(multipartFile.getSize())
                    .recipe(recipe)
                    .build();

            recipe.setRecipeImage(newRecipeImage);
            recipeRepository.save(recipe);
            recipeImageRepository.save(newRecipeImage);

            return amazonS3Client.getUrl(bucket, key).toString();
        }
    }

    //프로필 이미지 url 가져오기
    public String getProfilePath(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
        ProfileImage profileImage = profileImageRepository.findByUser(user);
        if (profileImage == null) {
            return null;
        }
        return amazonS3.getUrl(bucket, profilePath + "/" + profileImage.getUuid() + "_" + profileImage.getOriginalFilename()).toString();
    }

    //레시피 이미지 url 가져오기
    public String getRecipePath(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new GeneralException(ErrorStatus.RECIPE_NOT_FOUND));

        RecipeImage recipeImage = recipeImageRepository.findByRecipe(recipe);
        if(recipeImage == null){
            return null;
        }

        return amazonS3.getUrl(bucket, recipePath + "/" + recipeImage.getUuid() + "_" + recipeImage.getOriginalFilename()).toString();
    }

    //    // MultipartFile 을 전달받아 File 로 전환한 후 S3에 업로드
    public String upload(MultipartFile multipartFile, String path, ProfileImage uuid) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        try {
            return upload(uploadFile, path, uuid);
        } finally {
            removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        }
    }

    private String upload(File uploadFile, String path, ProfileImage profileImage) {
        String fileName = generateKeyName(path, profileImage);
        return putS3(uploadFile, fileName);      // 업로드된 파일의 S3 URL 주소 반환
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.exists() && !targetFile.delete()) {
            log.error("파일이 삭제되지 못했습니다: {}", targetFile.getAbsolutePath());
            throw new RuntimeException("파일 삭제 실패: " + targetFile.getAbsolutePath());
        }
    }

    public void deleteFile(String targetFileName) {
        targetFileName = targetFileName.substring(46);
        log.info("targetFileUrl {} : ", targetFileName);
        amazonS3Client.deleteObject(bucket, targetFileName);
    }


    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + Objects.requireNonNull(file.getOriginalFilename()));
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            } catch (IOException e) {
                log.error("파일 변환 중 오류 발생: {}", e.getMessage());
                throw e;
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public String generateKeyName(String path, ProfileImage profileImage) {
        return profilePath + '/' + profileImage.getUuid();
    }

    public void deleteRecipeImage(String imageUrl) {
        // URL에서 S3 Key 추출
        String fileKey = extractFileKeyFromUrl(imageUrl);

        try {
            // 한글이 포함된 경우 URL 디코딩 적용 (예외 처리 추가)
            fileKey = URLDecoder.decode(fileKey, StandardCharsets.UTF_8.name());
            System.out.println("🗑 삭제할 S3 파일 경로 (디코딩 적용): " + fileKey);

            // S3에서 삭제
            amazonS3Client.deleteObject(bucket, fileKey);
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE_URL);
        }
    }

    // URL에서 파일 경로만 추출하는 메서드 추가
    private String extractFileKeyFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return url.getPath().substring(1); // '/' 제거한 경로 반환
        } catch (MalformedURLException e) {
            throw new GeneralException(ErrorStatus.INVALID_IMAGE_URL);
        }
    }


}
