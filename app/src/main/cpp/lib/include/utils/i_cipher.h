
#ifndef RAINTEXTCORE_I_CIPHER_H
#define RAINTEXTCORE_I_CIPHER_H

#include <iostream>
#include <vector>
#include <random>

namespace rain_text_core {


class ICipher {
 public:
  ICipher(uint8_t cypher_index, const std::vector<uint8_t>& key, const std::vector<uint8_t>& text)
      : cypher_index_(cypher_index), key_(key), text_(text) {}
  virtual void Encrypt(std::vector<uint8_t>& output) = 0;
  virtual void Decrypt(std::vector<uint8_t>& output) = 0;
  [[nodiscard]] virtual const std::vector<uint8_t>& GetKey() const = 0;
  virtual void SetKey(const std::vector<uint8_t>& key) = 0;
  [[nodiscard]] virtual const std::vector<uint8_t>& GetText() const = 0;
  virtual void SetText(const std::vector<uint8_t>& text) = 0;
  virtual ~ICipher() = default;
 protected:
  const uint8_t cypher_index_;
  std::vector<uint8_t> key_;
  std::vector<uint8_t> text_;
};

}

#endif  // RAINTEXTCORE_I_CIPHER_H
