#ifndef RAINTEXTCORE_AES_H
#define RAINTEXTCORE_AES_H

#include <iostream>
#include <random>
#include <vector>
#include "utils/i_cipher.h"

namespace rain_text_core {

class Aes : public ICipher{
  const uint8_t cypher_index_;
  uint8_t* key_index_;
  uint8_t* init_vector_index_;
  uint8_t* pre_salt_index_;
  uint8_t init_vector_[16];
  std::random_device random_;
  std::vector<uint8_t> key_;
  std::vector<uint8_t> text_;
  std::vector<std::vector<uint8_t>> splited_keys_;

 public:
  explicit Aes(uint8_t cypher_index, const std::vector<uint8_t>& key,
               const std::vector<uint8_t>& text);
  ~Aes() override;
  void Encrypt(std::vector<uint8_t>& output) override;
  void Decrypt(std::vector<uint8_t>& output) override;
  [[nodiscard]] const std::vector<uint8_t>& GetKey() const override;
  void SetKey(const std::vector<uint8_t>& key) override;
  [[nodiscard]] const std::vector<uint8_t>& GetText() const override;
  void SetText(const std::vector<uint8_t>& text) override;

 private:
  void CreateInitVector();
  void ComputeInitVector();
};

}  // namespace rain_text_core

#endif  // RAINTEXTCORE_AES_H
