cmake_minimum_required(VERSION 3.2)

if(NOT EXISTS "$ENV{QNX_TARGET}" OR NOT EXISTS "$ENV{QNX_HOST}")
    message(FATAL_ERROR "QNX_TARGET or QNX_HOST environment variable isn't set.")
endif()


set(CMAKE_SYSTEM_NAME QNX)
set(arch gcc_ntoaarch64le)
set(QNX_ARCH "aarch64le")

set(CMAKE_C_COMPILER /home/yyatsenko/work/qnx700/host/linux/x86_64/usr/bin/qcc)
set(CMAKE_C_COMPILER_TARGET ${arch})
set(CMAKE_CXX_COMPILER /home/yyatsenko/work/qnx700/host/linux/x86_64/usr/bin/q++)

add_executable(server server.cpp)
target_link_libraries(server socket)