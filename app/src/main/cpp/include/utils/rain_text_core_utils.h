//
// Created by kureii on 13.5.23.
//

#ifndef RAINTEXTCORE_RAIN_TEXT_CORE_UTILS_H
#define RAINTEXTCORE_RAIN_TEXT_CORE_UTILS_H

#include <iostream>
#include <vector>

namespace rain_text_core::rain_text_core_utils {

void SplitKey(uint16_t byte_size, std::vector<uint8_t>& key,
              std::vector<std::vector<uint8_t>>& output);

void GetIV(std::vector<std::vector<uint8_t>> &splited_keys, uint8_t *key_index, uint8_t *init_vector_index, uint8_t *pre_salt_index,uint8_t* iv, uint8_t iv_size, bool decrypt=false);

}

#endif  // RAINTEXTCORE_RAIN_TEXT_CORE_UTILS_H
