#ifndef RAINTEXTCORE_RAIN_TEXT_CORE_H
#define RAINTEXTCORE_RAIN_TEXT_CORE_H

#include <iostream>
#include <memory>
#include <random>
#include <vector>

#include "utils/i_cipher.h"

namespace rain_text_core {

enum CipherType { AES = 0, TWOFISH, CHACHA20, NUM_CIPHERS };

class RainTextCore {
  uint16_t iterations_;
  std::vector<uint8_t> key_;
  std::vector<uint8_t> text_;
  std::random_device random_;

 public:
  RainTextCore(uint16_t iterations, const std::vector<uint8_t>& key,
               const std::vector<uint8_t>& text);
  void Encrypt(std::vector<uint8_t>& output);
  void Decrypt(std::vector<uint8_t>& output);
  [[nodiscard]] const std::vector<uint8_t>& GetText() const;
  void SetText(const std::vector<uint8_t>& text);
#ifdef ENABLE_TESTS
#endif

 private:
  std::unique_ptr<rain_text_core::ICipher> CreateCipher(
      CipherType type, const std::vector<uint8_t>& text);
};

}  // namespace rain_text_core

#endif  // RAINTEXTCORE_RAIN_TEXT_CORE_H
