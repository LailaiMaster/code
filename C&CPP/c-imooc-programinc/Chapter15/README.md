# 慕课网《[C 语言体系化精讲](https://coding.imooc.com/class/463.html) 》 第十五章-综合案例：高仿Mac版计算器

本章程序仅支持 Windows + Mingw 环境【因为如果使用 msvc 的话，就要自己编译 gtk】。

**计算器效果**：

![image](../imgs/calculator_ui.png)

**计算器接口设计**：

核心思想，UI 与逻辑分离，只要定义好接口，就可以多人协作开发。

![image](../imgs/calculator_arch.png)
