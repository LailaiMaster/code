#include <iostream>

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

using namespace std;

int main() {
    cout << av_version_info() << endl;
    return 0;
}
