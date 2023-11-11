#include <jni.h>
#include <vector>
#include "core/rain_text_core.h"
#include "Argon2/Argon2.h"
#include "scrypt/libscrypt.h"
#include "sha3/sha3.h"
#include <android/log.h>
#include <sstream>
#include <string>

#define LOG_TAG "MyTag"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))


std::vector<uint8_t> makeKey(std::vector<uint8_t>& token, std::vector<uint8_t>& identifier);
std::vector<uint8_t> jbyteArray2Vector(JNIEnv *env, jbyteArray& array);

std::string vectorToString(const std::vector<uint8_t>& vec) {
    if (vec.empty()) {
        return "";
    }

    std::ostringstream oss;
    for (size_t i = 0; i < vec.size() - 1; ++i) {
        oss << static_cast<int>(vec[i]) << ", ";
    }

    oss << static_cast<int>(vec.back());

    return oss.str();
}


extern "C" JNIEXPORT jbyteArray JNICALL
Java_cz_kureii_raintext_model_EncryptionManager_nativeEncrypt(JNIEnv *env, jobject thiz, jbyteArray token, jbyteArray identifier, jbyteArray plainText, jint iterations) {
    auto tokenVector = jbyteArray2Vector(env, token);
    auto identifierVector= jbyteArray2Vector(env, identifier);
    auto plainTextVector = jbyteArray2Vector(env, plainText);

    auto key = makeKey(tokenVector, identifierVector);

    auto rainTextCore = new rain_text_core::RainTextCore(static_cast<uint16_t>(iterations), key, plainTextVector);

    std::vector<uint8_t> output;
    rainTextCore->Encrypt(output);

    //LOGI("EncriptedData: %s", vectorToString(output).data());

    delete rainTextCore;

    jbyteArray result = env->NewByteArray(static_cast<jsize>(output.size()));

    env->SetByteArrayRegion(result, 0, static_cast<jsize>(output.size()), reinterpret_cast<const jbyte*>(output.data()));

    return result;

}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_cz_kureii_raintext_model_EncryptionManager_nativeDecrypt(JNIEnv *env, jobject thiz, jbyteArray token, jbyteArray identifier, jbyteArray encryptedText, jint iterations) {
    auto tokenVector = jbyteArray2Vector(env, token);
    auto identifierVector= jbyteArray2Vector(env, identifier);
    auto encryptedTextVector = jbyteArray2Vector(env, encryptedText);

    auto key = makeKey(tokenVector, identifierVector);
//    LOGI("EncriptedData: %s", vectorToString(encryptedTextVector).data());
//    LOGI("EncriptedData size: %d", encryptedTextVector.size());

    auto rainTextCore = new rain_text_core::RainTextCore(static_cast<uint16_t>(iterations), key, encryptedTextVector);

    std::vector<uint8_t> output;
    rainTextCore->Decrypt(output);
    delete rainTextCore;

    jbyteArray result = env->NewByteArray(static_cast<jsize>(output.size()));

    env->SetByteArrayRegion(result, 0, static_cast<jsize>(output.size()), reinterpret_cast<const jbyte*>(output.data()));

    return result;
}

std::vector<uint8_t> makeKey(std::vector<uint8_t>& token, std::vector<uint8_t>& identifier) {
    uint8_t pre_salt[64];
    sha3_HashBuffer(512, SHA3_FLAGS_NONE, token.data(), token.size(),
                    pre_salt, 64);

    uint8_t salt[64];
    std::vector<uint8_t> data;
    data.insert(data.end(), token.begin(), token.end());
    data.push_back(0x1C);
    data.insert(data.end(), identifier.begin(), identifier.end());

    libscrypt_scrypt(reinterpret_cast<const uint8_t*>(data.data()), data.size(),
                     pre_salt, 64, 131072, 16, 1, salt, 64);
    auto vectorSalt = std::vector<uint8_t>(salt, salt+64);
    return Argon2::Argon2id(token, vectorSalt, 4, 1 << 18, 4, 2048);
}

std::vector<uint8_t> jbyteArray2Vector(JNIEnv *env, jbyteArray& array) {
    jbyte *arrayBody = env->GetByteArrayElements(array, nullptr);
    jsize arraySize = env->GetArrayLength(array);
    std::vector<uint8_t> arrayVector(arrayBody, arrayBody + arraySize);
    env->ReleaseByteArrayElements(array, arrayBody, 0);
    return  arrayVector;
}
