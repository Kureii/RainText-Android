set(rain-text-core-core-headers
    ${CMAKE_CURRENT_SOURCE_DIR}/include/core/rain_text_core.h
)

set(rain-text-core-utils-headers
    ${CMAKE_CURRENT_SOURCE_DIR}/include/utils/i_cipher.h
    ${CMAKE_CURRENT_SOURCE_DIR}/include/utils/rain_text_core_utils.h
    ${CMAKE_CURRENT_SOURCE_DIR}/include/utils/cipher/aes.h
    ${CMAKE_CURRENT_SOURCE_DIR}/include/utils/cipher/chacha20.h
    ${CMAKE_CURRENT_SOURCE_DIR}/include/utils/cipher/twofish.h
)

set(rain-text-core-headers
    ${rain-text-core-core-headers}
    ${rain-text-core-utils-headers}
)