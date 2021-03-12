# 综合案例：跨平台下载软件

## 环境配置

1. 配置 MinGW（仅 Windows 用户需要）

由于本节需要使用 Conan 管理依赖，因此需要让 Conan 知道编译器的配置。我们需要在

```shell
<你的用户目录>/.conan/profiles/
```

这个路径下创建一个新文件，例如命名为 mingw64，添加以下内容：

```shell
MSYS2_ROOT=C:\msys64
MINGW64_ROOT=C:\msys64\mingw64

[settings]
os_build=Windows
os=Windows
arch=x86_64
arch_build=x86_64
compiler=gcc
compiler.version=9.2
compiler.exception=seh
compiler.libcxx=libstdc++11
compiler.threads=posix
build_type=Release

[env]
MSYS_ROOT=$MSYS2_ROOT\bin
MSYS_BIN=$MSYS2_ROOT\usr\bin
CONAN_CMAKE_GENERATOR="MinGW Makefiles"
CXX=$MINGW64_ROOT\bin\g++.exe
CC=$MINGW64_ROOT\bin\gcc.exe
CONAN_BASH_PATH=$MSYS2_ROOT\usr\bin\bash.exe
PATH=[$MSYS2_ROOT\usr\bin, $MINGW64_ROOT\bin]
```

注意 `MSYS2_ROOT` 和 `MINGW64_ROOT` 需要替换成你自己实际的安装路径。如果你安装时选择的是默认的路径，那么应该就不用修改。

2. 添加 bennyhuo 的 Conan 仓库。

由于本章用到了 `tinycthreadpool/1.0@bennyhuo/testing` 这个依赖，这个库是我专门为了课程讲解基于 [tinycthread](https://github.com/tinycthread/tinycthread) 和 [threadpool](https://github.com/mbrossard/threadpool) 定制而来的，定制之后的版本支持 Windows、macOS、Linux 等系统。

这个依赖通过 Conan 发布到了我个人的 Conan 仓库：https://api.bintray.com/conan/bennyhuo/conan-bennyhuo，因此大家需要在安装依赖之前添加这个仓库，执行下面的命令即可：

```shell
conan remote add bennyhuo https://api.bintray.com/conan/bennyhuo/conan-bennyhuo
conan remote add bincrafters https://api.bintray.com/conan/bincrafters/public-conan
```

安装成功后可以通过以下命令查看：

```shell
> conan remote list
conan-center: https://conan.bintray.com [Verify SSL: True]
bincrafters: https://api.bintray.com/conan/bincrafters/public-conan [Verify SSL: True]
bennyhuo: https://api.bintray.com/conan/bennyhuo/conan-bennyhuo [Verify SSL: True]
```

3. 安装 Conan 依赖。前面的步骤配置好之后，通过 Conan 命令安装依赖，安装方法我们在前面介绍 Conan 时已经讲过，这里稍微需要注意的是：Windows 上安装时需要指定 profile，增加参数 `--profile=mingw64`；安装时如果提示没有现成的包，可以增加参数 `--build=missing` 来解决。

4. curl 从 7.72 开始加入 zstd 编码模块，windows 上 mingw 不会自动引入这个依赖，通过 msys 安装：

```shell
pacman -S mingw-w64-x86_64-zstd
```

安装后通过 pkg-config 确认：

```shell
$pkg-config --list-all | grep zstd
libzstd               zstd - fast lossless compression algorithm library
```
