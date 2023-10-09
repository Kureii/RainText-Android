#ifndef RAINTEXTCORE_CHACHA20_H
#define RAINTEXTCORE_CHACHA20_H

#include <iostream>
#include <vector>
#include <random>
#include "utils/i_cipher.h"

namespace rain_text_core {


class ChaCha20 : public ICipher{
  const uint8_t cypher_index_;
  uint8_t* key_index_;
  uint8_t* init_vector_index_;
  uint8_t* pre_salt_index_;
  uint8_t init_vector_[8];
  std::random_device random_;
  std::vector<uint8_t> key_;
  std::vector<uint8_t> text_;
  std::vector<std::vector<uint8_t>> splited_keys_;

 public:
  explicit ChaCha20(uint8_t cypher_index, const std::vector<uint8_t>& key,
               const std::vector<uint8_t>& text);
  void Encrypt(std::vector<uint8_t>& output) override;
  void Decrypt(std::vector<uint8_t>& output) override;
  [[nodiscard]] const std::vector<uint8_t>& GetKey() const override;
  void SetKey(const std::vector<uint8_t>& key) override;
  [[nodiscard]] const std::vector<uint8_t>& GetText() const override;
  void SetText(const std::vector<uint8_t>& text) override;
  ~ChaCha20() override;

 private:
  void CreateInitVector();
  void ComputeInitVector();
};

}
#endif  // RAINTEXTCORE_CHACHA20_H
